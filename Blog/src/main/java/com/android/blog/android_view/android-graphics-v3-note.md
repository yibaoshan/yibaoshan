#### 疑问区❓

- BufferQueue在哪个进程？貌似是一个单独的service，但是否是单独进程还需确认
- 创建一个activity或者surfaceview会申请一个graphicbuffer吗？graphicbuffer
- 既然graphicbuffer贯穿全文，sf和GPU都可以用，那么native从哪一层开始创建/拥有了它，surface还是layer
- HWC1和HWC2区别
- APP通过Choreographer感知vsync信号，进而去更新Render List，那么和渲染线程里面渲染动作是同一个vsync周期吗？

- 每日更新

  > 图形显示流程大致分为三个阶段，bufferqueue和graphicbuffer
  >
  > - 绘制/渲染阶段
  >
  >   > 此阶段由上层应用，比如OpenGL/Vulkan/Skia等引擎绘制到graphicbuffer
  >   >
  >   > 对应应该是从wms获取一个Surface
  >
  > - 合成阶段
  >
  >   > 此阶段由surfaceflinger调用OpenGL或者HWC来合成layer
  >   >
  >   > 此阶段对应数据应该是layer
  >
  > - 送显阶段
  >
  >   > 此阶段调用Android的显示框架
  >   >
  >   > Android显示框架进化过程，从Framebuffer架构进化到Google自己设计的ADF框架
  >   >
  >   > 再从ADF框架进化到DRM框架
  >
  > 通过gralloc管理图形内存
  >
  > 剩下还有fence同步机制和传递数据的bufferqueue
  >
  > 
  
- GPU

  > GPU的职责是根据一个三维场景中的顶点、纹理等信息，转换成一张二维图像
  >
  > 通常把一个渲染流程分3个阶段：
  >
  > - 应用阶段
  >
  >   > 应用阶段由CPU完成，几何阶段 和 光栅化阶段 由GPU完成。
  >
  > - 几何阶段
  >
  >   > - 顶点数据
  >   >
  >   > OpenGL规定图形是由点，线，三角形组成
  >   >
  >   > 对应1个顶点，2个顶点，3个顶点。这三个又称为**图元**。点和线一般在2D场景使用，在3D场景，基本是由N多个三角形，来拼接成一个物体模型
  >   >
  >   > 通常表示为顶点数组**VertexArray**和顶点缓冲区**VertexBuffer**
  >   >
  >   > 在3D开发中，一组顶点数据，一般会包含顶点坐标 + 纹理坐标
  >   >
  >   > - 顶点着色器
  >   >
  >   > 顶点着色器是GPU内部流水线的第一步，每个顶点，都会调用一次顶点着色器
  >   > 它的主要工作：坐标转换和逐顶点光照，以及准备后续阶段（如片元着色器）的数据。
  >
  > - 光栅化阶段
  >
  >   > 计算每个图元都覆盖了哪些像素，然后给这些像素着色
  >   >
  >   > - 光栅化就是把顶点数据转换为片元的过程。具有将图转化为一个个栅格组成的图像的作用。片元中的每一个元素对应于帧缓冲区的一个像素
  >   > - 光栅化其实是一种将几何图元变为二维图像的过程。该过程包含了两部分的工作。第一部分工作：决定窗口坐标中的哪些整型栅格区域被基本图元占用；第二部分工作：分配一个颜色值和一个深度值到各个区域。光栅化过程产生的是片元
  >   > - 3d 坐标如何转换成 2D的屏幕坐标

- HWC（Hardware Composer）

  > Google的提供的协议标准，对手机SOC来说，通常指的是DPU（Display Processing Unit）
  >
  > 如果SOC没有DPU，那么就调用GPU来合成
  >
  > 如果连GPU也没有，那也不要紧，libGLES_android库可以支持上层调用OpenGL ES的函数，不过执行者是CPU而已
  >
  > 如果连CPU也没有，那..那系统也启动不了
  >
  > 
  
- OpenGL ES

  > 规定GPU实现标准协议，掌握了OpenGL ES的语法
  >
  > - OpenGL是一个跨编程语言、跨平台的编程图形程序接口，它将计算机的资源抽象称为一个个OpenGL的对象，对这些资源的操作抽象为一个个的OpenGL指令
  >
  > - OpenGL ES是OpenGL三维图形API的子集，针对手机、PDA和游戏主机等嵌入式设备而设计的，去除了许多不必要和性能较低的API接口
  >
  > - [EGL](https://www.khronos.org/registry/EGL/)
  >
  >   > [api地址](https://www.khronos.org/registry/EGL/sdk/docs/man/html/)
  >   >
  >   > eglCreateClientImage对象是CPU和GPU共享内存对象
  >
  > - 在Android中使用ES的方式
  >
  >   > 需要使用EGL，也就是Android版本的OpenGL ES具体实现
  >   >
  >   > - Java层
  >   >
  >   >   > - 自定义类直接继承GLSurfaceView，然后我们重点就主要写Render方法就行
  >   >   > - 自己搭建EGL（是OpenGL ES和本地窗口系统的接口，不同平台上EGL配置是不一样的，而OpenGL的调用方式是一致的，就是说：OpenGL跨平台就是依赖于EGL接口）环境，然后开启自己的EGL线程，后续使用和继承GLSurfaceView基本是一致的
  >   >
  >   > - native层

- Skia

  > 一套2D渲染的api协议，类似的还有cairo，Skia的核心是canvas
  >
  > Skia依赖的第三方库众多，包括字体解析库freeType,图片编解码库libjpeg-turbo、libpng、libgifocode、libwebp和pdf文档处理库fpdfemb等。Skia支持多种软硬件平台，既支持ARM和x86指令集，也支持OpenGL、Metal和Vulkan低级图形接口。
  >
  > https://skia.googlesource.com/skia/

- SurfaceFlinger

  > Fence同步机制，APP进程、surfaceflinger进程、hwc、GPU驱动，四个方面的同步机制
  >
  > 在文章开始之前，有必要提醒了解什么是生产者消费者模式
  >
  > 了解几个概念，生产者消费者，绘制渲染合成送显
  >
  > 摒弃几个概念，hal，hwc，GPU
  >
  > 本篇文章将会从屏幕的角度展开，
  >
  > 文章中隐藏了大量的代码细节，在绘制过程中任何一个环节都可以单独写一篇文章来介绍
  >
  > 为后续的源码阅读提供支撑
  >
  > 以架构设计来看
  >
  > 以服务进程来看
  >
  > - 绘制图层
  >
  >   > APP通过OpenGL指令把图形结果绘制到graphicbuffer对象
  >   >
  >   > layer最终绘制
  >
  > - 合成图层
  >
  >   > surfaceflinger会调用GPU或者hwc进行合成
  >   >
  >   > framebuffersurface最终合成
  >
  > - 送显
  >
  >   > 交由framebuffer或者drm

- Fence同步机制

  > **这就需要一种不仅是跨进程的，也是跨硬件的同步机制: Fence 机制**

- **Graphic Buffer**

  > 硬件共享内存，由Gralloc分配，由bufferqueue管理
  >
  > 在板子上，CPU和GPU和DPU属于不同的硬件，graphic buffer这块内存被这几个硬件共享，这样就能减少复制，提高效率
  >
  > - **Server端**：**Allocate**
  >
  >   > 分配Buffer，由 SurfaceFlinger负责（也可以用单独的服务，这个可以配置，后面默认就用SF）；**只有两个接口：alloc与free**
  >   >
  >   > 为了减小SF的负载，Android S开始强制**Client端分配buffer，**而linux上早已如此处理。
  >
  > - **Client端**：**Mapper**
  >
  >   > GraphicBufferMapper ：应用使用Buffer，由GPU填东西了；**接口主要为：importBuffer（之前是registerBuffer），lock**，导入当前进程地址空间。
  >
  > - **BufferQueue**
  >
  >   > **BufferQueue对App端的接口为IGraphicBufferProducer，实现类为Surface**，对SurfaceFlinger端的接口为IGraphicBufferConsumer，实现类为SurfaceFlingerConsumer（最新版本改名了，但不影响讨论）。
  >   >
  >   > BufferQueue中对每一个GraphiBuffer都有BufferState标记着它的状态，
  >   >
  >   > > 比如new Surface是不会真正分配的，只有在**dequeuBuffer的时候才会请求分配，此时会调用**new GraphicBuffer则会真正分配。
  >   > > 在状态分配时，对于Client端有dequeueBuffer(请求), queueBuffer（绘制结束，发送至服务端） ；

- Android显示框架演变

  > FB框架->[ADF](https://lwn.net/Articles/565422/)->DRM
  >
  > linux给userspace提供的屏幕操作的接口，通过这组接口来向屏幕来提交我们所绘制的画面
  >
  > Android的display架构中是谁在使用这组api呢？
  >
  > - DRM
  >
  >   > DRM是linux内核的一个子系统，它提供一组API，用户空间程序可以通过它发送画面数据到GPU或者专用图形处理硬件（如高通的MDP）
  >   >
  >   > drm 是一个管理 GPU 的显示框架
  >   > 在内核级别提供内存管理，中断处理， DMA控制
  >
  > - ADF
  >
  >   > ADF(Atomic Display Framework)是Google新推出的一个关于Display驱动的框架
  >
  > | **SurfaceView**      | **Java**                                                     | **/[frameworks](http://aospxref.com/android-12.0.0_r3/xref/frameworks/)/[base](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/)/[core](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/core/)/[java](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/core/java/)/[android](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/core/java/android/)/[view](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/core/java/android/view/)/[SurfaceView.java](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/core/java/android/view/SurfaceView.java)** |
  > | -------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
  > | **JNI**              | **无**                                                       |                                                              |
  > | **SurfaceControl**   | **Java**                                                     | **/[frameworks](http://aospxref.com/android-12.0.0_r3/xref/frameworks/)/[base](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/)/[core](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/core/)/[java](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/core/java/)/[android](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/core/java/android/)/[view](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/core/java/android/view/)/[SurfaceControl.java](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/core/java/android/view/SurfaceControl.java)** |
  > | **JNI**              | **/[frameworks](http://aospxref.com/android-12.0.0_r3/xref/frameworks/)/[base](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/)/[core](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/core/)/[jni](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/core/jni/)/[android_view_SurfaceControl.cpp](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/core/jni/android_view_SurfaceControl.cpp)** |                                                              |
  > | **Surface**          | **Java**                                                     | **/[frameworks](http://aospxref.com/android-12.0.0_r3/xref/frameworks/)/[base](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/)/[core](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/core/)/[java](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/core/java/)/[android](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/core/java/android/)/[view](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/core/java/android/view/)/[Surface.java](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/core/java/android/view/Surface.java)** |
  > | **JNI**              | **/[frameworks](http://aospxref.com/android-12.0.0_r3/xref/frameworks/)/[base](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/)/[core](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/core/)/[jni](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/core/jni/)/[android_view_Surface.cpp](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/core/jni/android_view_Surface.cpp)** |                                                              |
  > | **BLASTBufferQueue** | **Java**                                                     | **/[frameworks](http://aospxref.com/android-12.0.0_r3/xref/frameworks/)/[base](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/)/[graphics](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/graphics/)/[java](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/graphics/java/)/[android](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/graphics/java/android/)/[graphics](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/graphics/java/android/graphics/)/[BLASTBufferQueue.java](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/graphics/java/android/graphics/BLASTBufferQueue.java)** |
  > | **JNI**              | **/[frameworks](http://aospxref.com/android-12.0.0_r3/xref/frameworks/)/[base](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/)/[core](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/core/)/[jni](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/core/jni/)/[android_graphics_BLASTBufferQueue.cpp](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/core/jni/android_graphics_BLASTBufferQueue.cpp)** |                                                              |

- 沧浪之水-**概念**

  > **在这里各种Surface，Window，View、Layer实在是太乱了，下面就区分一下这些概念**
  >
  > Window -> DecorView-> ViewRootImpl -> WindowState -> Surface -> Layer 是一一对应的。
  > 一般的Activity包括的多个View会组成View hierachy的树形结构，只有最顶层的DecorView，也就是根结点视图，才是对WMS可见的，即有对应的Window和WindowState。
  >
  > 一个应用程序窗口分别位于应用程序进程和WMS服务中的**两个Surface对象**有什么区别呢？
  > 虽然它们都是用来操作位于SurfaceFlinger服务中的同一个Layer对象的，不过，它们的操作方式却不一样。具体来说，就是位于应用程序进程这一侧的Surface对象负责绘制应用程序窗口的UI，即往应用程序窗口的图形缓冲区填充UI数据，而位于WMS服务这一侧的Surface对象负责设置应用程序窗口的属性，例如位置、大小等属性。
  > 这两种不同的操作方式分别是通过C++层的Surface对象和SurfaceControl对象来完成的，因此，位于应用程序进程和WMS服务中的两个Surface对象的用法是有区别的。之所以会有这样的区别，是因为**绘制应用程序窗口是独立的，由应用程序进程来完即可，而设置应用程序窗口的属性却需要全局考虑，即需要由WMS服务来统筹安排**。

- 沧浪之水-绘制（**客户端，对应于DRM**）

  > 
  >
  > 绘制完成之后，Android应用程序进程再调用前面获得的Canvas的成员函数unlockAndPost请求显示在屏幕中，其本质上是向SurfaceFlinger服务queue一个Graphic Buffer，以便SurfaceFlinger服务可以对Graphic Buffer的内容进行合成，以及显示到屏幕上去
  >
  > - **软件绘制**
  >
  >   > 软件绘制流程比较简单
  >   >
  >   > 在Android应用程序进程这一侧，每一个窗口都关联有一个Surface。每当窗口需要绘制UI时，就会调用其关联的Surface的成员函数lock获得一个Canvas，其本质上是向SurfaceFlinger服务dequeue一个Graphic Buffer。
  >   > Canvas封装了由Skia提供的2D UI绘制接口，并且都是在前面获得的Graphic Buffer上面进行绘制的，这个Canvas的底层是一个bitmap，也就是说，绘制都发生在这个Bitmap上。绘制完成之后，Android应用程序进程再调用前面获得的Canvas的成员函数unlockAndPost请求显示在屏幕中，其本质上是向SurfaceFlinger服务queue一个Graphic Buffer，以便SurfaceFlinger服务可以对Graphic Buffer的内容进行合成，以及显示到屏幕上去。
  >
  > - **硬件绘制**
  >
  >   > 硬件加速渲染和软件渲染一样，在开始渲染之前，都是要先向SurfaceFlinger服务dequeue一个Graphic Buffer。不过对硬件加速渲染来说，这个Graphic Buffer会被封装成一个ANativeWindow，并且传递给Open GL进行硬件加速渲染环境初始化。
  >   > 在Android系统中，ANativeWindow和Surface可以是认为等价的，只不过是ANativeWindow常用于Native层中，而Surface常用于Java层中。 Open GL获得了一个ANativeWindow，并且进行了硬件加速渲染环境初始化工作之后，Android应用程序就可以调用Open GL提供的API进行UI绘制了，绘制出来内容就保存在前面获得的Graphic Buffer中。
  >   > 当绘制完毕，Android应用程序再调用libegl库（一般由第三方提供）的eglSwapBuffer接口请求将绘制好的UI显示到屏幕中，其本质上与软件渲染过程是一样的。
  >
  > - **MainThread和RenderThread的分离**
  >
  >   > 在Android 5.0之前，Android应用程序的Main Thread不仅负责用户输入，同时也是一个OpenGL线程，也负责渲染UI。通过引进Render Thread，我们就可以将UI渲染工作从Main Thread释放出来，交由Render Thread来处理，从而也使得Main Thread可以更专注高效地处理用户输入，这样使得在提高UI绘制效率的同时，也使得UI具有更高的响应性。
  >   >
  >   > 
  >   >
  >   > 对于上层应用而言，UI thread仍然是Main Thread，它并不清楚Render Thread的存在，而**对于SurfaceView，UI thread不是Main Thread，而是重新启用了一个新的线程**。
  >
  > - **Render Node**
  >
  >   > Android 5之后，在Android应用程序窗口中，每一个View都抽象为一个[Render Node](https://developer.android.com/reference/android/graphics/RenderNode)
  >   >
  >   > 如果一个View设置有Background，这个Background也被抽象为一个Render Node
  >   >
  >   > app在onDraw方法中是创建displayList
  >   >
  >   > 
  >   >
  >   > 每一个Render Node都关联有一个Display List Renderer。这里又涉及到另外一个概念——**Display List**。Display List是一个绘制命令缓冲区。也就是说，当View的成员函数onDraw被调用时，我们调用通过参数传递进来的Canvas的drawXXX成员函数绘制图形时，我们实际上只是将对应的绘制命令以及参数保存在一个Display List中。接下来再通过Display List Renderer执行这个Display List的命令，这个过程称为Display List Replay。
  >   > 引进Display List的概念有什么好处呢？主要是两个好处。
  >   > 第一个好处是在下一帧绘制中，如果一个View的内容不需要更新，那么就不用重建它的Display List，也就是不需要调用它的onDraw成员函数。
  >   > 第二个好处是在下一帧中，如果一个View仅仅是一些简单的属性发生变化，例如位置和Alpha值发生变化，那么也无需要重建它的Display List，只需要在上一次建立的Display List中修改一下对应的属性就可以了，这也意味着不需要调用它的onDraw成员函数。这两个好处使用在绘制应用程序窗口的一帧时，省去很多应用程序代码的执行，也就是大大地节省了CPU的执行时间。
  >   >
  >   > 
  >   >
  >   > 注意，只有使用硬件加速渲染的View，才会关联有Render Node，也就才会使用到Display List。对于使用了软件方式来渲染的View，具体的做法是创建一个新的Canvas，这个Canvas的底层是一个Bitmap，也就是说，绘制都发生在这个Bitmap上。绘制完成之后，这个Bitmap再被记录在其Parent View的Display List中。而当Parent View的Display List的命令被执行时，记录在里面的Bitmap再通过Open GL命令来绘制。

- 沧浪之水-合成（**服务端，对应于KMS**）

  > FB框架到DRM框架是整个框架的改变，对于上层用户来说，’/dev/fb0’是找不到了，但在drm框架下兼容FB接口还是非常简单的，主要看OEM厂商的意愿了。
  >
  > *还需要兼容这些接口吗？那就看谁在使用FB/ADF/DRM的接口呢？*
  >
  > **其一，Android启动后，hwcomposer就是唯一的使用者；**
  >
  > **其二，Android启动前，大约有下面3个情况：**
  >
  > - ***Recovery——显示调用比较简单，可以通过这个了解drm的基本显示流程；***
  > - ***关机充电；***
  > - ***开机从kernel到android的启动前这段时间；基本不会主动绘制（通常是一个logo，由bootloader传过来），这是在kernel完成的***
  >
  > **可以看出来使用这些接口都是系统完成的，由谷歌自己完成了升级，第三方是没有权限调这样的接口的，兼容是没必要了。**

- 努比亚团队

  > 在Android系统上应用要绘制一个画面，首先要向SurfaceFlinger申请一个画布，这个画布所使用的buffer是SurfaceFlinger通过allocator service（vendor.qti.hardware.display.allocator-service）来分配出来的，allocator service是通过ION从kernel开辟出来的一块共享内存，这里申请的都是每个图层所拥有独立buffer, 这个buffer会共享到HWC Service里，由SurfaceFlinger来作为中枢控制这块buffer的所有权，其所有权会随状态不同在App, SurfaceFlinger, HWC Service间来回流转。【有误】
  >
  > - App到SurfaceFlinger
  >
  >   > 应用首先通过Surface的接口向SurfaceFlinger申请一块buffer,  需要留意的是Surface刚创建时是不会有buffer被alloc出来的，只有应用第一次要绘制画面时dequeueBuffer会让SurfaceFlinger去alloc buffer, 在应用侧会通过importBuffer把这块内存映射到应用的进程空间来
  >   >
  >   > 
  >   >
  >   > 之后App通过dequeueBuffer拿到画布， 通过queueBuffer来提交绘制好的数据
  >   >
  >   > 
  >   >
  >   > HWC Service负责将SurfaceFlinger送来的图层做合成，形成最终的画面，然后通过drm的接口更新到屏幕上去（注意：在DRM一章中给出的使用DRM的例子子demo的是通过page flip方式提交数据的，但hwc service使用的是另一api atomic commit的方式提交数据的，drm本身并不只有一种方式提交画面）
  >
  > - SurfaceFlinger到HWC Service
  >
  >   > HWC Service的代码位置在 hardware/qcom/display, HWC Service使用libdrm提交帧数据的地方我们可以在systrace上观察到：
  >
  > - HWC Service到kernel
  >
  >   > hwc service通过drmModeAtomicCommit接口向kernel提交合成数据：
  >   >
  >   > drmModeAtomicCommit通过ioctl调用到kernel：
  >   >
  >   > kernel/msm-5.4/techpack/display/msm/msm_atomic.c
  >
  > 在本章中我们了解了APP绘画的画布是由SurfaceFlinger提供的，而画布是一块共享内存，APP向SurfaceFlinger申请到画布，是将共享内存的地址映射到自身进程空间。 App负责在画布上作画，画完的作品提交给SurfaceFlinger， 这个提交操作并不是把内存复制一份给SurfaceFlinger，而是把共享内存的控制权交还给SurfaceFlinger，  SurfaceFlinger把拿来的多个应用的共享内存再送给HWC Service去合成，   HWC Service把合成的数据交给DRM去输出完成app画面显示到屏幕上的过程。为了更有效地利用时间这样的共享内存不止一份，可能有两份或三份，即常说的double buffering, triple buffering.
  >
  > 那么我们就需要设计一个机制可以管理buffer的控制权，这个就是BufferQueue.
  >
  > - BufferQueue
  >
  >   > 在BufferQueue的设计中，一个buffer的状态有以下几种：
  >   >
  >   > **FREE** ：表示该buffer可以给到应用程序，由应用程序来绘画
  >   >  **DEQUEUED**:表示该buffer的控制权已经给到应用程序侧，这个状态下应用程序可以在上面绘画了
  >   >  **QUEUED**: 表示该buffer已经由应用程序绘画完成，buffer的控制权已经回到SurfaceFlinger手上了
  >   >  **ACQUIRED**:表示该buffer已经交由HWC Service去合成了，这时控制权已给到HWC Service了
  >
  > - 画布的申请
  >
  >   > 在Android系统中每个Activity都有一个独立的画布（在应用侧称为Surface,在SurfaceFlinger侧称为Layer）， 无论这个Activity安排了多么复杂的view结构，它们最终都是被画在了所属Activity的这块画布上，当然也有一个例外，SurfaceView是有自已独立的画布的，但此处我们先只讨论Activity画布的建立过程。
  >
  > - 绘制的流程
  >
  >   > 首先App每次开始绘画都是收到一个vsync信号才会开始绘图（这里暂不讨论SurfaceView自主上帧的情况），应用是通过Choreographer来感知vsync信号, 在ViewRootImpl里向Choreographer注册一个callback, 每当有vsync信号来时会执行mTraversalRunnable:
  >   >
  >   > 































标题：为什么关机了还能显示充电画面？

SurfaceFlinger图形系统

标题：View和ViewGroup，图像容器

#### **概念**

> **在这里各种Surface，Window，View、Layer实在是太乱了，下面就区分一下这些概念**
>
> Window -> DecorView-> ViewRootImpl -> WindowState -> Surface -> Layer 是一一对应的。
> 一般的Activity包括的多个View会组成View hierachy的树形结构，只有最顶层的DecorView，也就是根结点视图，才是对WMS可见的，即有对应的Window和WindowState。
>
> 一个应用程序窗口分别位于应用程序进程和WMS服务中的**两个Surface对象**有什么区别呢？
> 虽然它们都是用来操作位于SurfaceFlinger服务中的同一个Layer对象的，不过，它们的操作方式却不一样。具体来说，就是位于应用程序进程这一侧的Surface对象负责绘制应用程序窗口的UI，即往应用程序窗口的图形缓冲区填充UI数据，而位于WMS服务这一侧的Surface对象负责设置应用程序窗口的属性，例如位置、大小等属性。
> 这两种不同的操作方式分别是通过C++层的Surface对象和SurfaceControl对象来完成的，因此，位于应用程序进程和WMS服务中的两个Surface对象的用法是有区别的。之所以会有这样的区别，是因为**绘制应用程序窗口是独立的，由应用程序进程来完即可，而设置应用程序窗口的属性却需要全局考虑，即需要由WMS服务来统筹安排**。

#### 疑问区

> - Window和surface对应关系，最小表示单位是framebuffer吗？一个framebuffer对应的是什么？每个surface都有两个fb吗？
> - framebuffer是一堆数据吧，从哪来，到哪去
> - ，framebuffer的生成过程
> - 绘图方式、格栅化、合成
> - HWC到底是什么？GPU吗？HWC的KMS又是什么？
> - view和canvas之间的关系，view是一张画布，对应的是surface吗？
> - 一个Window上有好多个view吧，Window对应的是啥
> - Java Window和Native surface和Native layer的对应关系？
> - buffer是什么？是像素点数据吗？
> - 三重缓存和vsync和framebuffer对应Android里面的啥
> - 猜想：view不调用失效invalided方法，那么该view就不用重新绘制，调用合成就好了，举例来说，假设你的APP，为了性能考虑，当页面不可见时所有的动画都应该取消，不然一直调用
> - 自定义view中，谁来调用onDraw()方法ss
> - frameData是画面数据

- 底层组件支持

  > surface canvas
  >
  > - ANativeWindow支持GLES和Vulkan协议

- 高级别组件支持

  > - GLSurfaceView拥有GL环境的surface
  > - SurfaceView继承自View，并提供了一个独立的绘图层Surface，这个Surface在WMS中有自己对应的WindowState，在SF中也会有自己的Layer。你可以完全控制SurfaceView，比如说设定它的大小，所以SurfaceView可以嵌入到View结构树中，需要注意的是，但在Server端（WMS和SF）中，它的Surface与宿主窗口是分离的。这样的好处是对这个Surface的渲染可以放到单独线程去做，渲染时可以有自己的GL context。这对于一些游戏、视频等性能相关的应用非常有益，因为它不会影响主线程对事件的响应。但它也有缺点，因为这个Surface不在View hierachy中，它的显示也不受View的属性控制，所以不能进行Transition，Rotation，Scale等变换，也不能放在其它ViewGroup中，也不能进行Alpha透明度运算

#### 前言

> - 理解Android图形系统并不是一件轻松的事情，Google发布的是Android版本，内核部分拉取的是上游Linux最新的LTS版本，对其做裁剪和增加新功能，下游是以高通为首的芯片供应商，Google定义的HWC都是高通在实现，接着往下才是手机厂商，高通就把google的aosp的代码同步回去，经过修改（硬件抽象层、系统框架、通讯层），形成自己的版本，也就是msm-aosp，手机厂商拿到的通常是msm-aosp，所以我们在学习的时候就很痛苦，你不但要看Google的AOSP，还要看下游供应商是如何实现的，要看如何实现的还得去查高通display框架设计，不同的版本可能还会有变化，就很痛苦
>
> - Android显示框架几经演变，从Linux FB框架到自研的ADF框架，再到现在的Linux DRM/KMS框架，HWC也从1.0进化到2.0版本
>
> - 我写文章的目的首先是自己了解，接着才是，本着文章的是让读懂的原则
>
> - 之前的两篇文章中分别介绍了屏幕的显示原理和屏幕的刷新原理，前两篇文章我们了解了屏幕实际上是由一个个像素点组成，本篇文章关注的是framebuffer的产生
>
> - 前面讲了画面撕裂的原因，framebuffer可以理解为屏幕每个像素点的值，包含颜色，深度等信息
>
> - 本章的重点是关心view是如何转变为屏幕像素点数据的？
>
> - 屏幕显示的元素很多，我们自己写的APP一个页面都有好几层视图，这几层是如何变成一副图像信息的呢？这一帧像素矩阵信息是怎么来的呢？
>
> - 众所周知，Google为了改进Android流畅度，在Android 4.1版本发布了project buffer黄油计划，希望Android系统能够像黄油一样丝滑
>
> - 在黄油计划中，Android的绘制被分成了三步，绘制、合成、显示，增加了，对应的vsync被分成了两份，一部分，今天我们来聊一聊
>
> - 发展到2022年，Android图形系统更加复杂，为了减少GPU的压力，设计了HWC的HAL抽象层进行合成，绘制端也加入了UI Renderer
>
> - 至此，文章的标题就有答案了，同理工厂模式也是直接
>
> - 图形系统真他妈复杂，全文完
>
> - 标题，Android  黄油计划
>   - 为了改进Android系统的流畅度，Google在Android 4.1版本发布了Android project butter黄有计划，希望Android系统能够像黄油一样丝滑
>   - 在黄油计划中新增了渲染线程，
>   - 发展到今天，Google为了减缓GPU压力加入hwc等，具体可以看官网以及高通的开发文档
>
> - 结语：对于没有接触过framework开发的同学来说，理解hwc等概念还是有难度的，在文章的结尾，我们来聊一聊Android的hal层，理清hal的概念，对于我们理解Android系统架构有很大的帮助，这一切在知道hal是干嘛的之后就清晰了，不考虑硬件厂商驱动的情况下，Android图形系统是由sf作为中介，framebuffer作为媒介，通过binder传输，最终输出到显存由显示器驱动更新到屏幕
>
> - 注1：Android 7.0以上和图像引擎（高通a系列，armmali系列等）均已支持vulkan协议，本文未包含Vulkan相关内容
>
> - Andorid图形系统有surfaceflinger、wms等组成，内部命名也比较混乱，想要在短期内完全理清有些困难，本文也只是从下至上分析Android支持哪些绘图方式，以及他们是如何实现的
>
>   > 任何一节都可以单独，比如开发者选项中过度绘制的原理
>   >
>   > 为什么关机了还能显示充电画面？
>   >
>   > 为什么flutter可以跨平台
>
> - 结语
>
>   > 本文只是从框架设计的角度聊聊Android系统的图形架构

#### 难点

> - Android图形系统框架一直在改进，由fb改为drm，加入了hwc，所以市面上也没有相关书籍可以从上到下的学习view的绘制流程

#### blog区

> - 加入了
> - 加入了Hardware Composer负责合成

#### 知识区

> - View使用skia，打开硬件加速使用hwui，也就是GPU
> - GPU厂家，除了骁龙使用自家的Adreno系列之外，华为的麒麟、三星的Exynos、联发科的天玑使用的都是ARM亲儿子：mali
> - PC中Linux系统在2012年就已经全部使用DRM框架了
> - PC端的显卡 = 移动端GPU+ Display Processor + Video Processor
> - Android的显示流程分为三个部分，绘制、合成、显示
> - surfacefingler是Android gui的核心，但对于OpenGL来说，sf可以看做应用
> - 抛开Java层不谈，在系统级别是由surfaceflinger和其内部的bufferqueue组成，他们都是c++程序
> - 底层应用我们可以来分析bootanimation，它的内部是借助surfaceflinger来完成的

#### 深入理解Android内核设计思想

> - 疑惑：
>   - 每个应用程序可以有几个bufferqueue，他们的关系是一对一还是多对一/一对多
>   - 应用程序绘制UI所需的内存控件时由谁来分配的
> - Surface对应一个本地窗口
> - bufferqueue是一个服务中心，生产者（应用程序/surface）需要使用一块buffer时，首先会去向中介bufferqueue发起dequeue申请，完成写入后调用queue接口将图形数据入列
> - 前面已经学习了bufferqueue的内部原理，那么应用程序又是如何与之配合的呢？解决这个疑惑的关键是了解应用程序是如何执行绘图流程的，这也是本节内容的重点

Android使用的图形框架

> 原子显示框架ADF：Atomic Display Framework

Andorid图形框架发展史

> Vulkan驱动

OpenGL ES代码在frameworks/native/opengl

Vulkan代码在frameworks/native/vulkan

SurfaceFlinger代码在framework/native/services/surfaceflinger

大致流程

> Android系统提供了三种绘图方式，分别是：Canvas、OpenGL ES和Vulkan
>
> 无论使用哪种，最终内容都会被渲染到surface上，然后交给surfaceflinger管理
>
> **图像生产者**
>
> 图像流生产方可以是生成图形缓冲区以供消耗的任何内容。例如 OpenGL ES、Canvas 2D 和 mediaserver 视频解码器。
>
> **图像消费者**
>
> 图像流的最常见消耗方是 SurfaceFlinger，该系统服务会消耗当前可见的 Surface，并使用窗口管理器中提供的信息将它们合成到屏幕
>
> SurfaceFlinger 是可以修改所显示部分内容的唯一服务。SurfaceFlinger 使用 OpenGL 和 Hardware Composer 来合成一组 Surface
>
> 其他 OpenGL ES 应用也可以消耗图像流，例如相机应用会消耗相机预览图像流。非 GL 应用也可以是使用方，例如 ImageReader 类

合成机制、显示机制

### 概述

#### 操作系统

Android系统是由三个部分组成，看gityuan的吧，这部分不用写了

曾经被问过一个问题，Activity和AndroidX包中的AppCompatActivity的类加载器是同一个吗？

我说是，然后面试官就让我回家等通知..

#### BufferQueue和Gralloc

- Gralloc 

  > Android系统在硬件抽象层中提供了一个Gralloc模块，封装了对帧缓冲区的所有访问操作。用户空间的应用程序在使用帧缓冲区之间，首先要加载Gralloc模块，并且获得一个gralloc设备和一个fb设备。有了gralloc设备之后，用户空间中的应用程序就可以申请分配一块图形缓冲区，并且将这块图形缓冲区映射到应用程序的地址空间来，以便可以向里面写入要绘制的画面的内容。最后，用户空间中的应用程序就通过fb设备来将已经准备好了的图形缓冲区渲染到帧缓冲区中去，即将图形缓冲区的内容绘制到显示屏中去。相应地，当用户空间中的应用程序不再需要使用一块图形缓冲区的时候，就可以通过gralloc设备来释放它，并且将它从地址空间中解除映射。

#### Surface和SufaceHolder

#### SurfaceFlinger和WindowManager

- SurfaceFlinger

  > SurfaceFlinger中需要显示的图层（layer）将通过DisplayDevice对象传递到OpenGLES中进行合成，合成之后的图像再通过HWComposer对象传递到Framebuffer中显示。DisplayDevice对象中的成员变量mVisibleLayersSortedByZ保存了所有需要显示在本显示设备中显示的Layer对象，同时DisplayDevice对象也保存了和显示设备相关的显示方向、显示区域坐标等信息。
  >
  > > ​    DisplayDevice是显示设备的抽象，定义了3中类型的显示设备。引用枚举类位于frameworks/native/services/surfaceflinger/DisplayDevice.h中，定义枚举位于hardware/libhardware/include/hardware/Hwcomposer_defs.h中：
  > >
  > > - DISPLAY_PRIMARY：主显示设备，通常是LCD屏
  > > - DISPLAY_EXTERNAL：扩展显示设备。通过HDMI输出的显示信号
  > > - DISPLAY_VIRTUAL：虚拟显示设备，通过WIFI输出信号

#### 硬件混合渲染器HAL（HWC）

### 渲染引擎

#### Skia库：Canvas 2D绘制

#### OpenGL ES/Vulkan：3D渲染

#### Mediaserver：视频解码器

Android 2D API，代码在/external/skia中，canvas调用的API底层就是由skia库来实现

正文

- 一、开篇

  - 应用开发者的工作中，想更新view很简单，只需要调用invalidate()方法，让当前view失效即可

  - 在Android 4.1黄油计划中，加入了choreographer，它的作用是将vsync信号同步给应用层，目的是把合成和显示分开，减轻GPU的计算量

  - 介绍当前图形框架的发展历史，最终引申到surfaceflinger

  - 对于应用开发者来说，我们只需要关surfaceflinger的工作流程即可

  - 图像流中最常见的消费者是surfaceflinger，同时他也是我们应用开发者需要关注的服务

    sufaceflinger消耗当前可见的surface

- 低级别组件之surfaceflinger

- Choreographer

Android GUI系统 SurfaceFlinger

- 文案

  > 在学习图形体系过程中，对系统中出现的dispaly/gralloc等等一系列陌生的模块感到混乱而无序，鉴于此，我们先从框架设计的角度来观察它们之间错综复杂的关系
  >
  > 程序设计就是这样，越到上层越复杂，因为需要支撑不同的业务
  >
  > Android提供了两种方式，一种是OpenGL ES，另一种是View

- 想法

  > 使用c写一个画面，修改开机画面文字

- OpenGL ES与EGL

  > SurfaceFlinger虽然是GUI系统的核心，但从OpenGL ES的角度来讲，SurfaceFlinger只能算是一个应用程序
  >
  > OpenGL ES是协议，开发商可以选择由软件实现（CPU）或者硬件实现（GPU），OpenGL在启动时会去读取egl.cfg这个配置文件，根据配置文件决定加载哪个so

- Android终端显示设备的化身，gralloc与Framebuffer

  > 所有的使用者需要借助gralloc来打开设备
  >
  > fb0表示“主屏幕”
  >
  > gpu0表示gralloc设备，负责图形缓冲区的分配和释放

- Android中的本地窗口

  > 本地窗口是OpenGL能否兼容多种系统的关键，在Android系统中，至少需要两种本地窗口
  >
  > - 对于管理者surfaceflinger来说，需要直接或间接的持有本地窗口，这个窗口就是FramebufferNativeWindow
  > - 对于应用程序来说，本地窗口是surface
  >
  > 在一个系统设备中只会有一个帧缓冲区

- 角色汇总

  - HAL层

    > HWComposer，有两个composer，一个用于合成layer，一个用于发送vsync信号
    >
    > Gralloc，单一职责，用于分配图像所需的内存

#### 资料

- 《深入理解Android内核设计思想-林学森》
- [Android-SurfaceFlinger图形系统](https://github.com/jeanboydev/Android-ReadTheFuckingSourceCode/blob/master/article/android/framework/Android-SurfaceFlinger%E5%9B%BE%E5%BD%A2%E7%B3%BB%E7%BB%9F.md)
- [针对移动端TBDR架构GPU特性的渲染优化](https://blog.csdn.net/leonwei/article/details/79298381)
- [深刻理解现代移动GPU](https://blog.csdn.net/qq_41028985/article/details/120828912)