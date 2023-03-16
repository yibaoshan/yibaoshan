package com.android.blog.informal.quitloop;

import android.os.Build;
import android.os.MessageQueue;

import androidx.annotation.RequiresApi;

import java.lang.reflect.Field;

/**
 * Created on 2022/12/6
 */
public class Quit {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void setQuitAllowed(MessageQueue messageQueue) {
        try {
            Field field = MessageQueue.class.getDeclaredField("mQuitAllowed");
            field.setAccessible(true);
            field.setBoolean(messageQueue, true);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }

}
