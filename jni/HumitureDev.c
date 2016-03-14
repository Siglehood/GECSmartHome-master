#include <jni.h>

#include <fcntl.h>
#include <pthread.h>

#include <android/log.h>

#define TAG "humiture"
#define LOGD(msg,args...)__android_log_print(ANDROID_LOG_DEBUG,TAG,msg,##args)
#define LOGE(msg,args...)__android_log_print(ANDROID_LOG_ERROR,TAG,msg,##args)

int fd = -1;
JavaVM *g_jvm = NULL;
jobject g_obj = NULL;
int isEnable = 0;

void *get_data_main(void *arg) {
	char counter[30];
	int ret;
	unsigned short tempz = 0;
	unsigned short tempx = 0;
	unsigned short humidityz = 0;
	unsigned short humidityx = 0;
	unsigned long temperature = 0;
	JNIEnv *env;
	jclass cls;
	jmethodID mid;
	if ((*g_jvm)->AttachCurrentThread(g_jvm, &env, NULL) != JNI_OK) {
		return NULL;
	}
	//找到对应的类
	cls = (*env)->GetObjectClass(env, g_obj);
	while (isEnable) {
		ret = read(fd, &temperature, sizeof(temperature));
		tempz = (temperature & 0x0000ff00) >> 8; //温度整数
		tempx = (temperature & 0x000000ff); //温度小数
		humidityz = (temperature & 0xff000000) >> 24; //湿度整数
		humidityx = (temperature & 0x00ff0000) >> 16; //湿度小数
		jmethodID methodId = (*env)->GetMethodID(env, cls, "setOnDataListener",
				"(IIII)V");
		(*env)->CallVoidMethod(env, g_obj, methodId, tempz, tempx, humidityz,
				humidityx);
		LOGD("[1] --> temp = %d --> humidity = %d", tempz, humidityz);
		//睡眠1秒
		usleep(1 * 1000 * 1000);
	}
	(*g_jvm)->DetachCurrentThread(g_jvm);
}

JNIEXPORT void JNICALL Java_com_gec_smarthome_library_HumitureDev_openHumiture
  (JNIEnv *env, jobject obj) {
	fd = open("/dev/humidity", O_RDWR);
		if (fd < 0) {
			LOGE("Open humiture unsuccessfully.\n");
			return;
		}
		LOGD( "Open humiture fd=%d successfully.\n", fd);
	(*env)->GetJavaVM(env, &g_jvm);
	g_obj = (*env)->NewGlobalRef(env, obj);
}

JNIEXPORT void JNICALL Java_com_gec_smarthome_library_HumitureDev_startGetData
(JNIEnv *env, jobject obj) {
	if(fd < 0)
		return;
	isEnable = 1;
	pthread_t tid;
	pthread_create(&tid, NULL, get_data_main, NULL);
}

JNIEXPORT void JNICALL Java_com_gec_smarthome_library_HumitureDev_stopGetData
(JNIEnv *env, jobject obj) {
	isEnable = 0;
}

JNIEXPORT void JNICALL Java_com_gec_smarthome_library_HumitureDev_closeHumiture
(JNIEnv *env, jobject obj) {
	isEnable = 0;
	close(fd);
	LOGD("Close humiture successfully.\n");
}
