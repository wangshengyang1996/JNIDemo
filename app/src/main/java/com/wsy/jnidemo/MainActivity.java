package com.wsy.jnidemo;

import android.app.Activity;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    //C层将获取此变量，勿删
    private int code = 10;
    //C层将获取此变量，勿删
    private String msg = "hello world";

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        File dir = new File(getApplicationInfo().nativeLibraryDir);
        Log.i(TAG, "onCreate: " + dir.getParentFile().listFiles().length);
        for (File file : dir.listFiles()) {
            Log.i(TAG, "onCreate: " + file.getAbsolutePath());
        }
    }

    //在一定情况下一定会crash
    public native String testExceptionCrash() throws CustomException;

    //不会crash，可能会报exception
    public native String testExceptionNotCrash(int i) throws CustomException;

    //在jni中调用java方法显示toast
    public native void nativeShowToast(Activity activity);

    //在native层调用java方法
    public native void testCallJava(MainActivity activity);

    //查看第二个参数的类型（Java方法声明为非静态）
    public native String getJobjectClassNotStatic();

    //查看第二个参数的类型（Java方法声明为静态）
    public static native String getJobjectClassStatic();


    //不存在的native方法
    public native void methodNotExists();

    public void nativeThrowException(View view) {
        int count = new Random().nextBoolean() ? 1000 : 100;
        try {
            Toast.makeText(this, testExceptionNotCrash(count), Toast.LENGTH_SHORT).show();
        } catch (CustomException e) {
            Toast.makeText(this, "caught an exception from c:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    //C层将调用此方法，勿删
    public void cCallJava(String str) {

        Log.i(TAG, "cCallJava: " + str);
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    /**
     * 在native调用Java的方法
     *
     * @param view
     */
    public void callJavaFromC(View view) {
        testCallJava(this);
    }

    /**
     * 在C中获取Toast类和show方法显示内容
     *
     * @param view
     */
    public void nativeShowToast(View view) {
        nativeShowToast(this);
    }

    /**
     * 调用java中声明了但是native不存在的方法
     *
     * @param view
     */
    public void callMethodNotExists(View view) {
        methodNotExists();
    }

    /**
     * 错误示范，在native层抛出exception后仍然使用JNIEnv变量导致crash
     *
     * @param view
     */
    public void wrongSampleUsingJNIEnv(View view) {
        try {
            Toast.makeText(this, testExceptionCrash(), Toast.LENGTH_SHORT).show();
        } catch (CustomException e) {
            e.printStackTrace();
        }
    }

    /**
     * {@link #getJobjectClassNotStatic }和{@link #getJobjectClassStatic } 调用的native方法内容一样，
     * 只是在java层声明一个是静态方法一个是成员方法，查看两者自动生成的第二个参数(jobject)的类型）
     *
     * 对于静态方法，jobject对象是调用native方法的java class，
     * 对于成员方法，jobject对象是调用native方法的java object
     *
     * @param view
     */
    public void showClassName(View view) {
        Toast.makeText(this, "static:\n" + getJobjectClassStatic() + "\n\nnot static:\n" + getJobjectClassNotStatic(), Toast.LENGTH_SHORT).show();
    }
}
