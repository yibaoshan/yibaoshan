### Overview
1. 什么是泛型？为什么要有泛型
2. 泛型的基本使用
3. 泛型擦除
4. 常见问题

### 一、泛型介绍
Java5开始加入的泛型是为了规范形参

### 二、泛型的基本使用
1. 泛型的使用范围：泛型类、泛型接口、泛型方法、泛型数组
2. 泛型的限定符
    1. <?> 无限制通配符
    2. <? extends E> extends 上界，指定类型或其子类
    3. <? super E> super 下界，指定类型或其父类
    4. 多个限制使用&号连接

### 三、 泛型擦除
概念：若泛型没有指定类型，则擦除为Object

### 四、常见问题
1. 如何获取泛型擦除前的类型？两种情况：
    1. 子类指定泛型类型，方法签名中有
    2. 通过调用class的genericInfo方法获取类类签名信息

2. 泛型擦除会导致多态冲突，如何解决？
举例：
```
class Human<T> {
    eat(T t);
}
class Man extends Human<Rice>{
    eat(Rice t);
}
class Test{
    Man man = new Man();
    man.eat(new Object());//编译器报错说明是方法重写
    man.eat(new Rice());
}
```
答：虚拟机编译器生成class时会在子类生成Object的eat(Object t)方法，在此方法中调用重载的eat(Rice t)方法

3. 如何理解基本类型不能作为泛型类型？
答：Object 类型不能存储 int 值，只能引用 Integer 的值，list.add(1)能够运行是因为自动拆装箱
