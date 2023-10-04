package com.android.notebook.学习笔记.资深带你破解Android高级;

/**
 * author :Bob.
 * date : 2020/10/30
 * desc :
 */
public class Java_4_2_如何写出线程安全的程序 {


    /*
     * @ 线程安全是指可变资源
     * @
     * @
     */
    public void main(String[] args) {
//        ThreadLocal
//        WeakHashMap
    }

    public static class DCL {

        private volatile static DCL instance;

        private DCL() {
        }

        public static DCL getInstance() {
            if (instance == null) {
                synchronized (DCL.class) {
                    if (instance == null) instance = new DCL();
                }
            }
            return instance;
        }

    }

}
