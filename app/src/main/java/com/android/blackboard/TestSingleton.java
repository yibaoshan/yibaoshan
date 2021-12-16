package com.android.blackboard;

/**
 * Created by yibaoshan@foxmail.com on 2021/12/16
 * Description : If you have any questions, please contact me
 */
public class TestSingleton {

    {
        System.err.println("TestSingleton Init");
    }

    private TestSingleton() {

    }

    public static String string = "string";

    private static TestSingleton singleton = new TestSingleton();

    public static TestSingleton getInstance() {
//        return Holder.singleton;
        return singleton;
    }

    private static class Holder{
        private static TestSingleton singleton = new TestSingleton();
    }

    public void doSomething() {

    }

}
