package com.android.blackboard.ui.recyclerview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.blackboard.R;
import com.android.blackboard.ui.recyclerview.mcxtzhang.CardConfig;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * author : xiaobao
 * e-mail : yibaoshan@foxmail.com
 * time   : 2023/04/12
 * desc   : 自定义 RecyclerView LayoutManager，实现仿探探卡片式滑动
 */
public class SwipeCardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private CardAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_card);

        CardConfig.initConfig(this);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new SlideCardLayoutManager2());
//        recyclerView.setLayoutManager(new OverLayCardLayoutManager());

        adapter = new CardAdapter(this);

        ArrayList<CardBean> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(new CardBean(R.drawable.img_2000x3000));
            list.add(new CardBean(R.drawable.test));
        }

        recyclerView.setAdapter(adapter);
        adapter.setDatas(list);

        new ItemTouchHelper(new SlideCardCallBack2(adapter, list)).attachToRecyclerView(recyclerView);
//        new androidx.recyclerview.widget.ItemTouchHelper(new SlideCardCallBack(adapter, list)).attachToRecyclerView(recyclerView);
//        new ItemTouchHelper(new RenRenCallback(recyclerView,adapter, list)).attachToRecyclerView(recyclerView);

        try {
            testBitmap();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String TAG = "SwipeCardActivity";

    private void testBitmap() throws NoSuchAlgorithmException {

        int count = 0;
        while (++count < 5) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_2000x3000);
//            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test);

            Log.e(TAG, "size = "+( bitmap.getByteCount() / 1024.0 / 1024.0));


            long cur = System.currentTimeMillis();
            Log.e(TAG, "bitmap2string: " + bitmap2string(bitmap) + " , 耗时 = " + (System.currentTimeMillis() - cur));
            cur = System.currentTimeMillis();
            Log.e(TAG, "getMD5Digest: " + BitmapDigestUtils.getMD5Digest(bitmap) + " , 耗时 = " + (System.currentTimeMillis() - cur));

            try {
                Thread.sleep(2 * 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }


    }

    final float MAX_ROW_LIMIT = 1024f;
    final float MAX_COLUMN_LIMIT = 1024f;

    public String bitmap2string(Bitmap bitmap) throws NoSuchAlgorithmException {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int offsetRow = width < MAX_ROW_LIMIT ? 1 : (int) Math.ceil(width / MAX_ROW_LIMIT);
        int offsetColumn = height < MAX_COLUMN_LIMIT ? 1 : (int) Math.ceil(height / MAX_COLUMN_LIMIT);

        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < width; i += offsetColumn) {
            StringBuilder sb = new StringBuilder();
            sb.append(i);
            for (int j = 0; j < height; j += offsetRow) {
                sb.append(bitmap.getPixel(i, j));
            }
            ret.append(generateMD5(sb.toString()));
        }
        return generateMD5(ret.toString());
    }

    public static String generateMD5(String input) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] byteArr = md5.digest(input.getBytes(StandardCharsets.UTF_8));
        return byte2hex(byteArr);
    }

    public static String byte2hex(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp = "";
        for (byte value : b) {
            //为了保证二进制机器数不变，这里需要& 0XFF
            stmp = (Integer.toHexString(value & 0XFF));
            //如果只有一位，需要在前面补上0凑足两位
            if (stmp.length() == 1) {
                hs.append("0").append(stmp);
            } else {
                hs.append(stmp);
            }
        }
        return hs.toString();
    }

}
