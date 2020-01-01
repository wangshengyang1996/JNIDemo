package com.wsy.jnidemo.test;

public class ReferenceTest {
    static {
        System.loadLibrary("reftest");
    }
    // 不断持有引用且不删除，使内部引用表溢出
    public static native  void findClassWithoutDelete();

    // 不断持有引用但是删除，内部引用表不会溢出
    public static  native void findClassAndDelete();

    public static  native void createLocalRef(int count);

    public static  native void createWeakGlobalRef();

    public static  native void createGlobalRef();

    public static  native void nativeJudgeSameObject();

    static Object returnObject(){
        return ReferenceTest.class;
    }

}
