package com.android.blackboard.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ThreadPoolExecutor {

    private static java.util.concurrent.ThreadPoolExecutor mThreadPoolExecutor;

    static {
        // 核心线程数量，这里获取的是 CPU 核心数
        int corePoolSize = Runtime.getRuntime().availableProcessors();
        // 最大线程数量，先取最大 fd 限制，再根据核心数
        int maximumPoolSize = getMaximumPoolSizeLimit(corePoolSize);
        // 空闲活跃时间，通常任务完成该丢弃就丢弃，不需要活跃
        int keepAliveTime = 0;
        // 单位，不用管，随便你
        TimeUnit unit = TimeUnit.SECONDS;
        // 线程池满世的阻塞队列，限制大小根据可用堆内存
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(getMaxQueueLimit());
        mThreadPoolExecutor = new java.util.concurrent.ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    private static int getMaximumPoolSizeLimit(int corePoolSize) {
        int max = 0;
        try {
            // 检查当前设备最大可用 fd 限制数量
            Process process = Runtime.getRuntime().exec("limit");
            // 对返回值进行筛选，逻辑省略。。
            InputStream inputStream = process.getInputStream();
//            max = inputStream.xxx
        } catch (IOException e) {
            e.printStackTrace();
        }

        return max == 0 ? corePoolSize * 10 : max;
    }

    private static int getMaxQueueLimit() {
        // 获取当前设备进程最大可用内存，如果最大可用 512MB，那么队列可以设置小一点，因为也是在堆内存
        long maxMemory = Runtime.getRuntime().maxMemory();
        return (int) (maxMemory / 2);
    }

    public static void execute(Runnable runnable) {
        mThreadPoolExecutor.execute(runnable);
    }

    public static Future<?> submit(Runnable runnable) {
        return mThreadPoolExecutor.submit(runnable);
    }


}
