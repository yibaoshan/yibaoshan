#include <jni.h>
#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <math.h>
#include <android/log.h>

#define  LOG_TAG    "ybs"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

JNIEXPORT jstring JNICALL
Java_cn_ybs_c_NativeLib_callJNI(JNIEnv *env, jobject thiz) {
    LOGE("callJNI");
    return 0;
}

