
Java5开始加入的泛型是为了规范形参

泛型的使用范围：

- 泛型类
- 泛型接口
- 泛型方法
- 泛型数组

## 泛型擦除

若泛型没有指定类型，则擦除为Object

泛型擦除会导致多态冲突，两个方法不同的入参，编译不通过

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

解决方案是，虚拟机编译器生成 class 时会在子类生 成Object 的 eat(Object t) 方法

在此方法中调用重载的 eat(Rice t) 方法

## 限定符

- 无限制通配符 ?
- 上界通配符（? extends），只能是 ? 或者其子类
- 下界通配符(? super)，只能是 ? 或者其父类
- 多个限制使用&号连接

### ? 和 T 的区别？

T 通常表示指定元素只能是 T 类型

而 ? 则表示元素可以是任意类型

？和 T 都表示不确定的类型，区别在于我们可以对 T 进行操作，但是对 ？ 不行，比如如下这种 ：

```
// 可以
T t = operate();

// 不可以
？ car = operate();
```

## 常见问题

- 如何理解基本类型不能作为泛型类型？
  - 答：Object 类型不能存储 int 值，只能引用 Integer 的值，list.add(1)能够运行是因为自动拆装箱

## 参考资料

- [Java 不能实现真正泛型的原因是什么？](https://www.zhihu.com/question/28665443)
- [Java中的反射真的可以获取泛型属性吗](https://jasonkayzk.github.io/2020/03/25/Java%E4%B8%AD%E7%9A%84%E5%8F%8D%E5%B0%84%E7%9C%9F%E7%9A%84%E5%8F%AF%E4%BB%A5%E8%8E%B7%E5%8F%96%E6%B3%9B%E5%9E%8B%E5%B1%9E%E6%80%A7%E5%90%97/)