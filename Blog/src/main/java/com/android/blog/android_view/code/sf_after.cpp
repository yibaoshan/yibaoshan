//
// Created by Bob on 2022/7/30.
//

/*

了解sf合成layer的过程，有助于帮助我们理解截屏、录屏的工作原理：你有没有想过，录屏软件是怎么获取到屏幕内容的？https://blog.simowce.com/all-about-virtualdisplay/
了解View的显示流程，有助于我们的APP发生卡顿问题时，快速定位/排查并解决问题：定性和定位Android图形性能问题 https://juejin.cn/post/7096288511053004830

在这里要和读者先说一声抱歉，因为时间和精(智)力(商)的原因，关于SF进程合成部分的源码没有继续跟下去，以下大多数结论都来自于：《Android SurfaceFlinger 学习之路》- windrunnerlihuan

关于合成过程的分析，点击这里查看完整版

虽然我没有跟代码，但大致的调用流程过了一遍，除了版本不一样导致的命名差异外，其他关键部分

，sf进程的合成部分非常长比较复杂，跟一段时间的源码之后放弃了，我实在是没有多余的精力去一步步解释，

了解每一步做了些什么可能会更有意义一些

关于sf合成部分的

想要了解合成部分的细节可以点击查看

sf合成五部曲中的任意一步

其任意一步内部都比较复杂，没有精力去跟踪源码

*/

#VSync信号到来处理

/frameworks/native/services/surfaceflinger/MessageQueue.cpp
class MessageQueue {

    //接受来自DisplayEventReceiver的vsync信号
    int MessageQueue::eventReceiver(int /*fd*/, int /*events*/) {
        mHandler->dispatchInvalidate();
        return 1;
    }

    //收到vsync信号后，向sf进程中发送类型为"INVALIDATE"的消息
    void MessageQueue::Handler::dispatchInvalidate() {
        mQueue.mLooper->sendMessage(this, Message(MessageQueue::INVALIDATE));
    }

    //外部接口，用于向sf发送合成消息
    void MessageQueue::refresh() {
        mHandler->dispatchRefresh();
    }

    //给sf发送类型为REFRESH的消息，sf收到以后将会执行合成操作
    void MessageQueue::Handler::dispatchRefresh() {
        mQueue.mLooper->sendMessage(this, Message(MessageQueue::REFRESH));
    }

}

/frameworks/native/services/surfaceflinger/SurfaceFlinger.cpp
class SurfaceFlinger {

    void SurfaceFlinger::onMessageReceived(){

        //接收到vsync信号后，判断图层是否需要合成
        case MessageQueue::INVALIDATE: {
            bool refreshNeeded = false;
            refreshNeeded = handleMessageTransaction();
            refreshNeeded |= handleMessageInvalidate();
            //如果需要合成，通知MessageQueue发送一条REFRESH类型的消息
            //绕了一圈后，最终执行的代码就在下面的handleMessageRefresh()方法中
            if(refreshNeeded) signalRefresh();
        }
        //将会执行最终的合成操作
        case MessageQueue::REFRESH: {
            handleMessageRefresh();//合成并输出到屏幕
        }
    }

    //调用mEventQueue给sf自己发送一条Refresh类型的消息
    void SurfaceFlinger::signalRefresh() {
        mEventQueue.refresh();
    }

}



#sf合成流程

/frameworks/native/services/surfaceflinger/Layer.cpp
class Layer {

    //当Surface发生变化以后，最终会调用onFrameAvailable()方法通知sf，让sf请求下一次vsync
    //这里需要注意，vsync信号是EventThread来分发的，APP和sf各自管理自己是否需要请求下一次vsync信号
    void Layer::onFrameAvailable() {
        mFlinger->signalLayerUpdate();
    }
}

/frameworks/native/services/surfaceflinger/SurfaceFlinger.cpp
class SurfaceFlinger {

    //queue内部调用了请求下一次vsync
    void SurfaceFlinger::signalLayerUpdate() {
        mEventQueue.invalidate();
    }

    void SurfaceFlinger::onMessageReceived(){

        //接收到vsync信号后
        case MessageQueue::INVALIDATE: {
            bool refreshNeeded = false;
            refreshNeeded = handleMessageTransaction();
            refreshNeeded |= handleMessageInvalidate();
            if(refreshNeeded) signalRefresh();//会触发handleMessageRefresh
        }
        //如果图层需要合成，将会执行最终的合成操作
        case MessageQueue::REFRESH: {
            handleMessageRefresh();//合成并输出到屏幕
        }
    }

    //调用mEventQueue给sf自己发送一条Refresh类型的消息
    void SurfaceFlinger::signalRefresh() {
        mEventQueue.refresh();
    }

}

/frameworks/native/services/surfaceflinger/MessageQueue.cpp
class MessageQueue {

    //最终在MessageQueue类中执行了请求vsync信号的操作
    void MessageQueue::invalidate() {
        mEvents->requestNextVsync();
    }

    int MessageQueue::eventReceiver(int /*fd*/, int /*events*/) {
        mHandler->dispatchInvalidate();
        return 1;
    }
}

/frameworks/native/services/surfaceflinger/SurfaceFlinger.cpp
class SurfaceFlinger {

    void SurfaceFlinger::init() {
        //用于控制硬件vsync开关状态
        mEventControlThread = new EventControlThread(this);
        mEventControlThread->run("EventControl", PRIORITY_URGENT_DISPLAY);
    }

    //接收来自hwc的硬件vsync信号
    void SurfaceFlinger::onVSyncReceived(int32_t type, nsecs_t timestamp) {
        bool needsHwVsync = false;
        needsHwVsync = mPrimaryDispSync.addResyncSample(timestamp);

        if (needsHwVsync) {
            enableHardwareVsync();
        } else {
            disableHardwareVsync(false);
        }
    }

    //启用硬件vsync信号
    void SurfaceFlinger::enableHardwareVsync() {
        mPrimaryDispSync.beginResync();
        mEventControlThread->setVsyncEnabled(true);
    }

    //关闭硬件vsync信号
    void SurfaceFlinger::disableHardwareVsync(bool makeUnavailable) {
       mEventControlThread->setVsyncEnabled(false);
       mPrimaryDispSync.endResync();
    }

}

/frameworks/native/services/surfaceflinger/SurfaceFlinger.cpp
class SurfaceFlinger {

    void SurfaceFlinger::onMessageReceived(){
        case MessageQueue::INVALIDATE: {
            //接收到vsync信号后
            handleMessageTransaction();
            //主要调用handlePageFlip，从各Layer的BufferQueue拿到最新的缓冲数据，并根据内容更新脏区域
            handleMessageInvalidate();
            signalRefresh();//会触发handleMessageRefresh
        }
        case MessageQueue::REFRESH: {
            handleMessageRefresh();//合成并输出到屏幕
        }
    }

    //queue内部调用了请求下一次vsync
    void SurfaceFlinger::signalLayerUpdate() {
        mEventQueue.invalidate();
    }

    //合成五部曲
    void SurfaceFlinger::handleMessageRefresh(){
        //合成之前的与处理，检查是否有新的图层变化，如果有，执行请求下一次vsync信号
        preComposition();
        //若Layer的位置/先后顺序/可见性发生变化，重新计算Layer的目标合成区域和先后顺序
        rebuildLayerStacks();
        //调hwc的prepare方法询问是否支持硬件合成
        setUpHWComposer();
        //当打开开发者选项中的“显示Surface刷新”时，额外为产生变化的图层绘制闪烁动画
        doDebugFlashRegions();
        //执行合成主体，对3D合成而言，调opengl的drawcall，对硬件合成而言，调hwc的set方法
        doComposition();
        //调Layer的onPostComposition方法，主要用于调试，可以忽略
        postComposition(refreshStartTime);
    }

    //第一步：预处理阶段，调用每个layer的onPreComposition()方法询问是否需要合成
    //第一步执行完以后，确定是否有遗漏的图层，如果有就再次请求vsync信号
    void SurfaceFlinger::preComposition(){
        bool needExtraInvalidate = false;
        const LayerVector& layers(mDrawingState.layersSortedByZ);
        const size_t count = layers.size();
        for (size_t i=0 ; i<count ; i++) {
            //因为在调用合成之前已经计算过脏区域，如果有图层在计算以后加入了队列，那么在预处理阶段要再次请求vsync信号
            if (layers[i]->onPreComposition()) {
                needExtraInvalidate = true;
            }
        }
        //存在未处理的layer，执行请求下一次vsync信号，避免这段时间内的帧数据丢掉了
        if (needExtraInvalidate) {
            signalLayerUpdate();
        }
    }

    //第二步： 若Layer的位置/先后顺序/可见性发生变化，重新计算Layer的目标合成区域和先后顺序
    //第二步执行完以后，确定了每个图层的可见区域和跟其他图层发生重叠部分的脏区域
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

    //第三步：更新HWComposer对象中图层对象列表以及图层属性
    //第三步执行完以后，确定了每个图层的合成方式
    void SurfaceFlinger::setUpHWComposer() {
        //prepareFrame方法中调用了HWComposer::prepare方法
        for (size_t displayId = 0; displayId < mDisplays.size(); ++displayId) {
            auto& displayDevice = mDisplays[displayId];
            if (!displayDevice->isDisplayOn()) {
                continue;
            }
            status_t result = displayDevice->prepareFrame(*mHwc);
        }
    }

    //第四步：执行真正的合成工作
    //第四部执行完以后，完成了两件事
    //1. 将不支持硬件合成的图层进行GPU合成
    //2. 调用postFramebuffer()将GPU合成后的图层和需要HWC合成的图层一起打包提交给HWC
    void SurfaceFlinger::doComposition(){
        //遍历所有的DisplayDevice然后调用doDisplayComposition函数
        for (size_t dpy=0 ; dpy<mDisplays.size() ; dpy++) {
            const sp<DisplayDevice>& hw(mDisplays[dpy]);
            if (hw->isDisplayOn()) {
                //获得屏幕的脏区域，将脏区转换为该屏幕的座标空间
                const Region dirtyRegion(hw->getDirtyRegion(repaintEverything));
                //在此方法中将会调用到doComposeSurfaces()方法
                //在doComposeSurfaces方法中，将会为被标记为不支持硬件合成的图层调用Layer#draw()方法使用OpenGL ES合成
                doDisplayComposition(hw, dirtyRegion);
            }
        }
        postFramebuffer();
    }


    //第五步：更新DispSync机制，详情参见
    void SurfaceFlinger::postComposition(){
        //更新DispSync机制，详情参见
    }

}

/frameworks/native/services/surfaceflinger/SurfaceFlinger.cpp
class SurfaceFlinger {

    //第五步：更新DispSync机制，详情参见
    void SurfaceFlinger::postComposition(){
        //更新DispSync，详情参见DispSync模型一节
    }

}