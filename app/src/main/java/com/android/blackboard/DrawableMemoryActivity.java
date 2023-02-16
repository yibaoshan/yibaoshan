package com.android.blackboard;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

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

}