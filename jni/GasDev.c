#include <jni.h>

#include <pthread.h>
#include <fcntl.h>

#include <android/log.h>

#define TAG "gas"
#define LOGD(msg,args...)__android_log_print(ANDROID_LOG_DEBUG,TAG,msg,##args)
#define LOGE(msg,args...)__android_log_print(ANDROID_LOG_ERROR,TAG,msg,##args)

int fd = -1;
JavaVM *g_jvm = NULL;
jobject g_obj = NULL;
int isEnable = 0;

void *get_gas_main(void *arg) {
	char counter[30];
	int ret;
	int value;
	JNIEnv *env;
	jclass cls;
	jmethodID mid;
	char current_gas_value[] = { '0' };
	if ((*g_jvm)->AttachCurrentThread(g_jvm, &env, NULL) != JNI_OK) {
		return NULL;
	}
	//找到对应的类
	cls = (*env)->GetObjectClass(env, g_obj);
	while (isEnable) {
		value = -1;
		ret = read(fd, current_gas_value, sizeof(current_gas_value));
		if (current_gas_value[0] == '0') {
			value = 0;
		} else {
			value = 1;
		}
		jmethodID methodId = (*env)->GetMethodID(env, cls, "setOnDataListener",
				"(I)V");
		(*env)->CallVoidMethod(env, g_obj, methodId, value);
		current_gas_value[0] = '0';
	}
	(*g_jvm)->DetachCurrentThread(g_jvm);

}

JNIEXPORT void JNICALL Java_com_gec_smarthome_library_GasDev_openGas
(JNIEnv *env, jobject obj) {
	fd = open("/dev/gec_gas_drv", O_RDWR);
	if (fd < 0) {
			LOGE("Open gas unsuccessfully.\n");
			return;
		}
	LOGD( "Open gas fd=%d successfully.\n", fd);
	(*env)->GetJavaVM(env, &g_jvm);
	g_obj = (*env)->NewGlobalRef(env, obj);
}

JNIEXPORT void JNICALL Java_com_gec_smarthome_library_GasDev_startGetData
(JNIEnv *env, jobject obj) {
	if(fd < 0)
	return;
	isEnable = 1;
	pthread_t tid;
	pthread_create(&tid, NULL, get_gas_main, NULL);
}

JNIEXPORT void JNICALL Java_com_gec_smarthome_library_GasDev_stopGetData
(JNIEnv *env, jobject obj) {
	isEnable = 0;
}

JNIEXPORT void JNICALL Java_com_gec_smarthome_library_GasDev_closeGas
(JNIEnv *env, jobject obj) {
	isEnable = 0;
	close(fd);
	LOGD("Close gas successfully.\n");
}
