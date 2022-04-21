package com.android.blog

import android.app.Application
import com.silencedut.fpsviewer.FpsViewer


class App : Application() {
    override fun onCreate() {
        super.onCreate()
        FpsViewer.getViewer().initViewer(this,null);
    }
}