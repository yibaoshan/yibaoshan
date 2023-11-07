package com.android.notebook.学习笔记.资深带你破解Android高级;

/**
 * date : 2020/10/29
 */
class Java_4_1_如何停止一个线程 {

    public void main(String[] args) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    //休眠必须要加异常捕获是因为在调用interrupt的时候会触发异常
                    e.printStackTrace();
                }
            }
        });
        //调用自己非静态方法，通知当前线程可以中断了
        thread.interrupt();

        //调用自己非静态方法，获取当前线程有没有被中断
        thread.isInterrupted();
        //调用当前线程的静态方法，获取当前中断状态，并清空状态
        //简单来讲，第二次调用必为false
        Thread.interrupted();

        //下面来看看native层源码
        //java_lang_thread.cc
        /*
         *
         * thread.cc
         *
         * bool interrupt
         *
         * isInterrupted{
         *    return interrupt
         * }
         *
         * interrupted{
         *   bool temp = interrupt
         *   interrupt = false
         *   return temp
         * }
         *
         */

    }

}
