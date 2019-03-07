package com.wsy.jnidemo;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
    }

    //在一定情况下一定会crash
    public native String testExceptionCrash() throws CustomException;

    //不会crash，可能会报exception
    public native String testExceptionNotCrash(int i) throws CustomException;

    //在jni中调用java方法显示toast
    public native void nativeShowToast(Activity activity);

    //在native层调用java方法
    public native void testCallJava(MainActivity activity);

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

    public void callJavaFromC(View view) {
        testCallJava(this);
    }
    public void nativeShowToast(View view) {
        nativeShowToast(this);
    }

    public void callMethodNotExists(View view) {
        methodNotExists();
    }

    public void wrongSampleUsingJNIEnv(View view) {
        try {
            Toast.makeText(this, testExceptionCrash(), Toast.LENGTH_SHORT).show();
        } catch (CustomException e) {
            e.printStackTrace();
        }
    }
}
