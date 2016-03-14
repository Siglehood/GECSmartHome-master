#include <jni.h>
#include <stdio.h>
#include <fcntl.h>
#include <unistd.h>
#include <termios.h>
#include <sys/types.h>
#include <sys/select.h>
#include <stdlib.h>
#include <unistd.h>
#include <netdb.h>
#include <string.h>
#include <errno.h>
#include <time.h>
#include <pthread.h>

#include <android/log.h>

#define TAG "rfid"
#define LOGD(msg,args...)__android_log_print(ANDROID_LOG_DEBUG,TAG,msg,##args)
#define LOGE(msg,args...)__android_log_print(ANDROID_LOG_ERROR,TAG,msg,##args)

#define DEV_PATH   "/dev/ttySAC1"
//#define DEV_PATH  "/dev/s3c2410_serial1"

volatile unsigned int cardid;
static struct timeval timeout;

int fd = -1;

//JNIEnv *g_env;(不能用于全局)
JavaVM *g_vm = NULL;
jobject g_obj = NULL;
int enableRfid = 0;

/* 设置窗口参数：9600速率 */
void init_tty(int fd) {
	//声明设置串口的结构体
	struct termios termios_new;
	//先清空该结构体
	bzero(&termios_new, sizeof(termios_new));
	//cfmakeraw()设置最终属性，就是设置termios结构体的各个参数
//	cfmakeraw(&termios_new);
	//设置波特率
//	termios_new.c_cflag(B9600);
//	cfsetispeed(&termios_new, B9600);
//	cfsetospeed(&termios_new, B9600);
	//CLOCAL和CREAD分别用于本地连接和接受使能，因此，首先先通过位掩码的方式激活这两个选项
	termios_new.c_cflag |= CLOCAL | CREAD;
	//通过掩码设置数据位为8位
	termios_new.c_cflag &= ~CSIZE;
	termios_new.c_cflag |= CS8;
	//设置无奇偶数效验
	termios_new.c_cflag &= ~PARENB;
	//一位停止位
	termios_new.c_cflag &= ~CSTOPB;
	//原始输入根本不会被处理。输入字符只是被原封不动地接收。一般情况下，如果要使用原始输入模式，程序中需要去掉ICANON，ECHO，ECHOE和ISIG选项
	//termios_new.c_lflag &= ~(ICANON | ECHO | ECHOE | ISIG);
//	tcflush(fd, TCIFLUSH);
	//可设置接收字符和等待时间，无特殊要求可以将其设置为0
	termios_new.c_cc[VTIME] = 10;
	termios_new.c_cc[VMIN] = 1;
	//用于清空输入、输出缓冲区
//	tcflush(fd, TCIFLUSH);
	//完成配置后，可以使用一下函数激活串口设置
//	if (tcsetattr(fd, TCSANOW, &termios_new))
//		LOGE("Setting the serial1 failed!\n");

}

/*计算校验和*/
unsigned char CalBCC(unsigned char *buf, int n) {
	int i;
	unsigned char bcc = 0;
	for (i = 0; i < n; i++) {
		bcc ^= *(buf + i);
	}
	return (~bcc);
}

int PiccRequest(int fd) {
	unsigned char WBuf[128], RBuf[128];
	int ret, i, len;
	fd_set rdfd;

	memset(WBuf, 0, 128);
	memset(RBuf, 1, 128);
	WBuf[0] = 0x07; //帧长=7Byte
	WBuf[1] = 0x02; //包号=0，命令类型=0x01
	WBuf[2] = 0x41; //命令='C'
	WBuf[3] = 0x01; //信息长度=0
	WBuf[4] = 0x52; //请求模式：ALL=0X52
	WBuf[5] = CalBCC(WBuf, WBuf[0] - 2); //校验和
	WBuf[6] = 0x03; //结束标志

	FD_ZERO(&rdfd);
	FD_SET(fd, &rdfd);
	write(fd, WBuf, 7);
	ret = select(fd + 1, &rdfd, NULL, NULL, &timeout);
	switch (ret) {
	case -1:
		LOGE("Select error.\n");
		break;
	case 0:
		LOGE("Request timed out.\n");
		break;
	default:
		//睡眠10毫秒
		usleep(10 * 1000);
		ret = read(fd, RBuf, 8);
		len = ret;
		ret = read(fd, RBuf + len, 8);
		len += ret;
		for (i = 0; i < ret; i++)
			LOGE("request RBuf[%d]=%x\n", i, RBuf[i]);
		if (len < 0) {
//			LOGE("len = %d, %m\n", len, errno);
			break;
		}
		if (RBuf[2] == 0x00) //应答帧状态部分为0则请求成功
				{
			return 0;
		}
		break;
	}
	return -1;
}

/*防碰撞，获取范围内最大的ID*/
int PiccAnticoll(int fd) {
	unsigned char WBuf[128], RBuf[128];
	int ret, i, len;
	fd_set rdfd;

	memset(WBuf, 0, 128);
	memset(RBuf, 0, 128);

	WBuf[0] = 0x08; //帧长= 8Byte
	WBuf[1] = 0x02; //包号=0，命令类型=0x01
	WBuf[2] = 0x42; //命令=‘B’
	WBuf[3] = 0x02; //信息长度=2
	WBuf[4] = 0x93; //防碰撞0x93  一级防碰撞
	WBuf[5] = 0x00; //位计数0
	WBuf[6] = CalBCC(WBuf, WBuf[0] - 2); //校验和
	WBuf[7] = 0x03; //结束标志

//	tcflush(fd, TCIFLUSH);
	FD_ZERO(&rdfd);
	FD_SET(fd, &rdfd);
	write(fd, WBuf, 8);

	/*
	 如果你在原数据模式(raw data mode)操作端口的话，每个read(2)系统调用都会返回从串口输入
	 缓冲区中实际得到的个数。在不能得到数据的情况下，read(2)系统调用就会一直等着，直到有端
	 口上新的字符可以读取或者发生超时或者错误的情况发生。如果需要read(2)函数迅速返回的话，
	 你可以使用下面的这个方式：
	 */
	//fcntl(fd,F_SETFL,FNDELAY);
	ret = select(fd + 1, &rdfd, NULL, NULL, &timeout);
	switch (ret) {
	case -1:
		LOGE("Select error.\n");
		break;
	case 0:
		LOGE("Timeout.");
		break;

	default:
		usleep(10 * 1000);
		ret = read(fd, RBuf, 10);
		len = ret;
		ret = read(fd, RBuf + len, 10);
		len += ret;

//		for (i = 0; i < ret; i++)
//			LOGE("anticoll RBuf[%d]=%x\n", i, RBuf[i]);

		if (len < 0) {
//			LOGE("len = %d, %m\n", len, errno);
			break;
		}

		if (RBuf[2] == 0x00) //应答帧状态部分为0 则获取ID，成功
				{
			cardid = (RBuf[4] << 24) | (RBuf[5] << 16) | (RBuf[6] << 8)
					| RBuf[7];
			return 0;
		}
	}
	return -1;
}

void *main_thread(void* arg) {
	JNIEnv *g_env;
	char buf[15];
	jstring str;
	//Attach主线程
	if ((*g_vm)->AttachCurrentThread(g_vm, &g_env, NULL) != JNI_OK) {
		return NULL;
	}
	jclass jclassObj = (*g_env)->GetObjectClass(g_env, g_obj);

	jmethodID methodId = (*g_env)->GetMethodID(g_env, jclassObj,
			"setOnDataListener", "(Ljava/lang/String;)V");

	while (enableRfid) {
		sleep(1);
		/*初始化串口*/
		init_tty(fd);
		timeout.tv_sec = 1;
		timeout.tv_usec = 0;
		/*请求天线范围的卡*/
		if (PiccRequest(fd)) {
			LOGE("The request failed!\n");
//			continue;
		}
		/*进行防碰撞，获取天线范围内最大的ID*/
		if (PiccAnticoll(fd)) {
			LOGE("Couldn't get card-id!\n");
//			continue;
		}
//		LOGD("card ID = %ld\n", cardid);
		sprintf(buf, "%u", cardid);
		str = (*g_env)->NewStringUTF(g_env, (char *) buf);
		LOGD("card ID = %s\n", buf);
		(*g_env)->CallVoidMethod(g_env, g_obj, methodId, str);
		//睡眠200毫秒
		usleep(200 * 1000);
		cardid = 0;
	}
	(*g_vm)->DetachCurrentThread(g_vm);
	return (void*) 0;
}

JNIEXPORT void JNICALL Java_com_gec_smarthome_library_RfidDev_openRfid
(JNIEnv *env, jobject obj) {
	 //O_RDWR|O_NOCTTY|O_NDELAY设置非阻塞，没有数据的时候就返回
		 fd=open(DEV_PATH,O_RDWR|O_NOCTTY|O_NONBLOCK);
	 if(fd<0) {
			 LOGE("Open Gec5260_ttySAC1 unsuccessfully!\n");
			 return;
		}
		 LOGD( "Open Gec5260_ttySAC1 fd=%d successfully.\n", fd);
		 /*初始化串口*/
	init_tty(fd);
	//保存全局JVM以便在子线程中使用
	(*env)->GetJavaVM(env,&g_vm);
	//不能直接赋值 (g_obj=obj)
	g_obj = (*env)->NewGlobalRef(env,obj);
}

JNIEXPORT void JNICALL Java_com_gec_smarthome_library_RfidDev_startGetData
(JNIEnv *env, jobject obj) {
	if(fd < 0)
		return;
	enableRfid = 1;
	pthread_t pt;
	pthread_create(&pt,NULL,main_thread,NULL);
}

JNIEXPORT void JNICALL Java_com_gec_smarthome_library_RfidDev_stopGetData
(JNIEnv *env, jobject obj ) {
	enableRfid=0;
}

JNIEXPORT void JNICALL Java_com_gec_smarthome_library_RfidDev_closeRfid
(JNIEnv *env, jobject obj) {
	enableRfid=0;
	close(fd);
	LOGD("Close Gec5260_ttySAC1 successfully.\n");
}
