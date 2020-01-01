package com.wsy.jnidemo.test;

public class ReverseTest {
    static {
        System.loadLibrary("reversetest");
    }
    public static native void reverseData();

    public static native void reverseJudge(int count);
}
