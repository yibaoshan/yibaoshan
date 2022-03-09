### 1、Java反射介绍
### 2、Class类
### 3、常见问题

### 一、Java反射介绍
反射是指在程序运行期间，将一个类拆解成一个个对象，比如构造方法、成员变量、方法、包信息等等

### 二、Class类

每个java类运行时都在 JVM 里表现为一个 class对象，获取class对象的三种方式：
1. 类名.class
2. 对象.getClass()
3. Class.forName("全量类名")

- 常用方法：
    1. getName()/getSimpleName()：获取全量类名和类名
    2. 判断是否是数组、注解、本地类

- Constructor：构造方法
    1. getConstructors()：获取public修饰的方法对象数组
    2. getDeclaredConstructor()：获取所有构造方法数组

- Field：属性变量
    1. getField(String name)：获取public修饰的字段，包含继承的方法
    2. getDeclaredField(String name)：获取所有字段，但不包含继承方法

- Method：方法
    1. getMethod(String name, Class<?>... parameterTypes)：返回public方法
    2. getDeclaredMethod(String name, Class<?>... parameterTypes)：返回方法

### 三、常见问题

1. 反射类及反射方法的获取，都是通过从列表中搜寻查找匹配的方法，所以查找性能会随类的大小方法多少而变化；
2. 每个类都会有一个与之对应的 Class 实例，从而每个类都可以获取 method 反射方法，并作用到其他实例身上；
3. 反射也是考虑了线程安全的，放心使用；
4. 反射使用软引用 relectionData 缓存 class 信息，避免每次重新从 jvm 获取带来的开销；
5. 反射调用多次生成新代理 Accessor, 而通过字节码生存的则考虑了卸载功能，所以会使用独立的类加载器；
6. 当找到需要的方法，都会 copy 一份出来，而不是使用原来的实例，从而保证数据隔离；
7. 调度反射方法，最终是由 jvm 执行 invoke0() 执行；

