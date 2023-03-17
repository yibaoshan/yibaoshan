
在 Kotlin 中有两种类型的构造函数：

- 主构造函数(主构造器)
- 次级构造函数(次级构造器)

> 在Kotlin类中只有一个主构造函数(主构造器)，而辅助构造函数（次级构造器）可以是一个或者多个。

## Kotlin 的主构造函数

标准写法：class 类名 construction(参数1，参数2….){}

其中 **constructor** 是关键字，如果类可见性是 public 可以忽略 **constructor**


### 1、private

```kotlin
 class Person private constructor(name:String,age:Int){
     val name:String
     val age:Int
    init {
        this.name = name
        this.age = age
    }
}
```

### 2、public

```kotlin
 class Person (name:String,age:Int){
     val name:String
     val age:Int
    init {
        this.name = name
        this.age = age
    }
}
```

## Kotlin 的次级构造函数

kotlin在类中可以创建一个或者多个次级构造函数，并且使用 **construction** 关键字创建次级构造函数，举例：

```kotlin
class Person {
    private val name:String
    private val age:Int
    constructor(name:String,age:Int){
        this.name = name
        this.age = age
    }
}
```

### 1、多次次级构造函数

```kotlin
 open class Person {

    constructor(name:String,age:Int){

    }

    constructor(name: String,age: Int,sex:String):this(name,age){

    }

}

class Man: Person{
    constructor(name:String,age: Int):super(name,age){
        
    }
    constructor(name:String,age: Int,sex: String):super(name,age, sex)
    
}
```

## 参考资料

- [一篇文章学会Kotlin中的构造函数](https://juejin.cn/post/6844903872016678919)