
场景，游戏存档，淘宝快照

在自定义View中，经常使用到Canvas的save和restore方法，想看源码的同学在这
很多文章提到在activity中的onSaveInstanceState和onRestoreInstanceState，笔者认为从场景来看，说是备份机制更合适一些

备忘录模式最重要的是否是深拷贝

frameworks/base/graphics/java/android/graphics/Canvas.java
frameworks/base/libs/hwui/jni/android_graphics_Canvas.cpp
frameworks/base/libs/hwui/jni/SkiaCanvas.cpp //最终实现
