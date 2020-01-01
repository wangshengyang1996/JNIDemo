//
// Created by Administrator on 2019/12/26.
//

#include <jni.h>
#include <android/log.h>
#include "abi/abi.h"

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,"reftest" ,__VA_ARGS__)


extern "C" JNIEXPORT void
JNICALL
Java_com_wsy_jnidemo_test_ReferenceTest_findClassWithoutDelete(
        JNIEnv *env,
        jclass) {
    for (int i = 0; i < 10000000; ++i) {
        jclass clz = env->FindClass("android/content/Context");
    }
}
extern "C" JNIEXPORT void
JNICALL
Java_com_wsy_jnidemo_test_ReferenceTest_findClassAndDelete(
        JNIEnv *env,
        jclass) {
    for (int i = 0; i < 10000000; ++i) {
        jclass clz = env->FindClass("android/content/Context");
        env->DeleteLocalRef(clz);
    }
}
extern "C" JNIEXPORT void
JNICALL
Java_com_wsy_jnidemo_test_ReferenceTest_createWeakGlobalRef(
        JNIEnv *env,
        jclass) {
        jobject weakGlobalRef = env->NewWeakGlobalRef(env->NewByteArray(1));
//        env->DeleteWeakGlobalRef(weakGlobalRef);

}

jbyteArray localRef = NULL;
extern "C" JNIEXPORT void
JNICALL
Java_com_wsy_jnidemo_test_ReferenceTest_createLocalRef(
        JNIEnv *env,
        jclass, jint count) {
    if (localRef != NULL) {
        env->GetObjectClass(localRef);
    }
    for (int i = 0; i < count; ++i) {
        if (localRef == NULL) {
            localRef = env->NewByteArray(1);
        }
        env->NewByteArray(1);
    }
}


extern "C" JNIEXPORT void
JNICALL
Java_com_wsy_jnidemo_test_ReferenceTest_createGlobalRef(
        JNIEnv *env,
        jclass) {
    jobject globalRef = env->NewGlobalRef(env->NewByteArray(1));
//    env->DeleteGlobalRef(globalRef);
}
extern "C" JNIEXPORT void
JNICALL
Java_com_wsy_jnidemo_test_ReferenceTest_nativeJudgeSameObject(
        JNIEnv *env,
        jclass clazz) {
    jmethodID mId = env->GetStaticMethodID(clazz, "returnObject", "()Ljava/lang/Object;");
    jobject obj1 = env->CallStaticObjectMethod(clazz, mId);
    jobject obj2 = env->CallStaticObjectMethod(clazz, mId);
    LOGI("obj1 addr = %p , obj2 addr = %p", obj1, obj2);
    LOGI("(obj1 == obj2) is %d", (obj1 == obj2));
    LOGI("(env->IsSameObject(obj1,obj2)) is %d", env->IsSameObject(obj1, obj2));
}