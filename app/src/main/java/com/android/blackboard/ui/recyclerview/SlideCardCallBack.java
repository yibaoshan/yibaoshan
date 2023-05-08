package com.android.blackboard.ui.recyclerview;

import android.graphics.Canvas;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * author : xiaobao
 * e-mail : yibaoshan@foxmail.com
 * time   : 2023/04/12
 * desc   :
 */
public class SlideCardCallBack extends ItemTouchHelper.SimpleCallback {
    private RecyclerView.Adapter adapter;
    private ArrayList datas;
    private final float SCALE_RATIO = 0.08f;
    private final float TRANSITION_Y = 30f;

    /**
     * 构造方法设置可滑动方向，上下左右 0x1111，参数一：拖拽  参数er：滑动
     * public static final int UP = 1;
     * public static final int DOWN = 1 << 1;
     * public static final int LEFT = 1 << 2;
     * public static final int RIGHT = 1 << 3;
     */
    public SlideCardCallBack(RecyclerView.Adapter adapter, ArrayList datas) {
        super(0, 15); //15表示支持各个方向拖拽
        this.adapter = adapter;
        this.datas = datas;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    //滑动动画结束后的操作
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        //这里需要将最顶层滑出去的data移除，然后添加到最底层，保证切换能循环进行
        Object remove = datas.remove(viewHolder.getLayoutPosition());
        datas.add(0, remove);
        adapter.notifyDataSetChanged();
    }

    //绘制拖拽过程中方法的效果
    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

        //dx  dy分别表示当前拖拽view相对于原来横纵坐标偏移
        double distance = Math.sqrt(dX * dX + dY * dY);
        double maxDistance = recyclerView.getWidth() * 0.5f;
        double fraction = distance / maxDistance;  //偏移放大系数
        if (fraction > 1) {
            fraction = 1;  //放大系数最大为1
        }
        //遍历，为每个child执行动画
        int itemCount = recyclerView.getChildCount();
        for (int i = 0; i < itemCount; i++) {
            //在动画渐变的过程中，最大达到它上面一个卡片的大小
            View view = recyclerView.getChildAt(i);
            int level = itemCount - i - 1;
            view.setTranslationY((float) (TRANSITION_Y * level - fraction * TRANSITION_Y));
            view.setScaleX((float) (1 - SCALE_RATIO * level + fraction * SCALE_RATIO));
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    //设置滑动不生效回弹距离
    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return 0.2f;
    }
}