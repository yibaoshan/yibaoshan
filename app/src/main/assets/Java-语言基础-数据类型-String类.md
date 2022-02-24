### Overview
1. String类介绍
2. String类高频面试题
3. StringBuffer&StringBuilder

### 一、String介绍
#### 1、创建字符串对象的3种方式：
1. String str="hello";//字符串hello在常量池
2. String str=new String("hello");//字符串hello在堆内存(此方法编译器会提示建议使用方式1创建)
3. intern方法，会从字符串常量池中查询当前字符串是否存在，若不存在就会将当前字符串放入常量池中

#### 2、常用方法
1. int compareTo(String anotherString)和compareToIgnoreCase(String str)：
按字母顺序比较两个字符串，返回首个不相同的Unicode差值
2. concat(String str)：
将指定的字符串连接到此字符串的末尾
3. String intern()：
返回一个字符串，优先从字符串常量池中查询，若不存在就会将当前字符串放入常量池中

### 二、String高频面试题
#### 1、String能有多长？
编译期的限制：字符串的UTF8编码值的字节数不能超过65535，字符串的长度不能超过65534；
运行时限制：字符串的长度不能超过2^31-1，占用的内存数不能超过虚拟机能够提供的最大值。
在常量池中字符串是CONSTANT_Utf8_info类型，最大长度是u2 length，u2是无符号的16位整数，因此理论上允许的的最大长度是2^16-1=65535

#### 2、String真的不可变吗？
反射可更改值

#### 3、Java char是两个字节，如何存储UTF-8字符？
char里面存的是utf-8的编码值

### 三、StringBuffer&StringBuilder
加不加锁的区别
toString()会new String()创建对象
append/insert等方法操作的是数组

### 补充

String s1 = "hello";
String s2 = s1.intern();
String s3 = new String("hello");
String s4 = new String("hello");
String s5 = s4.intern();

System.out.println(s1 == s2);//true
System.out.println(s1 == s3);//false
System.out.println(s3 == s4);//false
System.out.println(s5 == s1);//true (此处true是因为常量池中已经有'hello'，所以s5=s1)