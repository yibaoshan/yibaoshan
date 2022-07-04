Android GUI系统 SurfaceFlinger

- 文案

  > 在学习图形体系过程中，对系统中出现的dispaly/gralloc等等一系列陌生的模块感到混乱而无序，鉴于此，我们先从框架设计的角度来观察它们之间错综复杂的关系
  >
  > 程序设计就是这样，越到上层越复杂，因为需要支撑不同的业务

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