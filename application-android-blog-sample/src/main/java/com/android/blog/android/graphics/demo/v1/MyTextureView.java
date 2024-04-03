package com.android.blog.android.graphics.demo.v1;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.util.AttributeSet;
import android.view.TextureView;

import androidx.annotation.RequiresApi;

/**
 * Created by yibs.space on 2022/4/21
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MyTextureView extends TextureView implements TextureView.SurfaceTextureListener {
    public MyTextureView(Context context) {
        this(context,null);
    }
    public MyTextureView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }
    public MyTextureView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }
    private void init() {
        setSurfaceTextureListener(this);
    }
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        new RenderThread(surface).start();
    }
    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }
    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }
    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }
}