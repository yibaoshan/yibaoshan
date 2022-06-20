# Android图形架构概述(一)硬件层：LCD和OLED怎么选？

对于应用开发工程师来说，虽然我们不需要写驱动程序，但是了解View是最终如何显示到屏幕上还是非常有必要的

本篇是View系列的第一篇文章，主要讨论的是Android手机屏幕的种类以及和屏幕相关的技术名词

![image_android_view_v1_overview](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/image_android_view_v1_overview.jpg)

## 一、开篇

手机屏幕发展至今，市场上在售手机的屏幕类型基本可以分为两种：**LCD屏和OLED屏**

其实不只是手机屏幕，**电脑**和**电视**屏幕面板基本也就这两种

我们平时在买屏幕时看到的**IPS屏**，**TN屏**等都属于**LCD屏**阵营，他们的区别只是**液晶层结构**或者**背光模组结构**不同

OLED屏幕因为价格比较贵，寿命也比不过LCD屏，所以OLED在显示器和电视机领域还没有大面积应用

但在手机圈，各大厂商自2020年之后生产的旗舰机，几乎都标配OLED屏幕

相较于LCD屏幕

- **OLED屏幕支持更高的亮度、对比度以及更艳丽的色彩**
- **更快的响应速度，高刷体验更好**
- **不需要背光板，采用COP方案封装，理论上可以去掉宽下巴**

在手机屏幕追求超高屏占比的时代，LCD被彻底的抛下车

但是，虽然OLED屏有许多LCD无法比拟的优势，但它同时也带了两个新的问题：**烧屏和低频PWM调光**

### 1、OLED屏缺陷：烧屏

**烧屏**指的是手机屏幕长时间停留在某个静止画面，特别是极个别高对比度画面，再切换到其他画面时仍然能看到原来画面的残影，而且残影不会消失

比如我之前在拼多多上买的二手Pixel 3就发现老化烧屏的现象

![image_android_view_v1_piexl](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/image_android_view_v1_piexl.jpg)

仔细观察**“位置信息”**一栏会发现，在屏幕上残留的有谷歌邮箱的图标icon

如今厂商用各种方案为OLED延长使用寿命，例如定期改变虚拟导航栏的位置、定期调节色温等等，但是现阶段烧屏问题还是会普遍存在的OLED屏幕上

**烧屏是一个不可逆的过程，一旦手机出现烧屏，会严重影响日常的使用体验，不能忍的话只能换屏幕或者换手机**

![image_android_view_v1_bilibili](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/image_android_view_v1_bilibili.jpg)

*图片来源：[B站评论：全网最简洁易懂的OLED与LCD屏幕工作原理与优劣科普](https://www.bilibili.com/video/BV1Wz411B7Tf)*

#### **为什么OLED会烧屏？**

讨论这个问题之前我们先来看**LCD屏和OLED屏的结构组成**

**LCD是由背光模组，下偏光片，液晶层，滤光层和上偏光片组成，通过改变施加在液晶层上的电压大小来控制每个像素点颜色**

![image_android_view_v1_screen_lcd](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/image_android_view_v1_screen_lcd.jpg)

*图片来源：https://zhuanlan.zhihu.com/p/30201452*

**OLED是有机发光材料作发光层，发光层上方有一层低功函数的金属电极，构成如三明治的结构，通过改变施加在自发光二极管上的电压大小来控制每个红黄蓝子像素亮度，进而改变每个像素点的颜色**

![image_android_view_v1_screen_oled](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/image_android_view_v1_screen_oled.jpg)

*图片来源：https://zhuanlan.zhihu.com/p/30201452*

*对OLED发光原理感兴趣的可以看这篇文章《[OLED发光原理和OLED面板结构及OLED关键技术深度图文解析](http://www.51touch.com/technology/touchpanel/201808/28-51282.html)》，文中从发光原理介绍到屏幕驱动技术再到制造和封装等360度无死角覆盖*

LCD屏幕基板是无机玻璃材料，所以几乎不会发生老化，而OLED基板是有机塑料材料，就意味着它会渐渐老化

**LCD要么全亮要么全灭，所以就算老化，也是全部老化，你也看不出来**

**OLED由于每个像素点是自发光，而不是LCD那样整块全部亮，这就会导致OLED每个像素点工作的时间不一样**

**有的像素点显示蓝色的时间长，那么他的蓝色衰减就会比其他像素点多，日后再显示蓝色的时候这一个像素点的蓝色就要比其余的淡一点，同样的红色和绿色也是一样**

**所以OLED屏幕非常容易发生一个现象就是烧屏，其本质就是屏幕老化不均匀导致的残留**

#### **如何减缓烧屏过程**

既然OLED烧屏不可避免，作为消费者我们可以做些什么来减缓烧屏现象呢？

除了定期更换壁纸、调短屏幕**自动锁定**的时间等手段外，我个人发现了一个更好的方案，能够从根本上解决烧屏的可能性：

read more books, read more newspapers, eat less snacks and sleep more

### 2、OLED屏缺陷：低频PWM调光

低频PWM调光是OLED屏上另一个经常被人诟病的问题，所谓的低频调光说得通俗点就是**“频闪”**

![image_android_view_v1_oled_turn_off_rapidly](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/image_android_view_v1_oled_turn_off_rapidly.gif)

*图片来源：https://www.igao7.com/news/201807/oJMDaPCKHbeufGdI.html*

眼睛长期暴露在**“频闪”**环境中会造成头痛和眼疲劳、导致视力下降和注意力分散等问题

观看低频的**频闪**画面还可能会引发[光敏性癫痫](https://baike.baidu.com/item/光敏性癫痫)病，近代最著名的“频闪事故”当属1997年日本电视台的[“3D龙事件”](https://baike.baidu.com/item/%E5%8D%A1%E9%80%9A%E6%98%8F%E8%BF%B7%E4%BA%8B%E4%BB%B6/9026774)

在宝可梦第38话中，为了渲染电脑世界中的战斗，大量使用12Hz的红蓝闪光展示爆炸场面，直接导致日本全国出现了700例癫痫症(650例是儿童)

![image_android_view_v1_pokemon](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/image_android_view_v1_pokemon.gif)

*图片来源：https://www.igao7.com/news/201807/oJMDaPCKHbeufGdI.html*

无独有偶，2007年的伦敦2012年宣传预告片、2011年的《暮光之城：破晓》都有过“因色块闪烁，导致观众癫痫发作”的事件

*注：不建议读者尝试，笔者在写这篇文章时只是看GIF动图都觉得头晕恶心，无法按捺好奇心的同学身边一定要有人陪同再去观看原片，不开玩笑*

#### PWM是什么？

回到我们的主角**PWM调光**。PWM，全称Pulse Width Modulation，翻译过来就是脉冲宽度调制，说到底，就是种把模拟信号调制成脉波的技术，它已经是应用非常广泛的显示器/光源的亮度控制方案

此外，还有我们后面会提到的DC直流调光(LED领域的CCR恒流调光，为方便表示，后续用DC调光代称)

进入主题前我们需要了解什么是“视觉暂留”现象：

**当人看到一幅画面快速闪过时，这幅画面产生的视觉刺激会在大脑中停留几十到几百毫秒时间，亮度越亮，停留的时间越长，这一特征我们称为“视觉暂留”**

PWM就是利用人眼的视觉暂停现象来实现亮度调节的，具体原理可以去B站看“硬件茶谈”的[《全网最简洁易懂的OLED与LCD屏幕工作原理与优劣科普》](https://www.bilibili.com/video/BV1Wz411B7Tf)这期节目

简单总结一下就是：

**在PWM调光屏幕上，调节亮度并不靠改变功率，而是靠屏幕的亮、灭交替显示来改变亮度**

**屏幕亮度为100%时，自发光二极管一直亮，亮度为80%时，一个PWM周期内亮80%的时间在亮，剩下的20%的时间是灭的，屏幕会不停地点亮、熄灭，当交替速度够快时，肉眼就会认为手机一直在亮**

#### PWM相关参数

这里多出了一个**PWM周期**的概念，解释一下

使用PWM调光的手机通常都标有调光频率参数，以我手里的[Pixel 3](https://www.notebookcheck.net/Google-Pixel-3-Smartphone-Review.366326.0.html#toc-9)举例，调光频率为245HZ

**1. PWM频率：**

- **频率是指1秒钟内信号从高电平到低电平再回到高电平的次数(一个周期)，表示单位：HZ**

**2. PWM周期：**

- **一次脉冲周期的时间，Pixel 3调光频率最高245HZ，换算成一个PWM周期为：1s/245≈4ms**

**3. 占空比**

- **一次PWM周期中，高电平与整个周期时间的比例。依旧以Pixel 3举例，高电平时间为2ms时，占空比 = 2ms/4ms=50%**

从这几个关于PWM的参数可以看出来，调光频率越高，人眼发觉频闪的机会就越小，对眼睛的伤害也就越小

所以手机厂商近些年推出的机型都宣传自己使用高频PWM调光技术，没钱换新手机的同学也不用担心OLED屏幕伤眼，平日使用手机时尽量把亮度调高，让高电平持续的时间变长，同样也可以起到降低**“频闪”**，保护眼睛的效果

*注：对自己手机屏幕参数感兴趣的同学可以去这个网站查询：www.notebookcheck.net*

#### 如何分辨是DC调光还是PWM调光

讲完了PWM调光，接下来我们介绍一下如何分辨手机是DC调光还是PWM调光

`1.1`小节介绍了LCD屏幕的结构组成，知道了LCD屏幕背后是一整块的背光模组，所以调整屏幕亮度只需要调整**“背光模组”**的亮度即可，这就是**“DC调光”**

**PWM调光**则是利用人眼的**“视觉残留”**现象，通过调节**“占空比”**来控制屏幕的亮度

去应用商店下载一个可以调节快门速度的第三方相机，调高快门速度拍手机屏幕，被拍摄的手机屏幕亮度别太高，调到50%左右

如下图，有条纹的就是**“PWM调光”**，没有条纹则是**“DC调光”**

![image_android_view_v1_screen_pwm](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/image_android_view_v1_screen_pwm.gif)

我们看到的**黑色条纹**其实就是**PWM周期**中低电平的时间，也就是像素点熄灭的时间

## 二、结语

OLED屏幕烧屏和低频PWM调光都已经介绍完了，目前市面上大多数OLED屏幕使用的都是PWM调光，LCD屏幕则大部分使用DC调光

但我们还是要把屏幕的材质和调光方式区分开来，因为并非所有的OLED屏幕都是PWM调光，同样的，也不是所有的LCD屏幕都是DC调光，例如采用LCD屏幕的荣耀Play使用的就是PWM调光

回到我们的标题，**LCD和OLED屏幕该怎么选？**

就我个人经验来看，我是偏向OLED屏幕的，从iPhone X到现在的iPhone 12Pro，OLED的频闪对我没什么影响，反而还挺喜欢OLED屏带来的更高的亮度，至于烧屏，我的主力机还没有遇到过，真正发生的时候大不了换个屏幕

不过人眼对频闪敏感程度不同，的确有些小伙伴使用OLED屏幕后出现眼睛干涩、眼疲劳等问题，这部分人群建议还是使用DC调光的屏幕

在文章的最后，还是建议大家少玩手机，多出去走走，闻一闻路边的野花，见一见想念的人，才能真正的保护眼睛

## 三、参考资料

- CSDN-[Z小旋](https://blog.csdn.net/as480133937)-PWM频率与占空比详解：https://blog.csdn.net/as480133937/article/details/103439546
- 硬件茶谈-全网最简洁易懂的OLED与LCD屏幕工作原理与优劣科普：https://www.bilibili.com/video/BV1Wz411B7Tf
- PWM调光科普(上篇)-人类显示器的黑历史：https://www.igao7.com/news/201807/oJMDaPCKHbeufGdI.html
- 走进自带光芒的有机发光二极管（OLED）材料绚丽多彩世界，领悟前沿的研究进展：http://www.cailiaoniu.com/232822.html
- 有机发光二极管| 8优点和缺点：https://zh-cn.lambdageeks.com/organic-light-emitting-diodes/
- [OLED发光原理和OLED面板结构及OLED关键技术深度图文解析](http://www.51touch.com/technology/touchpanel/201808/28-51282.html)