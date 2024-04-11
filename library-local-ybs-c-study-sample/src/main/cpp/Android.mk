LOCAL_PATH := $(call my-dir)
LOCAL_LDLIBS := -llog

include $(CLEAR_VARS)

LOCAL_MODULE    := simple-lib
LOCAL_SRC_FILES := simple-lib.c

include $(BUILD_SHARED_LIBRARY)