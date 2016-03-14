LOCAL_PATH:=$(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE:=led
LOCAL_SRC_FILES:=LedDev.c
LOCAL_LDLIBS += -L$(SYSROOT)/usr/lib -llog
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE:=dc_motor
LOCAL_SRC_FILES:=DcMotorDev.c
LOCAL_LDLIBS += -L$(SYSROOT)/usr/lib -llog
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE:=step_motor
LOCAL_SRC_FILES:=StepMotorDev.c
LOCAL_LDLIBS += -L$(SYSROOT)/usr/lib -llog
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE:=beep
LOCAL_SRC_FILES:=BeepDev.c
LOCAL_LDLIBS += -L$(SYSROOT)/usr/lib -llog
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE:=gas
LOCAL_SRC_FILES:=GasDev.c
LOCAL_LDLIBS += -L$(SYSROOT)/usr/lib -llog
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE:=humiture
LOCAL_SRC_FILES:=HumitureDev.c
LOCAL_LDLIBS += -L$(SYSROOT)/usr/lib -llog
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE:=serial_port
LOCAL_SRC_FILES:=SerialPort.c
LOCAL_LDLIBS += -L$(SYSROOT)/usr/lib -llog
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE:=rfid
LOCAL_SRC_FILES:=RfidDev.c
LOCAL_LDLIBS += -L$(SYSROOT)/usr/lib -llog
LOCAL_LDFLAGS += -fuse-ld=bfd
include $(BUILD_SHARED_LIBRARY)
