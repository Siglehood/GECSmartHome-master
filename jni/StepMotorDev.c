#include <jni.h>

#include <sys/ioctl.h>
#include <fcntl.h>

#include <android/log.h>

#define TAG "step_motor"
#define LOGD(msg,args...)__android_log_print(ANDROID_LOG_DEBUG,TAG,msg,##args)
#define LOGE(msg,args...)__android_log_print(ANDROID_LOG_ERROR,TAG,msg,##args)

int fd = -1;

JNIEXPORT void JNICALL Java_com_gec_smarthome_library_StepMotorDev_openStepMotor
(JNIEnv *env, jobject obj) {
	fd = open("/dev/stepmotor",O_RDWR);
	if (fd < 0) {
			LOGE("Open step_motor unsuccessfully.\n");
			return;
		}
		LOGD( "Open step_motor fd=%d successfully.\n", fd);
}

JNIEXPORT void JNICALL Java_com_gec_smarthome_library_StepMotorDev_ctrlStepMotor
(JNIEnv *env, jobject obj, jint pos) {
	ioctl(fd,pos,200);
}

JNIEXPORT void JNICALL Java_com_gec_smarthome_library_StepMotorDev_closeStepMotor
(JNIEnv *env, jobject obj) {
	close(fd);
	LOGD("Close step_motor successfully.\n");
}
