
## ViewGroup#dispatchTouchEvent()

ViewGroup 对事件的放行和拦截，大致流程：

- 事件首先到达 DecorView ，它是个 ViewGroup ，执行 ViewGroup#dispatchTouchEvent() 逻辑
- 优先检查子视图是否调用了 requestDisallowInterceptTouchEvent(true) 请求放行该事件
  - 子视图请求放行，优先分发给子视图
  - 子视图未请求放行，调用 onInterceptTouchEvent() 询问 ViewGroup 自身是否需要消费
    - true ，父视图自身想要消费，停止下发，转发到 ViewGroup#onTouchEvent() 消费
    - false ，父视图不消费，下发给子视图

```
/frameworks/base/core/java/android/view/ViewGroup.java
class ViewGroup extends View {

    public boolean dispatchTouchEvent(MotionEvent ev) {
        int actionMasked = ev.getAction() & MotionEvent.ACTION_MASK;
        TouchTarget newTouchTarget = null;
        boolean intercepted;
      	boolean handled = false;
        if (actionMasked == MotionEvent.ACTION_DOWN|| mFirstTouchTarget != null) {
            // 检查子视图是否调用了 requestDisallowInterceptTouchEvent(true) 请求放行
            boolean disallowIntercept = (mGroupFlags & FLAG_DISALLOW_INTERCEPT) != 0;
            if (!disallowIntercept) {
              intercepted = onInterceptTouchEvent(ev); // 子视图未请求放行，询问 ViewGroup 自身是否需要消费
            } else {
              intercepted = false;
            }
        } else {
            intercepted = true; // 如果 mFirstTouchTarget 为空，并且事件类型不为 DOWN ，表示先前的事件也是 ViewGroup 自己消费的，无需执行分发，再次交给自己执行即可
        }
        return handled;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //询问 ViewGroup 自身是否需要处理事件
        return false;
    }
  
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
      	if (disallowIntercept) {
          	mGroupFlags |= FLAG_DISALLOW_INTERCEPT;
      	} else {
          	mGroupFlags &= ~FLAG_DISALLOW_INTERCEPT;
      	}
  	}
}
```

## View#onTouchEvent()