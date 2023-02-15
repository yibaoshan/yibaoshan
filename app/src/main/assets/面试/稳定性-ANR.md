
## Input ANR

每次分发事件以后，都会调用 processAnrsLocked() 方法检查上次分发是否超时

```
InputDispatcher::dispatchOnce() 
->InputDispatcher::processAnrsLocked()
```

如果当前时间已经满足超时时间（默认 5s），则触发 onAnrLocked()