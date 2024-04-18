package cn.ybs.card.slide.recyclerview;

import android.graphics.Canvas;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import cn.ybs.card.slide.R;

/**
 * author : xiaobao
 * e-mail : yibaoshan@foxmail.com
 * time   : 2023/04/12
 * desc   :
 */
public class SlideCardCallBack2 extends ItemTouchHelper.SimpleCallback {
    private RecyclerView.Adapter adapter;
    private ArrayList datas;
    private final float SCALE_RATIO = 0.02f;

    private WeakReference<View> mLastViewRef;

    public SlideCardCallBack2(RecyclerView.Adapter adapter, ArrayList datas) {
        super(0, ItemTouchHelper.LEFT |
                ItemTouchHelper.RIGHT | ItemTouchHelper.UP |
                ItemTouchHelper.DOWN);
        this.adapter = adapter;
        this.datas = datas;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        viewHolder.itemView.setRotation(0f);
        TextView textView = viewHolder.itemView.findViewById(R.id.tv_index);
        Log.e(TAG, "onSwiped: " + textView.getText().toString());
    }

    private static final String TAG = "SlideCardCallBack2";

    //绘制拖拽过程中方法的效果
    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

        View curView = viewHolder.itemView;

        // 向左滑动，以 0 作为 x 旋转原点；向右滑动，以右上角作为原点旋转
        if (dX < 0) {
            curView.setPivotX(0);
        } else if (dX > 0) {
            curView.setPivotX(curView.getWidth());
        }

        // 旋转比例，50 是经验值
        curView.setPivotY(0f);
        curView.setRotation(-dX / 50);

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return super.isItemViewSwipeEnabled();
    }

    //设置滑动不生效回弹距离
    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return 0.38f;
    }



}