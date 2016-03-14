#include <jni.h>

#include <sys/ioctl.h>
#include <fcntl.h>

#include <android/log.h>

#define TAG "dc_motor"
#define LOGD(msg,args...)__android_log_print(ANDROID_LOG_DEBUG,TAG,msg,##args)
#define LOGE(msg,args...)__android_log_print(ANDROID_LOG_ERROR,TAG,msg,##args)

int fd = -1;

JNIEXPORT void JNICALL Java_com_gec_smarthome_library_DcMotorDev_openDcMotor
(JNIEnv *env, jobject obj) {
	fd = open("dev/dc_motor",O_RDWR);
	if (fd < 0) {
		LOGE("Open dc_motor unsuccessfully.\n");
return;
	}
	LOGD( "Open dc_motor fd=%d successfully.\n", fd);
}

JNIEXPORT void JNICALL Java_com_gec_smarthome_library_DcMotorDev_ctrlDcMotor
(JNIEnv *env, jobject obj, jint pos) {
ioctl(fd,pos,0);
}

JNIEXPORT void JNICALL Java_com_gec_smarthome_library_DcMotorDev_closeDcMotor
(JNIEnv *env, jobject obj) {
close(fd);
LOGD("Close dc_motor successfully.\n");
}
