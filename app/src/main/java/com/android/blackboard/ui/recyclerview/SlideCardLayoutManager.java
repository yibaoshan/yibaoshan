package com.android.blackboard.ui.recyclerview;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

/**
 * author : xiaobao
 * e-mail : yibaoshan@foxmail.com
 * time   : 2023/04/12
 * desc   :
 */
public class SlideCardLayoutManager extends RecyclerView.LayoutManager {

    private static final int MAX_CARD_COUNT = 4;
    private static final float SCALE_RATIO = 0.08f;
    private static final float TRANSITION_Y = 30f;

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    //必须要重写，对item设置放置规则
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        //直接使用父类的回收功能
        detachAndScrapAttachedViews(recycler);

        int itemCount = getItemCount();  //当前adapter中总共item数
        int bottomPosition;

        if (itemCount < MAX_CARD_COUNT) {
            bottomPosition = 0;
        } else {
            bottomPosition = itemCount - MAX_CARD_COUNT;
        }

        //遍历最下层position到最上层postion的view
        for (int i = bottomPosition; i < itemCount; i++) {
            //复用缓存view，底层就是使用了 tryGetViewHolderForPositionByDeadline
            View view = recycler.getViewForPosition(i);
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

            //从下到上定义level
            int level = itemCount - i - 1;

            if (level < MAX_CARD_COUNT) {
                //对itemView进行y方向平移
                view.setTranslationY(TRANSITION_Y * level);
                //对itemView进行放大
                view.setScaleX(1 - SCALE_RATIO * level);
            } else {
                //如果是最底下一张，则效果与前一张一样，即不会展示出来
                view.setTranslationY(TRANSITION_Y * (level - 1));
                view.setScaleX(1 - SCALE_RATIO * (level - 1));
            }
        }
    }
}