package com.android.blackboard.ui.recyclerview;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * author : xiaobao
 * e-mail : yibaoshan@foxmail.com
 * time   : 2023/04/12
 * desc   :
 */
public class SlideCardLayoutManager2 extends RecyclerView.LayoutManager {

    private static final int MAX_CARD_COUNT = 2;

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private static final String TAG = "SlideCardLayoutManager2";

    //必须要重写，对item设置放置规则
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
//直接使用父类的回收功能
        detachAndScrapAttachedViews(recycler);

        Log.e(TAG, "onLayoutChildren: " );

        int itemCount = getItemCount();
        int startIndex = Math.max(itemCount - 2, 0);

        //遍历最下层position到最上层postion的view
        for (int i = 0; i < itemCount; i++) {
            //复用缓存view，底层就是使用了 tryGetViewHolderForPositionByDeadline
            View view = recycler.getViewForPosition(i);

            if (view.getScaleX() != 1f) view.setScaleX(1f);
            if (view.getScaleY() != 1f) view.setScaleY(1f);

            addView(view);  //将itemView添加到recyclerView中
            measureChildWithMargins(view, 0, 0);  //测量itemView

            //view在RecyclerView里面还剩余的宽度、高度空隙
            int viewWidth = getDecoratedMeasuredWidth(view);
            int viewHeight = getDecoratedMeasuredHeight(view);
            int widthSpace = getWidth() - viewWidth;
            int heightSpace = getHeight() - viewHeight;

            //布局itemView，把所有itemView放在屏幕正中
            layoutDecoratedWithMargins(view, widthSpace / 2, heightSpace / 2,
                    widthSpace / 2 + viewWidth, heightSpace / 2 + viewHeight);

        }
    }
}