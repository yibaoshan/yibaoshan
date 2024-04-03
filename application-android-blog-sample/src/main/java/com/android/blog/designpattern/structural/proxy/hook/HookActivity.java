package com.android.blog.designpattern.structural.proxy.hook;

import android.app.Instrumentation;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HookActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        try {
            hook();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private void hook() throws InvocationTargetException, IllegalAccessException, NoSuchFieldException, ClassNotFoundException, NoSuchMethodException {
        Class<?> classes = Class.forName("android.app.ActivityThread");
        Method activityThread = classes.getDeclaredMethod("currentActivityThread");
        activityThread.setAccessible(true);
        Object currentThread = activityThread.invoke(null);
        Field instrumentationField = classes.getDeclaredField("mInstrumentation");
        instrumentationField.setAccessible(true);
        Instrumentation instrumentationInfo = (Instrumentation) instrumentationField.get(currentThread);
        InstrumentationProxy proxy = new InstrumentationProxy(instrumentationInfo);
        instrumentationField.set(currentThread, proxy);
    }

}
