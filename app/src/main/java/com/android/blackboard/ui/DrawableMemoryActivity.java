package com.android.blackboard.ui;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.blackboard.R;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class DrawableMemoryActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawable_memory);
        img = (ImageView) findViewById(R.id.img);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        printBitmapSize(img);
        Bitmap bitmap = decodeSampledBitmapFromResource(getResources(), R.drawable.test, img.getWidth(), img.getHeight());
        img.setImageBitmap(bitmap);
        printBitmapSize(img);
    }

    private void printBitmapSize(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        if (drawable != null) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            Log.e(TAG, " width = " + bitmap.getWidth() + " height = " + bitmap.getHeight() + " ByteCount = "+bitmap.getAllocationByteCount());
        } else {
            Log.e(TAG, "Drawable is null !");
        }
    }

    // 让加载的Bitmap和view宽度或者高度一样
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        // 调用上面定义的方法计算inSampleSize值
        if(options.outHeight/reqHeight >options.outWidth/reqWidth){
            options.inDensity = options.outHeight;
            options.inTargetDensity =reqHeight;
        }else{
            options.inDensity = options.outWidth;
            options.inTargetDensity = reqWidth;
        }
        // 指定色彩模式，不带透明度指定为 565 ，内存占用小一半
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        // 指定压缩比
        options.inSampleSize = 4;
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

}