package com.android.designpattern.创建型.单例模式;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    /**
     * 保证在同一进程中一个类仅有一个实例，并提供一个访问它的全局访问点。
     * <p>
     * 克隆/反射/序列化
     */

    @Test
    public void main() {
        testSingleton1();
        testSingleton2();
        testSingleton3();
    }

    /*1.恶汉模式*/
    private static void testSingleton1() {
        Object fromInstance1 =  Singleton1.getInstance();
        Object fromInstance2 =  Singleton1.getInstance();
        Object fromReflect = createClassWithReflect(Singleton1.class);
        System.out.println(fromInstance1.equals(fromInstance2));
        System.out.println(fromInstance1.equals(fromReflect));
    }

    /*2.懒汉模式-静态内部类*/
    private static void testSingleton2() {
        Object fromInstance1 =  Singleton2.getInstance();
        Object fromInstance2 =  Singleton2.getInstance();
        Object fromReflect = createClassWithReflect(Singleton2.class);
        System.out.println(fromInstance1.equals(fromInstance2));
        System.out.println(fromInstance1.equals(fromReflect));
    }

    /*3.懒汉模式-DoubleCheckLock*/
    private static void testSingleton3() {
        Object fromInstance1 =  Singleton3.getInstance();
        Object fromInstance2 =  Singleton3.getInstance();
        Object fromReflect = createClassWithReflect(Singleton3.class);
        System.out.println(fromInstance1.equals(fromInstance2));
        System.out.println(fromInstance1.equals(fromReflect));
    }

    /*4.枚举类*/
    private static void testSingleton4() {
        Singleton4.getInstance.print();
    }

    private static <T> T createClassWithReflect(Class<T> clz) {
        try {
            Constructor<?> constructor = clz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return (T) constructor.newInstance();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }



    private void testJavaHiddenApi() {
        try {
            Class reflectionHelperClz = Class.forName("com.example.support_p.ReflectionHelper");
            Class classClz = Class.class;
            Field classLoaderField = classClz.getDeclaredField("classLoader");
            classLoaderField.setAccessible(true);
            classLoaderField.set(reflectionHelperClz, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static class Run implements Runnable {

        @Override
        public void run() {
//            testSingleton1();
//            testSingleton2();
//            testSingleton3();
            testSingleton4();
        }


    }

}
