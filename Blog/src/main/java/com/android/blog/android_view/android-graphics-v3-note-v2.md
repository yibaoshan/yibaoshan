#### 1、Note in Code

##### SurfaceFlinger服务

sf的主要两个作用，一是接收/分发vsync信号

1. 接收来自hwc的vsync信号
2. 处理vsync信号，DispSync，在Android 12时已经被删除，可能Google觉得高刷是未来的趋势

二是合成layer

##### DispSync类

DispSync是Android 4.4才加入的类，位置在/frameworks/native/service/surfaceflinger/DispSync.cpp

Android 12删除了该类

##### BufferQueue

Android 4.1开始加入的机制

##### GraphicBuffer

bufferqueue的元素，最重要的

##### BLASTBufferQueue

Android 11开始加入的新的类，Android 12开始正式启用

#### 2、system机制

##### Fence机制

Android 4.0加入的同步机制

##### HWC

两层含义，当表示为vsync时，是用来监听回调屏幕驱动的垂直同步信号

表示为合成时，就是2D引擎，合成方式为DEVICE，否则为GPU合成

#### 3、Process Total

##### APP to screen from fsp的计算

1. APP绘制到buffer，这个buffer是通过dequeueBuffer()申请的
2. 绘制完成之后调用queueBuffer()将buffer还给surfaceflinger区进程合成，sf回调用acquireBuffer()从bufferqueue拿到一个buffer去合成，fence机制会保证sf拿到的是GPU绘制好的
3. 合成完后调用releaseBuffer()将buffer还给bufferqueue
4. sf拿到合成后的Framebuffer去送显？

> 统计在一秒内该 App 往屏幕刷了多少帧，而在 Android 的世界里，每一帧显示到屏幕的标志是： present fence signal 了，因此计算 App 的 fps 就可以转换为：**一秒内 App 的 Layer 有多少个有效 present fence signal 了（这里有效 present fence 是指，在本次 VSYNC-sf 中该 Layer 有更新的 present fence）**





