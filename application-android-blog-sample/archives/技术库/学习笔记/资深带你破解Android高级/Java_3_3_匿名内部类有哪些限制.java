package com.android.notebook.学习笔记.资深带你破解Android高级;


/**
 * date : 2020/10/27
 * desc : javac xxx.java
 * javap -c xxx.class
 *
 * 内部类的构造方法是由编译器决定的
 * 非静态方法内部类，构造方法会传入当前类的引用
 * 静态方法，构造方法为无参方法
 */
public class Java_3_3_匿名内部类有哪些限制 {

    private String publicStr = "external";

    public void main() {

        class MethodInsideClass implements Runnable {
            @Override
            public void run() {
                System.out.println("MethodInsideClass");
            }
        }

        MethodInsideClass methodInsideClass = new MethodInsideClass() {
            @Override
            public void run() {
                super.run();
                System.out.println("inside");
            }
        };

        methodInsideClass.run();

        InsideClass insideClass = new InsideClass() {
            @Override
            public void run() {
                super.run();
                System.out.println("inside");
            }
        };
        insideClass.run();
    }

    public static void main2() {

        final Object main2Str = new Object();

        class MethodInsideClass implements Runnable {
            @Override
            public void run() {
                System.out.println("MethodInsideClass");
            }
        }

        MethodInsideClass methodInsideClass = new MethodInsideClass() {
            @Override
            public void run() {
                super.run();
                System.out.println("inside"+main2Str.toString());
            }
        };

        methodInsideClass.run();

    }

    class InsideClass extends Exception implements Runnable {

        @Override
        public void run() {
            System.out.println("InsideClass");
        }
    }

}
