#include <jni.h>
#include <stdlib.h>
#include <string.h>

JNIEXPORT jlong JNICALL
Java_com_android_blackboard_JNIActivity_allocateMemory(JNIEnv *env, jobject thiz) {
    jlong allocatedMemorySize = 100 * 1024 * 1024;
    char *memory = (char *) malloc(allocatedMemorySize);
    if (memory == NULL) {
        // 内存分配失败
        return 0;
    }
    memset(memory, 'X', allocatedMemorySize);
    return allocatedMemorySize;
}