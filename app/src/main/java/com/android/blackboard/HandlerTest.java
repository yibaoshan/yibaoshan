package com.android.blackboard;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import java.io.FileDescriptor;

/**
 * Created by yibaoshan@foxmail.com on 2022/3/30
 * Description : If you have any questions, please contact me
 */
public class HandlerTest {

    public static void create(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Handler handler = Handler.createAsync(Looper.getMainLooper());
//            handler.getLooper().getQueue().addOnFileDescriptorEventListener();
        }

    }

}
