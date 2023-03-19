

中介者模式（Mediator Pattern）是用来降低多个对象和类之间的通信复杂性。这种模式提供了一个中介类，该类通常处理不同类之间的通信，并支持松耦合，使代码易于维护。中介者模式属于行为型模式。

中介者模式是一种行为设计模式， 能让你减少对象之间混乱无序的依赖关系。 该模式会限制对象之间的直接交互， 迫使它们通过一个中介者对象进行合作。

源码锚点：
在《Android源码设计模式解析与实战》一书中提到在Android系统源码中，有个Keyguard锁屏类使用到中介者模式，感兴趣的朋友可以自行查看

中介者模式有点像贪心算法，对于客户端来说，当一个页面的控件改变需要同步给其他若干个控件时，单单维护控件通信一项就已经很麻烦了；
这时候会自然而然的选择最优解，即每个控件状态改变都交给同一方法处理
正因为如此，在客户端开发中，中介者模式常常与观察者模式同时使用

中介者模式，AMS/PMS/WMS