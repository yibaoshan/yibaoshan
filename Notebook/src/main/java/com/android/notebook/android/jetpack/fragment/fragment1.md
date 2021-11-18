### 【AndroidX】Fragment（一）：介绍和使用说明

##### fragment出现是为了解决导航栏和内容区，参考微信
##### fragment可以是个有生命周期的viewgroup
##### fragment的生命周期不是由activity决定的

关于无参构造函数为什么一定要声明我想大部分开发者都知道，
就算你不知道，bugly也会让你知道。
我们都知道，fragment是通过newInstance创建的