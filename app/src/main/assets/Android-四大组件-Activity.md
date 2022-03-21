### Overview
1. 生命周期
2. 启动方式和使用场景
3. 启动流程
4. 通信方式
5. 常见问题

### 一、生命周期

1. 正常流程：

```

onCreate():调用attach方法赋值PhoneWindow、ContextImpl等属性，Activity进入ON_CREATE状态
    ->onStart():准备进入前台时调用，可以理解为其他Activity都已经进入到后台，Activity进入ON_START状态
        ->onResume():准备交互时调用，首次启动时DecorView未获得token，再次调用时可以在此方法操作UI，Activity进入ON_RESUME状态
            ->onPause():失去焦点时调用，此时Activity可见但不可交互，Activity进入ON_PAUSE状态
                ->onStop():不可见时调用，新页面执行onResume()时调用，新页面不会等待onStop执行完毕之后再显示，所以在onStop方法里做耗时操作不会影响新页面，Activity进入ON_START状态
                    ->onDestroy():被系统kill或调用finish()方法时调用，Activity进入ON_DESTROY状态

```

2. Activity页面发生切换

```

一：当从页面A跳转到页面B时的生命周期变化：

A:onPause()
    ->B:onCreate()->onStart()->onResume()
        ->A:onStop()

二：当从页面B跳转回页面A时的生命周期变化：

B:onPause()
    ->A:onRestart()->onStart()->onResume()
        ->B:onStop()->onDestroy()

三：当从页面B跳转回页面A时的生命周期变化(带返回值setResult())：

B:onPause()
    ->A:onActivityResult()->onRestart()->onStart()->onResume()
        ->B:onStop()->onDestroy()

```

3. Activity配置发生变化(屏幕旋转)

```

onPause -> onStop -> onSaveInstanceState -> onDestroy
onCreate -> onStart -> onRestoreInstanceState -> onResume

```

4. 重复启动(onNewIntent)

```

onPause() -> onNewIntent() -> onResume()

```

5. 其他

```

onSaveInstanceState():每次进入后台时调用，调用时机随API不同而不同，在api 28(Android 9)中在onStop()之后调用，低版本在stop之前调用，且与onPause之间的顺序是不确定的，正常情况下，每个view都会重写这两个方法
onRestoreInstanceState(): 调用顺序onCreate -> onStart -> onRestoreInstanceState -> onResume

```

### 五、常见问题

- 为什么Application启动Activity不加NEW_TASK会发生崩溃？
- 旋转屏幕导致Activity重建问题的解决办法
