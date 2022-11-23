
/*
    服务端发送消息
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
        processPointerEvent(event);
    }

    class NativePostImeInputStage extends AsyncInputStage {

        // 在 processPointerEvent() 方法中的 eventTarget 通常是 ViewRootImpl 保存的 DecorView 对象，也就是会调用到 View#dispatchPointerEvent() 方法
        private int processPointerEvent(QueuedInputEvent q) {
            MotionEvent event = (MotionEvent)q.mEvent;
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
        呼~ 到这里，终于看到我们熟悉的 dispatchTouchEvent() 方法了
        接下来我们终于可以好好聊聊，触摸事件的分发机制了
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