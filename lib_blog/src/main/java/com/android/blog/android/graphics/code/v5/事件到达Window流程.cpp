


/*
    服务端发送消息
    触摸事件驾到通通闪开
*/

//frameworks/native/libs/input/InputTransport.cpp
class InputTransport {

    class InputChannel {

        int mFd;

        status_t InputChannel::sendMessage(InputMessage* msg) {
            // 使用 send 唤醒 fd
            send(mFd, msg, msgLength, MSG_DONTWAIT | MSG_NOSIGNAL);
            return OK;
        }
    }
}

/**

    客户端接受消息

*/

//frameworks/base/core/jni/android_view_InputEventReceiver.cpp
class NativeInputEventReceiver {

    // 接受到消息后的处理，为了方便理解代码我做了省略，和源码对照不起来
    int NativeInputEventReceiver::handleEvent( receiveFd, events, data) {
        switch (type) { //这是 consumeEvents() 方法中的逻辑
            case AINPUT_EVENT_TYPE_KEY: {
                inputEventObj = factory->createKeyEvent(); // 转换为按键类型事件 KeyEvent，注意，这是 InputConsumer::consume() 方法中的逻辑
            }
            case AINPUT_EVENT_TYPE_MOTION: {
                inputEventObj = factory->createMotionEvent(); // 转转为触摸类型事件 MotionEvent
            }
        }
        // 将事件传递到 Java 层的 InputEventReceiver#dispatchInputEvent() 方法
        env->CallVoidMethod(receiverObj.get(),dispatchInputEvent, seq, inputEventObj);
    }
}

//frameworks/base/core/java/android/view/InputEventReceiver.java
abstract class InputEventReceiver {

    // Called from native code.
    private void dispatchInputEvent(int seq, InputEvent event) {
        onInputEvent(event);
    }
}

//frameworks/base/core/java/android/view/ViewRootImpl.java
class ViewRootImpl extends InputEventReceiver {

    /**
    putEventReceiver 是抽象类，在 dispatchInputEvent() 方法中回调 InputEventReceiver 的 onInputEvent() 方法。

    ViewRootImpl 的内部类 WindowInputEventReceiver 实现了 InputEventReceiver。
    */
    class WindowInputEventReceiver extends InputEventReceiver {

        @Override
        public void onInputEvent(InputEvent event) {
            enqueueInputEvent(event, this, 0, true);
        }
    }

    void enqueueInputEvent(InputEvent event,InputEventReceiver receiver, int flags, boolean processImmediately) {
        ...
        // 执行消息入列以后，接着还有一个比较复杂的流水线过程，我们这里先不关心，直接来看 processPointerEvent() 方法
        // input 消息到达 ViewRootImpl 后，Google 使用责任链的模式，将输入事件拆分为 KeyEvent 和 MotionEvent 两种类型，做进一步的处理
        //
        if(event.getType == input) processPointerEvent(event);
        if(event.getType == key) processKeyEvent(event);
    }

    // 责任链模式，每个 InputStage 负责不同的功能，链中的某个 InputStage 的结果会影响对下一节点的执行，或停止继续分发等
    // 在 ViewRootImpl#setView() 函数中指定责任链的前后顺序，这里不展开讨论，请查看参考资料列表中《这一次，带你彻底弄懂 Android 事件分发机制(外/内层责任链)》
    class InputStage {

        // 在 ViewRootImpl 中有好几个同名 processPointerEvent() 方法， eventTarget 通常是 ViewRootImpl 保存的 DecorView 对象，也就是会调用到 View#dispatchPointerEvent() 方法
        private int processPointerEvent(QueuedInputEvent q) {
            MotionEvent event = (MotionEvent)q.mEvent;
            final View eventTarget =  mView; // 通常是 DecorView
            boolean handled = eventTarget.dispatchPointerEvent(event);
            ...
            return handled ? FINISH_HANDLED : FORWARD;
        }

        private int processKeyEvent(QueuedInputEvent q) {
            KeyEvent event = (KeyEvent)q.mEvent;
            mView.dispatchKeyEvent(event)
            final View eventTarget =  mView; // 通常是 DecorView
            boolean handled = eventTarget.dispatchPointerEvent(event);
            ...
            return handled ? FINISH_HANDLED : FORWARD;
        }

    }

}

//frameworks/base/core/java/android/view/View.java
class View {

    /**
        到这里，一个 input 事件就从硬件驱动一路，成功的到达应用的 Window 层了

        Framework 的工作结束了，接下来就是从几十上百个 View/ViewGroup 中，找到想要消费事件的视图

        我们先来确定本章节要解决的问题

        按键事件通常在 Activity 、Dialog 时该处理的就被处理了，事件分发通常只需要关心触摸事件

        我们一指头按下去，在同一块区域内，可能有好几个 ViewGroup 和 View 都需要此事件

        但是，并不是此 Window 中的所有 View / ViewGroup 都会收到询问事件，只有当你

        当我们按下屏幕屏幕以后，可能在好几个用朴素的感情也能猜出来

        从视图层级的角度来讲，肯定是先有 ViewGroup ，再有 View。

        分发过程也是如此，事件先到达 ViewGroup，如果 ViewGroup 不想处理了，才会向下分发给子 View

        呼~ 到这里，终于看到我们熟悉的 dispatchTouchEvent() 方法了
        接下来我们终于可以好好聊聊，触摸事件的分发机制了

        好了，现在我们能理解触摸事件分发的过程，也能处理一些事件冲突

        理解了当 View / ViewGroup 消费了 DOWN 事件以后，后续的 MOVE / UP / CANCEL 都会交由该 View / ViewGroup 处理

        那么来看下面的效果图

        一次 DOWN 事件，所有视图都更新动画，并且，手指移动时，视图也要跟着变化，也就是说 MOVE / UP 事件也要同步给所有视图，请问该怎么实现？

        源码在我的 GitHub 主页，需要请自取

        先不看代码的话有实现方案吗？欢迎留言在评论区一起探讨
    */

    /**
     * Dispatch a pointer event.
     * <p>
     * Dispatches touch related pointer events to {@link #onTouchEvent(MotionEvent)} and all
     * other events to {@link #onGenericMotionEvent(MotionEvent)}.  This separation of concerns
     * reinforces the invariant that {@link #onTouchEvent(MotionEvent)} is really about touches
     * and should not be expected to handle other pointing device features.
     * </p>
     *
     * @param event The motion event to be dispatched.
     * @return True if the event was handled by the view, false otherwise.
     * @hide
     */
    public final boolean dispatchPointerEvent(MotionEvent event) {
        if (event.isTouchEvent()) {
            return dispatchTouchEvent(event);
        } else {
            return dispatchGenericMotionEvent(event);
        }
    }
}