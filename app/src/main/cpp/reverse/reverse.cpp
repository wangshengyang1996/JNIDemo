//
// Created by Administrator on 2019/12/26.
//

#include <jni.h>
#include <android/log.h>
#include "../abi/abi.h"

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,"reservetest" ,__VA_ARGS__)

extern "C" JNIEXPORT void
JNICALL
Java_com_wsy_jnidemo_test_ReverseTest_reverseData(
        JNIEnv
        *env,
        jclass) {
    const char *content = "content";
    LOGI("content is: %s", content);
}
extern "C" JNIEXPORT void
JNICALL
Java_com_wsy_jnidemo_test_ReverseTest_reverseJudge(
        JNIEnv
        *env,
        jclass,
        jint count
) {
    if (count > 1000) {
        LOGI("count is %d, count > 1000", count);
    } else {
        LOGI("count is %d, count <= 1000", count);
    }
}