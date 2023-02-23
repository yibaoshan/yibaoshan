package com.android.blackboard.ui.bitmap;

import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class VMRuntime {

    public static void clearGrowthLimit() {
        try {
            Class<?> vmRuntimeClass = Class.forName("dalvik.system.VMRuntime"); // 获取类

            Field instanceField = vmRuntimeClass.getDeclaredField("THE_ONE"); // 获取实例对象

            instanceField.setAccessible(true);

            Object vmRuntime = instanceField.get(null);

            Method growthLimitMethod = vmRuntimeClass.getMethod("clearGrowthLimit");

            growthLimitMethod.setAccessible(true);

            growthLimitMethod.invoke(vmRuntime, (Object) null);

        } catch (Throwable e) {
            Log.e("TAG", "reflect bootstrap failed:", e);
        }
    }

}
