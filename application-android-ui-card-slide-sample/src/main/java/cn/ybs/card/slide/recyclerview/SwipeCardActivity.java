package cn.ybs.card.slide.recyclerview;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import cn.ybs.card.slide.R;
import cn.ybs.card.slide.recyclerview.mcxtzhang.CardConfig;
import cn.ybs.card.slide.recyclerview.mcxtzhang.RenRenCallback;
import cn.ybs.core.base.BaseAppCompatActivity;

import java.util.ArrayList;

public class SwipeCardActivity extends BaseAppCompatActivity {

    private RecyclerView recyclerView;

    private CardAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_card);

        CardConfig.initConfig(this);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new SlideCardLayoutManager2());

        adapter = new CardAdapter(this);

        ArrayList<CardBean> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(new CardBean(R.drawable.img_2000x3000));
            list.add(new CardBean(R.drawable.test));
        }

        recyclerView.setAdapter(adapter);
        adapter.setDatas(list);

//        new ItemTouchHelper(new SlideCardCallBack2(adapter, list)).attachToRecyclerView(recyclerView);
        new ItemTouchHelper(new RenRenCallback(recyclerView,adapter, list)).attachToRecyclerView(recyclerView);

    }

}
