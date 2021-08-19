package com.android.notebook.operatingSystem

class Linux {

    /**
     *
     * */

    private class Thread{

        /**
         * 1. 主线程退出后进程会退出吗？进程内的子线程呢？
         *  进程内任何线程调用exit都会导致进程结束，pthread_exit则只会停止自己的线程，哪怕是主线程也是如此，系统会等到所有子线程都结束才会关闭进程
         * */

    }

}