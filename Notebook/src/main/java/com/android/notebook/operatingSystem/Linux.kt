package com.android.notebook.operatingSystem

class Linux {

    /**
     *
     * */

    private class Thread {

        /**
         * 1. 主线程退出后进程会退出吗？进程内的子线程呢？
         *  进程内任何线程调用exit都会导致进程结束，pthread_exit则只会停止自己的线程，哪怕是主线程也是如此，系统会等到所有子线程都结束才会关闭进程
         * */

    }

    private class epoll {

        /**
         * epoll=event poll
         * 在为设计epoll或者select之前，I/O事件只有两种方式
         * 1. 阻塞I/O
         * 2. 轮询I/O
         * 这两种方式各有各的缺点
         * 阻塞式想要监听多个事件发生只能创建多线程或者多进程
         * 轮询式浪费CPU
         * 接下来就轮到select上场了，select设计思想是中间人代理类，想要监听I/O状态可以调用select
         * 没有事件发生时，select会阻塞线程，有事件发生就使用'无差别轮询'所有流，这样一个线程就可以监听多个I/O流
         * 但select也有个缺点，他不知道是哪个流发生的事件，每次会有O(n)的无差别轮询，同时处理的流越多时间越长
         * 接下来又轮到epoll登场了，当有某个流或同时有多个流发生事件时，epoll能够把哪个流发生什么样的I/O事件告诉我们
         * */

    }

}