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
    //若出现异常，则env已不可使用一些方法，例如创建String将会crash，文档说明：https://developer.android.google.cn/training/articles/perf-jni#exceptions_1
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
    //若出现异常，则env已不可使用一些方法，例如创建String将会crash，文档说明：https://developer.android.google.cn/training/articles/perf-jni#exceptions_1
    return env->NewStringUTF("hello world, after exception");
}

extern "C" JNIEXPORT jstring
JNICALL
Java_com_wsy_jnidemo_MainActivity_getJobjectClassNotStatic(
        JNIEnv *env,
        jobject obj) {
    jclass cls = env->GetObjectClass(obj);
    jmethodID toStringMethod = env->GetMethodID(cls, "toString", "()Ljava/lang/String;");
    jstring def = static_cast<jstring>(env->CallObjectMethod(cls, toStringMethod));
    env->DeleteLocalRef(cls);
    return def;
}
extern "C" JNIEXPORT jstring
JNICALL
Java_com_wsy_jnidemo_MainActivity_getJobjectClassStatic(
        JNIEnv *env,
        jobject obj) {
    jclass cls = env->GetObjectClass(obj);
    jmethodID toStringMethod = env->GetMethodID(cls, "toString", "()Ljava/lang/String;");
    jstring def = static_cast<jstring>(env->CallObjectMethod(cls, toStringMethod));
    env->DeleteLocalRef(cls);
    return def;
}

extern "C" JNIEXPORT void
JNICALL
Java_com_wsy_jnidemo_MainActivity_testCallJava(
        JNIEnv *env,
        jobject /* this */, jobject activity) {
    // 第二个参数this被省略了，实际上，这个参数也是activity，但是在这里我们手动传入演示

    // 首先获取MainActivity的类
    jclass cls = env->GetObjectClass(activity);
    // 获取这个类中的code成员变量ID
    jfieldID codeId = env->GetFieldID(cls, "code", "I");
    // 获取这个类中的msg成员变量ID
    jfieldID msgId = env->GetFieldID(cls, "msg", "Ljava/lang/String;");

    // 获取code成员变量的值
    jint code = env->GetIntField(activity, codeId);

    // 获取msg成员变量的值
    jstring msg = (jstring) env->GetObjectField(activity, msgId);

    // 获取java.lang.String对象中的内容
    const char *cMsg = env->GetStringUTFChars(msg, JNI_FALSE);
    // 打印日志
    LOGI("code = %d,msg = %s", code, cMsg);
    // 用完String后要释放
    env->ReleaseStringUTFChars(msg, cMsg);

    // 找到MainActivity类中的cCallJava函数
    jmethodID callJavaMethodId = env->GetMethodID(cls, "cCallJava", "(Ljava/lang/String;)V");
    // 创建一个java.lang.String对象，内容如下
    jstring nativeMsg = env->NewStringUTF("java method cCallJava called");
    // 调用java中的cCallJava方法
    env->CallVoidMethod(activity, callJavaMethodId, nativeMsg);

    // 这里的DeleteLocalRef可以不执行，在函数执行完毕后会自动释放，但是在循环次数较多的循环中需要Delete，否则可能会溢出
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

jstring dynamicRegister(JNIEnv *jniEnv, jobject obj) {
    return jniEnv->NewStringUTF("dynamicRegister");
}

int JNI_OnLoad(JavaVM *javaVM, void *reserved) {
    JNIEnv *jniEnv;
    if (JNI_OK == javaVM->GetEnv((void **) (&jniEnv), JNI_VERSION_1_4)) {
        // 动态注册的Java函数所在的类
        jclass registerClass = jniEnv->FindClass("com/wsy/jnidemo/MainActivity");
        JNINativeMethod jniNativeMethods[] = {
                //3个参数分别为 Java函数的名称，Java函数的签名（不带函数名），本地函数指针
                {"dynamicRegister", "()Ljava/lang/String;", (void *) (dynamicRegister)}
        };
        if (jniEnv->RegisterNatives(registerClass, jniNativeMethods,
                                    sizeof(jniNativeMethods) / sizeof((jniNativeMethods)[0])) < 0) {
            return JNI_ERR;
        }
    }
    return JNI_VERSION_1_4;
}
