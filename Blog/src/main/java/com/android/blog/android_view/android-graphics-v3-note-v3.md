### Overview

### 一、开篇

#### 硬件介绍

##### 1、GPU

##### 2、DPU

#### 系统支持

##### 1、Fence机制

##### Gralloc

##### 2、什么是硬件加速？

总结如图，开启硬件加速对于APP来说导致的

1. 开启RenderThread，将会在systrace中体现
2. 由于厂商策略不同，GPU硬件可能并没能呈现预期效果

### 二、Vsync 系统的总指挥

#### Activity的创建

##### 1、SF创建layer

##### 2、PWM创建Window/Surface

##### 3、创建ViewRootImpl

#### Vsync生产与处理

##### 1、第一帧，APP进程绘制与渲染

##### 2、第二帧，SF合成

##### 3、第三帧，DRM/KMS显示

##### 4、新同学的加入：RenderThread

##### 5、如何暂停接收Vsync信号？

我们打开APP后没有进行任何操作，APP还会执行渲染流程吗？

答案显然是否定的



### 三、结语

