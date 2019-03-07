#include <jni.h>
#include <android/log.h>

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,"wsy" ,__VA_ARGS__)


extern "C" JNIEXPORT jstring
JNICALL
Java_com_wsy_jnidemo_MainActivity_testExceptionNotCrash(
        JNIEnv *env,
        jobject /* this */, jint i) {
    jstring hello = env->NewStringUTF("hello world");
    if (i > 100) {
        jclass exceptionCls = env->FindClass("com/wsy/jnidemo/CustomException");
        env->ThrowNew(exceptionCls, "i must <= 100");
        env->DeleteLocalRef(exceptionCls);
    }
    //已出现异常，env已不可使用，但事先已创建好了String对象，因此能够return
    return hello;
}

extern "C" JNIEXPORT jstring
JNICALL
Java_com_wsy_jnidemo_MainActivity_testExceptionCrash(
        JNIEnv *env,
        jobject /* this */) {
    jclass exceptionCls = env->FindClass("com/wsy/jnidemo/CustomException");
    env->ThrowNew(exceptionCls, "i am an exception");
    env->DeleteLocalRef(exceptionCls);
    //若出现异常，则env已不可使用，创建String会crash
    return env->NewStringUTF("hello world, after exception");
}

extern "C" JNIEXPORT void
JNICALL
Java_com_wsy_jnidemo_MainActivity_testCallJava(
        JNIEnv *env,
        jobject /* this */, jobject activity) {
    jclass cls = env->GetObjectClass(activity);
    jfieldID codeId = env->GetFieldID(cls, "code", "I");
    jfieldID msgId = env->GetFieldID(cls, "msg", "Ljava/lang/String;");
    jint code = env->GetIntField(activity, codeId);
    //获取MainActivity中定义的code和msg，并以log打印
    jstring msg = (jstring) env->GetObjectField(activity, msgId);
    LOGI("code = %d,msg = %s", code, env->GetStringUTFChars(msg, JNI_FALSE));
    jmethodID callJavaMethodId = env->GetMethodID(cls, "cCallJava", "(Ljava/lang/String;)V");
    jstring nativeMsg = env->NewStringUTF("java method cCallJava called");
    //调用java中的cCallJava方法
    env->CallVoidMethod(activity, callJavaMethodId, nativeMsg);
    env->DeleteLocalRef(msg);
    env->DeleteLocalRef(nativeMsg);
    env->DeleteLocalRef(cls);
}

extern "C" JNIEXPORT void
JNICALL
Java_com_wsy_jnidemo_MainActivity_nativeShowToast(
        JNIEnv *env,
        jobject /* this */, jobject activity) {
    //找到Toast类
    jclass cls = env->FindClass("android/widget/Toast");
    //找到静态方法makeText
    jmethodID makeTextMethodId = env->GetStaticMethodID(cls, "makeText",
                                                        "(Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;");
    //找到非静态方法show
    jmethodID showMethodId = env->GetMethodID(cls, "show", "()V");
    //创建显示的内容
    jstring content = env->NewStringUTF("Toast.makeText.show called in native");
    //获取Toast展示的时长
    jfieldID lengthShortId = env->GetStaticFieldID(cls, "LENGTH_SHORT", "I");
    jint showLength = env->GetStaticIntField(cls, lengthShortId);
    //使用makeText方法创建toast对象
    jobject toastObj = env->CallStaticObjectMethod(cls, makeTextMethodId, activity, content,
                                                   lengthShortId);
    //调用toast方法的show方法展示内容
    env->CallVoidMethod(toastObj, showMethodId, showLength);

    env->DeleteLocalRef(content);
    env->DeleteLocalRef(cls);
}