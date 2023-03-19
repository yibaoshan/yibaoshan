
代理类在不改版原始类的情况下，通过新增代理类的方式，来给原始类加入新的功能
我们通过一个简单的示例来解释上面的话

对于内部类，我们可以采用同一接口的方式来实现代理
对于外部类的，一般采用继承的方式

上面的代码会有两个问题：
1. 代码入侵，将来换库改动会比较多，代价高
2. 违反单一职责原则，业务类和统计无关

所谓'动态代理'，就是不为每个原始类写一个代理类，而是在运行的时候，动态地创建原始类对应的代理类，然后在系统中用代理类替换掉原始类