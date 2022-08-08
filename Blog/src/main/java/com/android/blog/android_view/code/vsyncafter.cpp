//
// Created by Bob on 2022/7/30.
//

/*

了解sf合成layer的过程，有助于帮助我们理解截屏、录屏的工作原理：你有没有想过，录屏软件是怎么获取到屏幕内容的？https://blog.simowce.com/all-about-virtualdisplay/
了解View的显示流程，有助于我们的APP发生卡顿问题时，快速定位/排查并解决问题：定性和定位Android图形性能问题 https://juejin.cn/post/7096288511053004830

在这里要和读者先说一声抱歉，因为时间和精(智)力(商)的原因，关于SF进程合成部分的源码没有继续跟下去，以下大多数结论都来自于：《Android SurfaceFlinger 学习之路》- windrunnerlihuan

虽然我没有跟代码，但大致的调用流程过了一遍，除了版本不一样导致的命名差异外，其他关键部分

，sf进程的合成部分非常长比较复杂，跟一段时间的源码之后放弃了，我实在是没有多余的精力去一步步解释，

了解每一步做了些什么可能会更有意义一些

关于sf合成部分的

想要了解合成部分的细节可以点击查看

sf合成五部曲中的任意一步

其任意一步内部都比较复杂，没有精力去跟踪源码

*/

#sf合成流程

/frameworks/native/services/surfaceflinger/MessageQueue.cpp
class MessageQueue {

    void MessageQueue::invalidate() {
        mEvents->requestNextVsync();
    }
}

/frameworks/native/services/surfaceflinger/SurfaceFlinger.cpp
class SurfaceFlinger {

    void SurfaceFlinger::onMessageReceived(){
        case MessageQueue::INVALIDATE: {

        }
        case MessageQueue::REFRESH: {
            handleMessageRefresh();
        }
    }

    //合成五部曲
    void SurfaceFlinger::handleMessageRefresh(){
        //调Layer的onPreComposition方法，主要是标志一下Layer已经被用于合成
        preComposition();
        //若Layer的位置/先后顺序/可见性发生变化，重新计算Layer的目标合成区域和先后顺序
        rebuildLayerStacks();
        //配置硬件合成器，调hwc的prepare方法
        setUpHWComposer();
        //当打开开发者选项中的“显示Surface刷新”时，额外为产生变化的图层绘制闪烁动画
        doDebugFlashRegions();
        //执行合成主体，对3D合成而言，调opengl的drawcall，对硬件合成而言，调hwc的set方法
        doComposition();
        //调Layer的onPostComposition方法，主要用于调试，可以忽略
        postComposition(refreshStartTime);
    }

    //第一步：调用每个layer的onPreComposition()方法询问是否需要合成
    void SurfaceFlinger::preComposition(){
        bool needExtraInvalidate = false;
        //获取全部经过z-order排序的layer；
        const LayerVector& layers(mDrawingState.layersSortedByZ);
        const size_t count = layers.size();
        //layer也分几种类型（不重要），调用每个layer的onPreComposition判断是否需要触发Vsync更新
        for (size_t i=0 ; i<count ; i++) {
            if (layers[i]->onPreComposition()) {
                needExtraInvalidate = true;
            }
        }
        if (needExtraInvalidate) {
            signalLayerUpdate();
        }
    }

    //第二步： 若Layer的位置/先后顺序/可见性发生变化，重新计算Layer的目标合成区域和先后顺序
    void SurfaceFlinger::rebuildLayerStacks(){
        //获取当前应用程序所有按照z-order排列的layer
        const LayerVector& layers(mDrawingState.layersSortedByZ);
        //遍历每一个显示屏
        for (size_t dpy=0 ; dpy<mDisplays.size() ; dpy++) {
            //z-order排列的layer
            hw->setVisibleLayersSortedByZ(layersSortedByZ);
            //显示屏大小
            hw->undefinedRegion.set(bounds);
            //减去不透明区域
            hw->undefinedRegion.subtractSelf(tr.transform(opaqueRegion));
            //累加脏区域
            hw->dirtyRegion.orSelf(dirtyRegion);
        }
    }

    //第二步：
    void SurfaceFlinger::setUpHWComposer(){
    }

    //第二步：
    void SurfaceFlinger::doDebugFlashRegions(){
    }

    //第二步：
    void SurfaceFlinger::doComposition(){
        const bool repaintEverything = android_atomic_and(0, &mRepaintEverything);
        //遍历所有的DisplayDevice然后调用doDisplayComposition函数
        for (size_t dpy=0 ; dpy<mDisplays.size() ; dpy++) {
            const sp<DisplayDevice>& hw(mDisplays[dpy]);
            if (hw->isDisplayOn()) {
                // transform the dirty region into this screen's coordinate space
                //获得屏幕的脏区域
                const Region dirtyRegion(hw->getDirtyRegion(repaintEverything));
                //合成，重绘framebuffer
                // repaint the framebuffer (if needed)
                doDisplayComposition(hw, dirtyRegion);
                //清除屏幕脏区域
                hw->dirtyRegion.clear();
                //判断系统是否支持软件部分更新
                hw->flip(hw->swapRegion);
                //清除交换区域
                hw->swapRegion.clear();
            }
            // inform the h/w that we're done compositing
            //通知hwc硬件合成结束
            hw->compositionComplete();
        }
        //主要是调用hwc硬件的set函数
        //此方法将完成各个图层的合成与显示，等效于EGL标准里面的eglSwapBuffers，
        //不过eglSwapBuffers是对OpenGL标准/GPU有效，此方法是对硬件合成器有效
        postFramebuffer();
    }

    //第二步：
    void SurfaceFlinger::postComposition(){
    }

}