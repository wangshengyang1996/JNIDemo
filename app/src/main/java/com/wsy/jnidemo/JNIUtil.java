package com.wsy.jnidemo;

import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.security.Policy;

public class JNIUtil {
    private static final String TAG = "JNIUtil";
    private static final String name = int.class.getName();
    private final String job = "aaa";
    private final int gender = 0;
    private final Integer liveness = 0;
    private final double aDouble = 666;

    public static String class2Signature(Class clazz) {
        if (clazz.isArray()) {
            return "[" + class2Signature(clazz.getComponentType());
        } else {
            if (clazz.isPrimitive()) {
                if (clazz == void.class) {
                    return "V";
                }
                if (clazz == boolean.class) {
                    return "Z";
                }
                if (clazz == byte.class) {
                    return "B";
                }
                if (clazz == char.class) {
                    return "C";
                }
                if (clazz == short.class) {
                    return "S";
                }
                if (clazz == int.class) {
                    return "I";
                }
                if (clazz == long.class) {
                    return "J";
                }
                if (clazz == float.class) {
                    return "F";
                }
                if (clazz == double.class) {
                    return "D";
                }
            } else {
                return "L" + clazz.getName().replace(".", "/") + ";";
            }
        }
        return null;
    }

    public static String generateClass2SignatureCode(Class clazz) {
        if (clazz.isArray()) {
            throw new IllegalArgumentException("class " + clazz.getName() + " is array!");
        } else {
            if (clazz.isPrimitive()) {
                if (clazz == void.class) {
                    return "V";
                }
                if (clazz == boolean.class) {
                    return "Z";
                }
                if (clazz == byte.class) {
                    return "B";
                }
                if (clazz == char.class) {
                    return "C";
                }
                if (clazz == short.class) {
                    return "S";
                }
                if (clazz == int.class) {
                    return "I";
                }
                if (clazz == long.class) {
                    return "J";
                }
                if (clazz == float.class) {
                    return "F";
                }
                if (clazz == double.class) {
                    return "D";
                }
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("jclass ")
                        .append(lowerFirst(clazz.getSimpleName()))
                        .append("Clazz = env->FindClass(\"")
                        .append(clazz.getName().replace(".", "/"))
                        .append("\");");

                return stringBuilder.toString();
            }
        }
        return null;
    }

    public static String upperFirst(String s) {
        char[] chars = s.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    public static String lowerFirst(String s) {
        char[] chars = s.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    public static String generateField2SignatureCode(Field field) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("jfieldID ").append(field.getName()).append("ID = ");
        boolean isStatic = Modifier.isStatic(field.getModifiers());

        stringBuilder.append(isStatic ? "env->GetStaticFieldId(" : "env->GetFieldId(");

        stringBuilder.append(lowerFirst(field.getDeclaringClass().getSimpleName()) + "clazz,");
        stringBuilder.append("\"").append(field.getName()).append("\"").append(",\"");
        stringBuilder.append(class2Signature(field.getType()));
        stringBuilder.append("\");");

        //换行
        stringBuilder.append("\n");

        //get操作
        stringBuilder.append(field.getName())
                .append("Value = ")
                .append(isStatic ? "env->GetStatic" : "env->Get")
                .append(field.getType().isPrimitive() ? upperFirst(field.getType().getName()) : "Object")
                .append("Field(")
                .append(field.getClass().getSimpleName())
                .append(isStatic ? "clazz," : "Obj,")
                .append(field.getName())
                .append("ID);");

        return stringBuilder.toString();
    }

    public static String generateMethod2SignatureCode(Method method) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("jmethodID ").append(method.getName()).append("ID = ");
        boolean isStatic = Modifier.isStatic(method.getModifiers());
        stringBuilder.append(isStatic ? "env->GetStaticMethodId(" : "env->GetMethodId(");
        stringBuilder.append(method.getDeclaringClass().getSimpleName()).append("clazz,");
        stringBuilder.append("\"").append(method.getName()).append("\"").append(",\"");

        Class[] parametersTypes = method.getParameterTypes();
        if (parametersTypes.length == 0) {
            stringBuilder.append("()");
        } else {
            stringBuilder.append("(");
            for (Class parameterType : parametersTypes) {
                stringBuilder.append(class2Signature(parameterType));
            }
            stringBuilder.append(")");
            Class<?> returnType = method.getReturnType();
            stringBuilder.append(class2Signature(returnType));
        }
        stringBuilder.append("\");");

        //换行
        stringBuilder.append("\n");

        //get操作
        if (method.getReturnType() != void.class) {
            stringBuilder
                    .append("j")
                    .append(method.getReturnType().isPrimitive() ? (method.getReturnType().getSimpleName()) :
                            (method.getReturnType() == String.class ? "string" : "object"))
                    .append(" ")
                    .append(method.getName())
                    .append("Value = ")
                    .append(method.getReturnType() == String.class ? "(jstring) " : "");
        }
        stringBuilder
                .append(isStatic ? "env->CallStatic" : "env->Call")
                .append(method.getReturnType().isPrimitive() ? upperFirst(method.getReturnType().getSimpleName()) : "Object")
                .append("Method(")
                .append(method.getClass().getSimpleName())
                .append(isStatic ? "clazz," : "Obj,")
                .append(method.getName()).append("ID");

        Parameter[] parameters = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            parameters = method.getParameters();
            if (parameters.length > 0) {
                Log.i(TAG, "generateMethod2SignatureCode: " + parameters[0].getName());
            }
            if (parameters.length != 0) {
                stringBuilder.append(",");
                for (int i = 0; i < parametersTypes.length; i++) {
                    stringBuilder.append(parameters[i].getName()).append("Value").append(",");
                }
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            }
        } else {
            if (parametersTypes.length != 0) {
                stringBuilder.append(",");
                for (int i = 0; i < parametersTypes.length; i++) {
                    stringBuilder.append("arg").append(i).append("Value").append(",");
                }
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            }
        }

        stringBuilder
                .append(");");

        return stringBuilder.toString();
    }


    public static void showDemo() {
        Method[] declaredMethods = JNIUtil.class.getDeclaredMethods();
        Log.i(TAG, "\n");
        for (Method method : declaredMethods) {
            Log.i(TAG, generateMethod2SignatureCode(method));
        }
        Log.i(TAG, "\n");
        Field[] declaredFields = JNIUtil.class.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            Log.i(TAG, generateField2SignatureCode(declaredField));
        }
        Log.i(TAG, "\n");
        declaredFields = TestClazz.class.getDeclaredFields();

        for (Field declaredField : declaredFields) {
            Log.i(TAG, generateField2SignatureCode(declaredField));
        }
        Log.i(TAG, "\n");
        Log.i(TAG, generateClass2SignatureCode(int.class));
        Log.i(TAG, generateClass2SignatureCode(TestClazz.class));
    }

    private static class TestClazz {
        int fieldA;
        static int staticFieldB;
    }
}
