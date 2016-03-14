#include <jni.h>

#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

#include <android/log.h>

#define TAG "beep"
#define LOGD(msg,args...)__android_log_print(ANDROID_LOG_DEBUG,TAG,msg,##args)
#define LOGE(msg,args...)__android_log_print(ANDROID_LOG_ERROR,TAG,msg,##args)

int fd = -1;

JNIEXPORT void JNICALL Java_com_gec_smarthome_library_BeepDev_openPwm
(JNIEnv *env, jobject obj) {
	fd = open("/dev/beep",O_RDWR);
	if (fd < 0) {
			LOGE("Open beep unsuccessfully.\n");
			return;
		}
		LOGD( "Open beep fd=%d successfully.\n", fd);
}

JNIEXPORT void JNICALL Java_com_gec_smarthome_library_BeepDev_ctrlPwm
(JNIEnv *env, jobject obj, jint pos) {
	ioctl(fd,pos,1);
}

JNIEXPORT void JNICALL Java_com_gec_smarthome_library_BeepDev_closePwm
(JNIEnv *env, jobject obj) {
	close(fd);
	LOGD( "Close beep successfully.\n");
}
