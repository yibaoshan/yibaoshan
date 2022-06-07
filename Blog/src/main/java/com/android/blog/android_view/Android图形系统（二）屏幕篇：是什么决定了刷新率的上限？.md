Android图形系统（二）屏幕篇：是什么决定了刷新率的上限？

### 内容汇总

- 补充CRT显示原理
- 介绍CRT等显示器的刷新原理
- 能不能看到屏幕刷新的过程
- 能不能超频手机屏幕刷新率
- 总结部分，回到标题，是什么决定了刷新率的上限
  - 对于CRT显示器来说
  - 对于LCD/OLED来说

### 内容顺序

- 开篇
  - 第一篇文章介绍了某某，在本篇的开始之前，我们需要介绍下老前辈
  - 什么是CRT显示器
  - 它是如何显示的？
- 屏幕是如何刷新的
  - CRT是水平/垂直偏向板
  - LCD和OLED，有源驱动和无源驱动

对于应用开发工程师来说，虽然我们不需要写驱动程序，但是了解View最终是如何显示到屏幕上还是非常有必要的

本篇是Android图形系列的第二篇文章，依旧是一些关于屏幕的名词解释，和第一篇文章不同的是，本篇重点在于介绍不同类型的屏幕是如何刷新的

以及，从屏幕刷新原理的角度来解释，决定刷新率上限的因素有哪些？

我是概览图

# 一、开篇

在Android图形系列的第一篇文章中，我们了解了LCD液晶屏和OLED屏幕

手机屏幕发展至今，市场上在售手机的屏幕类型基本可以分为两种：**LCD屏和OLED屏**

其实不只是手机屏幕，**电脑**和**电视**屏幕面板基本也就这两种

早在LCD屏和OLED屏诞生之前，我们使用的显示器大多都是CRT显示器

![image_android_view_v2_crt_overview](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/second/image_android_view_v2_crt_overview.gif)

*图片来源：https://gfycat.com/courageousunhealthyirishdraughthorse-computing-recycling-backlit-cathode*

如图所示，这种带着大屁股电脑就是**CRT显示器**，小时候家里的**大屁股彩电**、街机厅里面的**街机**也都是CRT显示器的一种

屏幕刷新中的**“逐行刷新”**技术是来源于**CRT显示器**，为了后续的剧情能够正常发展，在正文开始前我们需要先来了解一下这个快被遗忘的老家伙

### **显示器的鼻祖：CRT**

**CRT显示器**学名为**“阴极射线显像管”**，是一种使用阴极射线管（Cathode Ray Tube）的显示器，主要有五部分组成：**电子枪**、偏**转线圈**、**荫罩**、**高压石墨电极**和**荧光粉涂层**

![image_android_view_v2_crt_dismantle](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/second/image_android_view_v2_crt_dismantle.jpg)

*图片来源：[《计算机图形学基础（第3版）》](https://www.amazon.cn/dp/B07R4LNRR6)*

> [多扫描线彩色crt显像管及多扫描线高清crt图像显示装置：](https://patents.google.com/patent/CN1877782A/zh)
>
> **CRT(Cathode Ray Tube)阴极射线显像管**是一种采用多扫描线彩色CRT显像管实现高清晰度图像显示的装置的应用技术，以及一种多扫描线彩色CRT显像管多组电子束扫描矫正的调整方法
>
> 通过对图像信号进行数字技术处理，以及多扫描线彩色**CRT显像管**的采用，使**CRT显像管**能够实现大屏幕、高亮度、高对比度、高清晰度等性能，以适应HDTV高清晰度数字电视机以及高清晰度图像显示器的技术要求
>
> 一般的**CRT显示器**，如：**CRT电视机**和**CRT电脑显示器**，目前使用的**CRT显像管**都是属于单扫描线CRT显像管
>
> **CRT显像管**是靠电子枪发射电子束轰击荧光粉而发光的，荧光粉发光响应速度快，色彩鲜艳，对比度高，这些优点是其它显示器难以比得及的
>
> 但**CRT显像管**显示器与其它平板显示器相比，也有缺点：**显示器的屏幕越大，屏幕发光亮度和对比度就越低**，因此，目前一般**CRT显像管**显示器的最大屏幕尺寸很难做到32寸以上

**简单来说，CRT屏幕之所以能够显示图像，是靠电子枪发射的电子束来点亮的荧光粉实现的**

我们知道了CRT的显示原理，加上在图形系列的第一篇文章中介绍的**LCD液晶屏**和**OLED屏**，我们就有三种不同类型的显示器屏幕了

**CRT**的显示原理刚刚解释过了，另外两种我们一起来回顾一下：

> - **LCD是液晶面板，通过控制液晶层电压就可以改变像素颜色**
> - **OLED是自发光二极管，控制单个二极管的电流就可以改变像素颜色**

关于CRT屏幕的补充到这就结束了，接下来开始今天的正文：**显示器屏幕是如何刷新的？**

## **二、屏幕刷新原理**

上一小节我们知道了CRT是由电子束轰击点亮荧光粉来显示图形的，而荧光粉的特性就是被点亮后很快就会熄灭了，所以电子枪需要一刻不停的扫描

**那CRT屏幕刷新原理就显而易见了：电子枪快速扫描**

### **1、CRT刷新机制：快速扫描**

电子枪使用的扫描方式有很多，比如直线式扫描，圆形扫描，螺旋扫描等等，我们今天要介绍的是直线式扫描中的“逐行扫描”和“隔行扫描”

- **逐行扫描**

> 逐行扫描是最原始的扫描方式，电视机诞生之初使用的就是逐行扫描
>
> 逐行扫描的意思很简单：在一块CRT的屏幕上，电子枪按照从左到右、从上到下的顺序移动发射电子束的方式就叫做逐行扫描

- **隔行扫描**

> 隔行扫描是在逐行扫描的基础上修改的，扫描的顺序依旧是从左到右、从上到下，只是把原来一行行向下扫描改成了隔一行扫描一行

![image_android_view_v2_crt_scan](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/second/image_android_view_v2_crt_scan.gif)

如图所示，隔行扫描第一次会先扫描奇数行，第二次会扫描偶数行，两次扫描结束后才是一幅完整的图形

**隔行扫描得到的视频质量是不如逐行扫描的视频的，相比之下，隔行扫描图像的清晰度要差一些，而且还会伴有轻微的闪烁**

**这里可能有同学要问：既然隔行扫描的质量不如逐行扫描，那为什么还要发明它呢？**

**答：因为市场有需求**

- **隔行扫描的诞生背景**

> 早期的广播电视播放内容比较单一，大部分是文字广告和音乐之类的内容，电影还是使用“电影放映机”来播放的
>
> 电视机发明后，人们想把电影节目也搬到电视节目当中，但是当时的**广播电视传输带宽不够**，所以人们就想了个办法：将一幅画面的数据按照奇偶数拆成两场，其中奇数行称为上场，偶数行称为下场，分成**两次传输**
>
> 电视机每次接收到的也是半幅画面，在PAL制式和NTSC制式中，显示器接受到数据后会先显示奇数行，再显示偶数行，两次显示后凑成一幅完整的图像
>
> 再后来，DVD被发明出来，相当于电视机播放的是本地视频，广播传输的带宽不再是瓶颈，所以后来的CRT电视机逐行和隔行都支持，我们小的时候看的大屁股电视，只要带有DVD接口的都是可以兼容两种不同的扫描方式的



### **2、LCD/OLED：大人 时代变了**

时间来到了1968年，第一块LCD液晶屏横空出世了

经过了36年的发展，在2004年年末爆发了液晶电视革命，LCD液晶屏幕在亮度、色域、可视角度等各种参数上都完虐老大哥CRT；和液晶屏幕比，CRT屏幕除了在刷新率上有优势外，其他方面都是弟弟

于是乎从2005年开始，夏普、松下、三菱等各大屏幕厂商都陆续宣布停产CRT显示器

2012年，随着最后一家CRT显示器厂家**彩虹电子**宣布停产，CRT时代彻底宣告落幕

..

扯远了，说回液晶屏刷新的事儿

- **驱动原理：有源驱动**

> 首先我们得知道，LCD液晶屏和OLED屏的背后都是有电路板的，那想要控制每个像素点发光显示，就需要有驱动
>
> 驱动又分为两种，一种是**无源驱动**，是指在N*N的像素矩阵由水平垂直两根线来驱动，点亮像素只需要找到它在XY的坐标轴即可，缺点是不能持续的给每个像素通电，只能通过高频的点亮/熄灭像素来维持画面，和之前介绍的PWM调光原理类似
>
> 另一种叫**有源驱动**，TFT-LCD和OLED阵营的AMOLED屏都是属于有源驱动的方式，有源驱动简单来说是在每个像素点后面加入可以单独控制发光的开关，这样每个像素点就可以单独控制了，这样做的好处也显而易见，每个像素都可以持续发光

我在网上没有查到无源驱动和有源驱动各自的市场占用率，我们常见的LCD液晶屏中的**TFT-LCD屏**，和OLED屏的**AMOLED屏**都是使用**有源驱动**的方式，所以接下来关于屏幕刷新的所有都是建立在**有源驱动**之上，也叫**有源矩阵**

- **LCD液晶屏刷新原理**

> 前面介绍了LCD屏幕的大多使用有源驱动的方式，因为有电路驱动的原因，使得屏幕上每个像素点都可以直接寻址，无需等待电子光束到来才能更新
>
> **那是不是可以理解成LCD不需要逐行扫描技术了呢？**
>
> 答案是：NO
>
> **虽然LCD屏幕可以直接寻址来更新像素点，但随着屏幕分辨率越来越高，一次性更新所有像素点的代价也变得越高**
>
> 我们来看一下屏幕驱动从显存读取数据并显示到屏幕这个过程
>
> ![image_android_view_v2_lcd_ddram](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/second/image_android_view_v2_lcd_ddram.png)
>
> *图片来源：[《理解 LCD 屏幕的驱动原理与调试过程》](https://www.cnblogs.com/juwan/p/13069102.html)*
>
> 看上图，我们重点关注“显示内存（DDRAM）”这个词儿，后续简称显存
>
> 显存里面保存是一幅画面的帧数据，显示控制驱动每次会以一行或者多行的方式读取数据，接着去刷新屏幕像素
>
> 刷新完一帧后驱动便会进入等待状态，等待时间通常是由屏幕刷新率决定的，如果是60HZ，那等到16ms之后驱动再次开始工作

- **OLED屏刷新原理**

  > OLED的显示原理虽然比LCD液晶屏要先进一些，但是在刷新方式上和LCD基本上相同，都是**有源驱动**的方式，所以直接参考LCD液晶屏刷新原理就可以了



### 3、如何才能看到屏幕刷新的过程？

讲了这么多，可不可以观察到屏幕刷新的过程呢？感受一下显示器是如何点亮一个个像素点的？

**可以！**

YouTube上有个专门拍摄慢动作的团队：[The Slow Mo Guys](https://www.youtube.com/user/theslowmoguys)

他们在2018年发布一个叫《[How a TV Works in Slow Motion - The Slow Mo Guys](https://www.youtube.com/watch?v=3BJU2drrtCM)》的视频，视频内容就是介绍不同屏幕类型的电视在高速摄像机里是什么样子的，我截几张图大家感受一下：

![image_android_view_v2_slow_crt](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/second/image_android_view_v2_slow_crt.gif)

**CRT的逐行刷新，从动图里就能很明显的看到扫描枪的扫描线按照从左到右，从上到下的顺序扫描更新**

![image_android_view_v2_slow_lcd_tv](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/second/image_android_view_v2_slow_lcd_tv.gif)

**LCD液晶电视的截图，动图里看起来像是整副画面一起更新，实际上我用PR放慢来看是多行像素一起更新**

![image_android_view_v2_slow_lcd_phone](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/second/image_android_view_v2_slow_lcd_phone.gif)

**动图里是使用LCD液晶屏的iPhone 7手机，原视频太小了看起来很模糊，不过依稀还是可以看到屏幕是一行行像素刷新的**

