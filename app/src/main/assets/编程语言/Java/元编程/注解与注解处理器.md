### Overview
1. 什么是注解
2. 使用方法
3. 元注解的分类
4. 注解与反射
5. 常见问题和使用场景

### 一、注解介绍
注解是Java5开始引入的，目的是为了对代码进行解释说明
可以用于描述类、接口、方法、字段、参数

### 二、使用方法
class @interface MyAnnotation

### 三、元注解的分类

在Java5中提供了4中类型的元注解，他们分别是：
1. @Target：被修饰的注解可以用在什么地方
2. @Retention：保留的范围，运行时(RUNTIME)、编译时(CLASS)、写代码时(SOURCE)
3. @Documented：javadoc自动生成文档用
4. @Inherited：如果父类使用了被Inherited修饰的注解，那么继承的子类也具有这个注解，不用显式的再声明注解

在Java8后又增加了两种：
1. @Repeatable：提高代码可读性
2. @Native：表示可被本地代码引用

### 四、注解与反射
反射包 java.lang.reflect 下的 AnnotatedElement 接口提供这些方法
这里注意：只有注解被定义为 RUNTIME 后，该注解才能是运行时可见，当 class 文件被装载时被保存在 class 文件中的 Annotation 才会被虚拟机读取。

1. boolean isAnnotationPresent(Class<?extends Annotation> annotationClass)
判断该程序元素上是否包含指定类型的注解，存在则返回 true，否则返回 false。注意：此方法会忽略注解对应的注解容器。

2. <T extends Annotation> T getAnnotation(Class<T> annotationClass)
返回该程序元素上存在的、指定类型的注解，如果该类型注解不存在，则返回 null。

3. Annotation[] getAnnotations()
返回该程序元素上存在的所有注解，若没有注解，返回长度为 0 的数组。

4. <T extends Annotation> T[] getAnnotationsByType(Class<T> annotationClass)
返回该程序元素上存在的、指定类型的注解数组。没有注解对应类型的注解时，返回长度为 0 的数组。该方法的调用者可以随意修改返回的数组，而不会对其他调用者返回的数组产生任何影响。getAnnotationsByType方法与 getAnnotation的区别在于，getAnnotationsByType会检测注解对应的重复注解容器。若程序元素为类，当前类上找不到注解，且该注解为可继承的，则会去父类上检测对应的注解。

5. <T extends Annotation> T getDeclaredAnnotation(Class<T> annotationClass)
返回直接存在于此元素上的所有注解。与此接口中的其他方法不同，该方法将忽略继承的注释。如果没有注释直接存在于此元素上，则返回 null

6. <T extends Annotation> T[] getDeclaredAnnotationsByType(Class<T> annotationClass)
返回直接存在于此元素上的所有注解。与此接口中的其他方法不同，该方法将忽略继承的注释

7. Annotation[] getDeclaredAnnotations()
返回直接存在于此元素上的所有注解及注解对应的重复注解容器。与此接口中的其他方法不同，该方法将忽略继承的注解。如果没有注释直接存在于此元素上，则返回长度为零的一个数组。该方法的调用者可以随意修改返回的数组，而不会对其他调用者返回的数组产生任何影响。

### 五、常见问题

1. 注解支持继承吗？
答：不支持
