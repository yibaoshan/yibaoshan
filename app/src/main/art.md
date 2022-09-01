

### 内存

Android的Dalvik虚拟机，和Java虚拟机有很多类似的特点，比如它们的对象都是创建在堆上的

JVM有堆大小限制，Android同样也有，如果超过这个大小程序便会抛出"Out of Memory"异常

#### 查看APP内存限制

在代码中，我们可以使用activityManager.getMemoryClass()来获取默认情况的堆内存

如果你的APP想要使用更大的内存，也可以在xml中增加android:largeHeap="true"的标签

随后，调用activityManager.getLargeMemoryClass()来查看最大堆内存限制
