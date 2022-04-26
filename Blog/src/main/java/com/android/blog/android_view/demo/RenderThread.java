package com.android.blog.android_view.demo;

/**
 * Created by yibs.space on 2022/4/21
 */

import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.util.Random;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

import static android.opengl.EGL14.EGL_CONTEXT_CLIENT_VERSION;
import static android.opengl.EGL14.EGL_OPENGL_ES2_BIT;

public class RenderThread extends Thread {
    private SurfaceTexture mSurface;
    private EGL10 mEgl;
    private EGLDisplay mEglDisplay;
    private EGLConfig mEglConfig;
    private EGLContext mEglContext;
    private EGLSurface mEglSurface;

    public RenderThread(SurfaceTexture surfaceTexture) {
        mSurface = surfaceTexture;
    }

    @Override
    public void run() {
        initGL();
        //开始画一帧数据
        draw();

    }

    private void initGL() {
        /*Get EGL handle*/
        mEgl = (EGL10) EGLContext.getEGL();
        /*Get EGL display*/
        mEglDisplay = mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        if (EGL10.EGL_NO_DISPLAY == mEglDisplay) {
            throw new RuntimeException("eglGetDisplay failed:" + GLUtils.getEGLErrorString(mEgl.eglGetError()));
        }
        /*Initialize & Version*/
        int versions[] = new int[2];
        if (!mEgl.eglInitialize(mEglDisplay, versions)) {
            throw new RuntimeException("eglInitialize failed:" + GLUtils.getEGLErrorString(mEgl.eglGetError()));
        }
        /*Configuration*/
        int configsCount[] = new int[1];
        EGLConfig configs[] = new EGLConfig[1];
        int configSpec[] = new int[]{
                EGL10.EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
                EGL10.EGL_RED_SIZE, 8,
                EGL10.EGL_GREEN_SIZE, 8,
                EGL10.EGL_BLUE_SIZE, 8,
                EGL10.EGL_ALPHA_SIZE, 8,
                EGL10.EGL_DEPTH_SIZE, 0,
                EGL10.EGL_STENCIL_SIZE, 0,
                EGL10.EGL_NONE
        };
        mEgl.eglChooseConfig(mEglDisplay, configSpec, configs, 1, configsCount);
        if (configsCount[0] <= 0) {
            throw new RuntimeException("eglChooseConfig failed:" + GLUtils.getEGLErrorString(mEgl.eglGetError()));
        }
        mEglConfig = configs[0];
        /*Create Context*/
        int contextSpec[] = new int[]{
                EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL10.EGL_NONE
        };
        mEglContext = mEgl.eglCreateContext(mEglDisplay, mEglConfig, EGL10.EGL_NO_CONTEXT, contextSpec);
        if (EGL10.EGL_NO_CONTEXT == mEglContext) {
            throw new RuntimeException("eglCreateContext failed: " + GLUtils.getEGLErrorString(mEgl.eglGetError()));
        }
        /*Create window surface*/
        mEglSurface = mEgl.eglCreateWindowSurface(mEglDisplay, mEglConfig, mSurface, null);
        if (null == mEglSurface || EGL10.EGL_NO_SURFACE == mEglSurface) {
            throw new RuntimeException("eglCreateWindowSurface failed" + GLUtils.getEGLErrorString(mEgl.eglGetError()));
        }
        /*Make current*/
        if (!mEgl.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEglContext)) {
            throw new RuntimeException("eglMakeCurrent failed:" + GLUtils.getEGLErrorString(mEgl.eglGetError()));
        }
    }

    private void draw() {
        while (true) {
            drawFrame();
        }
    }

    private boolean changed = true;

    private void drawFrame() {
        //将背景设置为灰色
        if (changed) {
            GLES20.glClearColor(255f, 0f, 0f, 1.0f);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        } else {
            GLES20.glClearColor(0f, 0f, 255f, 1.0f);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        }
        changed = !changed;
        // 一帧完成之后，调用eglSwapBuffers(EGLDisplay dpy, EGLContext ctx)来显示
        // 这一句不能少啊，少了就什么都没有
        mEgl.eglSwapBuffers(mEglDisplay, mEglSurface);
//        sleep();
        Log.e("TAG", "drawFrame: " + changed);
    }

    private void sleep() {
        if (new Random().nextInt(100) % 3 == 0) {
            try {
                Thread.sleep(new Random().nextInt(100));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}