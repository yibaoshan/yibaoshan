package com.android.blackboard.ui;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.blackboard.R;

import java.lang.reflect.Field;
import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class RecyclerViewActivity extends AppCompatActivity {

    private static final String TAG = "RecyclerViewActivity";

    private RecyclerView recyclerView;

    // 反射获取刚被滑出屏幕的缓存
    private static ArrayList<RecyclerView.ViewHolder> mCachedViews;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);
        initRecyclerView();
        initReflect();
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.getRecycledViewPool().clear();

        String[] localDataSet = new String[30];

        for (int i = 0; i < 30; i++) {
            localDataSet[i] = i + "";
        }

        recyclerView.setAdapter(new CustomAdapter(localDataSet));
    }

    private void initReflect() {
        try {
            Field mRecyclerField = RecyclerView.class.getDeclaredField("mRecycler");
            mRecyclerField.setAccessible(true);
            RecyclerView.Recycler mRecycler = (RecyclerView.Recycler) mRecyclerField.get(recyclerView);

            Field mCachedViewsField = RecyclerView.Recycler.class.getDeclaredField("mCachedViews");
            mCachedViewsField.setAccessible(true);
            mCachedViews = (ArrayList<RecyclerView.ViewHolder>) mCachedViewsField.get(mRecycler);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    static class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

        private String[] localDataSet;

        static class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView textView;

            public ViewHolder(View view) {
                super(view);
                textView = (TextView) view.findViewById(R.id.textView);
            }

            public TextView getTextView() {
                return textView;
            }
        }

        public CustomAdapter(String[] dataSet) {
            localDataSet = dataSet;
            setHasStableIds(true);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.text_row_item, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {
            viewHolder.getTextView().setText(localDataSet[position]);
        }

        @Override
        public int getItemCount() {
            return localDataSet.length;
        }

        @Override
        public void onViewRecycled(@NonNull ViewHolder holder) {
            // 视图完全被回收，被丢进 mRecyclerPool
            Log.e(TAG, "onViewRecycled: " + holder.getTextView().getText() + mCachedViews);
            super.onViewRecycled(holder);
        }

        @Override
        public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
            Log.e(TAG, "onViewDetachedFromWindow: " + holder.getTextView().getText());
            super.onViewDetachedFromWindow(holder);
        }

        @Override
        public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
            Log.e(TAG, "onViewAttachedToWindow: " + holder.getTextView().getText());
            super.onViewAttachedToWindow(holder);
        }
    }


}
