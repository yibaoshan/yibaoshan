## Overview
### 1、强软弱虚
### 2、数组

### 一、基础数据类型
- 整形
  1. byte：占1个字节，默认值为0，封装类Byte
  2. short：占2个字节，默认值0，封装类Short
  3. int：占4个字节，默认值0，封装类Integer
  4. long：占8个字节，默认值0，封装类Long
- 浮点型
  1. float，占4个字节，默认值0.0f，小数表示位23位，最大十进制的7位，严谨点6位有效精度
  2. double，占8个字节，默认值0.0d，小数位52位，换算十进制最多16位，严谨点15位有效精度
- 字符型
  1. char，占2个字节，默认值'\u0000'对应数字0，输出为空，包装类Character
- 布尔型
  1. boolean，字节大小由jvm指定，网上结论：使用int表示是4个，boolean数组为1个

tips：

1. 涉及到金额计算时，要么使用BigDecimal，要么使用最小单位，比如多少分钱
2. 除了float和double，Character常量池为0-128；其他四种整形常量池均为-128-127
3. 整数类型最大值都是2的字节数*8位-1次幂再-1

####Java基础运算符

快速将10进制转2进制方法

一般算比较小的数字的话，就先找小于并且临这个数最近的2的N次方。然后依次。
比如 37 = 32+4+1，对应在2进制就是：

【32】【16】【8】【4】【02】【01】

【1】】【0】【0】【1】【0】【1】

在计算机中如果二进制的最高位是1，那么该数据肯定是负数，负数在计算机中存储的是补码

Java负数的存储过程：-5 = 11111011

1. 先取负数的绝对值：5-》00000101
2. 原码取反得到反码：00000101-》11111010
3. 反码+1得到补码：11111010-》11111011

- 位移操作符：

  1. 右移[>>]，如何是整数高位用0补位，负数高位补1
  2. 无符号右移[>>>]，高位永远补0
  3. 左移[<<]，低位永远补0
  4. Java没有无符号左移，因为左移操作和无符号左移操作做的是相同的事情，最右边位都是补0操作

- 位与(&)运算符

  规则：都为1等于1，否则为0

  举例：5&3-》0101&0011=0001

- 位或(|)运算符

  规则：只要有一个是1就为1，否则为0

  举例：5|3-》0101|0011=0111

- 位异或(^)运算符

  规则：相同为0，不同为1

  举例：5^3-》0101^0011=0110

- 位非(~)运算符

  规则：一元操作符

  举例：~5-》0100=1011

####Java五大引用类型

- 强引用 strongReference new关键字
- 软引用 softReference
- 弱引用 weakReference
- 虚引用 phantomReference
- finalizerReference 重写finalize且方法不为空自动创建

tips：

1. 软引用和弱引用同样可以接受referenceQueue对象，调用对象finalize方法前，会加入该队列

2. FinalizerReference执行add和remove时会加锁，Android4.2前加锁对象是class本身，粒度更大

3.  FinalizerReference线程优先级的问题：由于UI和Render线程的nice值都是负数，而FR所在线程一般为0，导致finalize回收不会很快，进一步影响性能

####Java Object

常用方法：

1. 线程相关：wait()，notify()：同步代码块中使用，wait释放锁进入等待队列，notify唤醒其他线程
2. hashCode，equals
3. clone：浅克隆实现，复制值类型而不复制引用类型。想实现深克隆可以序列化/反序列化或者每个对象都重写clone方法
4. finalize：慎用

####Java String

字符串常量池：

jdk1.6：方法区

jdk1.7：堆内存

jdk1.8：元空间

####Java异常捕获

介绍：Throwable是Error和Exception的父类

tips：

1. 在finally中使用system.exit或者死循环，将无法执行finally代码
2. 在finally中使用return语句会覆盖try/catch中的return
3. 使用finally后可以省略catch部分

常见runtimeException：

1. ClassCastException-类转换异常
2. IndexOutOfBoundsException-数组越界
3. NullPointerException-空指针

面试题：

- 下面这段代码返回值是什么？

  ```
  String str = "";
  try {
      str.charAt(1);
  }catch (Exception e){
      str = "catch";
      return str;
  }finally {
      str = "finally";
  }
  return str;
  ```

  答：catch，因为catch代码块已经压栈，finally执行完成后会跳转到已经压栈的return语句处执行

- 类 ExampleA 继承 Exception，类 ExampleB 继承ExampleA，下面代码块执行结果？

- ```
  try {
      throw new ExampleB("b")
  } catch（ExampleA e）{
      System.out.println("ExampleA");
  } catch（Exception e）{
      System.out.println("Exception");
  }
  ```

  答：ExampleA，里氏替换原则，能用父类型一定可以使用子类型

####Java关键字

- final

  简介：final用来修饰类、方法和变量

  修饰类和方法时防止被继承/复写

  修饰变量时，变量引用不可更改，当然，final还有禁止重排序的功能

- transient

  简介：修饰变量，和Serializable一起用时不可被序列化。和Externalizable一起用时无效

####Java元编程

- 编译阶段

  - APT注解处理器Annotation Progress Tool
  - ByteCode

- 运行阶段

  - 泛型机制generic

    简介：泛型的使用范围：泛型类、泛型接口、泛型方法

    优缺点：

  - 反射reflect

  - 动态代理

####补充

##### Java泛型

```
上界通配符：class Test<? extends Object>
下界通配符：class Test<? super Integer>
```

泛型的缺点：

1. 泛型无法使用基本数据类型，所以基本数据类型有装箱和拆箱的开销
2. 泛型无法用作方法重载
3. 泛型类型无法当做真实类型使用，必须强转
4. 静态方法无法引用类泛型参数
5. 泛型类型签名信息特定场景下反射可以获取

泛型的优点：

1. 类型安全，在编码过程中编译器会检查类型
2. 编译期有效

为什么Google的gson需要传入class文件

答：因为返回值是泛型，编译后会被擦除为object

#### Java jdk指令

javac->将.java文件编译为.class文件

javap -c->查看.class字节码

####问题分析

- Java I/O

  - 既然有了字节流，为什么还要有字符流？

    解析：这个问题本质想问：不管是文件读写还是网络发送接收，信息的最小存储单元都是字节，为什么I/O流操作要分为字节流和字符流操作呢？

    答：字符流是由 Java 虚拟机将字节转换得到的，问题就出在这个过程还算是非常耗时，并且，如果我们不知道编码类型就很容易出现乱码问题。所以， I/O 流就干脆提供了一个直接操作字符的接口，方便我们平时对字符进行流操作。如果音频文件、图片等媒体文件用字节流比较好，如果涉及到字符的话使用字符流比较好。

- Java的char是两个字节，如何存储UTF-8字符？

  答：UTF-8是编码格式，char存的是Unicode字符集。而且存入的是utf-16

- Java的String可以有多长？

  答：在常量池取决于u2 length描述符的长度65535，在堆中取决于heap大小，最大Integer.MAX_VALUE(约4G)

- 匿名内部类有哪些限制？  

  答：匿名内部类会持有外部类的引用

- 匿名内部类有构造方法吗？

  答：有，编译器生成，参数列表包括非静态外部对象，非静态父类外部对象，父类的构造方法参数，引用外部的final变量

- 在方法内部可以创建类吗？

  答：可以

- 怎样理解Java的方法分派？

  - 静态分派：调用时取决于编译时期声明的类型
  - 动态分派：运行时取决于实际类型

- i++和++i的区别？

  答：循环遍历没区别，赋值操作的话i++直接返回i的当前值再进行运算

- TODO Java的方法签名是什么？

- 下面代码为什么这么执行？

- ```
  public static class Singleton {
  
      private static Singleton singleton = new Singleton();
      public static int counter1;
      public static int counter2 = 0;
  
      private Singleton() {
          counter1++;
          counter2++;
      }
  
      public static Singleton getSingleton() {
          return singleton;
      }
  
  }
  
  public static void main(String[] args) {
      Singleton singleton = Singleton.getSingleton();
      System.out.println("counter1="+singleton.counter1);
      System.out.println("counter2="+singleton.counter2);
      //counter1=1
      //counter2=0
  }
  ```