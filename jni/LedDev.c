#include <jni.h>

#include <sys/ioctl.h>
#include <fcntl.h>

#include <android/log.h>

#define TEST_MAGIC 'x'
#define LED1 _IO(TEST_MAGIC, 0)
#define LED2 _IO(TEST_MAGIC, 1)
#define LED3 _IO(TEST_MAGIC, 2)
#define LED4 _IO(TEST_MAGIC, 3)

#define TAG "led"
#define LOGD(msg,args...)__android_log_print(ANDROID_LOG_DEBUG,TAG,msg,##args)
#define LOGE(msg,args...)__android_log_print(ANDROID_LOG_ERROR,TAG,msg,##args)

int fd = -1;

JNIEXPORT void JNICALL Java_com_gec_smarthome_library_LedDev_openLed
(JNIEnv *env, jobject obj) {
	fd = open("/dev/Led", O_RDWR);
	if (fd < 0) {
		LOGE("Open led unsuccessfully.\n");
		return;
	}
	LOGD( "Open led fd=%d successfully.\n", fd);
}

JNIEXPORT void JNICALL Java_com_gec_smarthome_library_LedDev_ctrlLed
(JNIEnv *env, jobject obj, jint pos, jint status) {
switch(pos)
{
	case 1:
	ioctl(fd,LED1 ,status);
	break;
	case 2:
	ioctl(fd,LED2 ,status);
	break;
	case 3:
	ioctl(fd,LED3 ,status);
	break;
	case 4:
	ioctl(fd,LED4 ,status);
	break;
	default:
	ioctl(fd, pos, status);
	break;
}
}
JNIEXPORT void JNICALL Java_com_gec_smarthome_library_LedDev_closeLed
(JNIEnv *env, jobject obj) {
	close(fd);
	LOGD("Close led successfully.\n");
}
