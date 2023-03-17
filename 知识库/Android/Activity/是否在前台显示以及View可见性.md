
## Application#ActivityLifecycleCallbacks

如果有处于 onResume() 的 Activity ，即表示前台有页面正在展示

## ActivityManager

利用 AMS 服务，获取 getRunningTasks()

注意申请权限

```
<uses-permission android:name="android.permission.GET_TASKS"/>
```

## 正确获取 View 可见性（拿到宽高）

- onWindowFocusChanged()
- View#post()
- ViewTreeObserver#addOnGlobalLayoutListener()
- View#measure()
