package com.android.blackboard.ui.recyclerview.mcxtzhang;

import android.content.Context;
import android.util.TypedValue;

/**
 * author : xiaobao
 * e-mail : yibaoshan@foxmail.com
 * time   : 2023/04/12
 * desc   :
 */
public class CardConfig {

    //屏幕上最多同时显示几个Item
    public static int MAX_SHOW_COUNT;

    //每一级Scale相差0.05f，translationY相差7dp左右
    public static float SCALE_GAP;
    public static int TRANS_Y_GAP;

    public static void initConfig(Context context) {
        MAX_SHOW_COUNT = 2;
        SCALE_GAP = 0.05f;
        TRANS_Y_GAP = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, context.getResources().getDisplayMetrics());
    }

}
