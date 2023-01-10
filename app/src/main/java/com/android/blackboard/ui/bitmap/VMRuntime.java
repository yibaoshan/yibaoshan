package com.android.blackboard.ui.bitmap;

import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class VMRuntime {

    public static void clearGrowthLimit() {
        try {
            Class<?> vmRuntimeClass = Class.forName("dalvik.system.VMRuntime");

            Field gDefaultField = vmRuntimeClass.getDeclaredField("THE_ONE");
            gDefaultField.setAccessible(true);

            Object gDefaultObj = gDefaultField.get(null);

            Method growthLimit = vmRuntimeClass.getMethod("clearGrowthLimit");

            growthLimit.setAccessible(true);
            growthLimit.invoke(gDefaultObj, null);

        } catch (Throwable e) {
            Log.e("TAG", "reflect bootstrap failed:", e);
        }
    }

}
