####Java反射

**获取class对象的方式**

1. 通过字符串：Class.forName("xxx.xxx.xxx")
2. 通过class属性：xxx.class
3. 通过对象的getClass方法：xxx.getClass()

**获取成员变量**

1. 获取所有字段：aClass.getDeclaredFields() [/dɪ'klɛrd/](cmd://Speak/_us_/declared)
2. 获取公开字段：aClass.getFields()

**获取构造方法**

1. 获取所有声明的构造方法：aClass.getDeclaredConstructors()
2. 获取公开的构造方法：aClass.getConstructors() [/kɔn'strʌktə/](cmd://Speak/_us_/constructor)

tips：

1. 通过setAccessible方法关闭安全检查

####注解处理器Annotation Processor Tool

介绍：jdk1.5之后引入

**元注解类型** java.lang.annotaion

1. @Target：描述构造器、类、字段、方法、包、参数等
2. @Retention：定义annotation被保留的时间长短
   1. SOURCE：源码文件保留，一般用于辅助类
   2. CLASS：class保留
   3. RUNTIME：保留到运行期间
3. @Document：文档注释，不知道干啥的
4. @Inhrited：子类可继承

**Processor**

1. **init(ProcessingEnvironment processingEnv)**方法
   该方法用来初始化处理器，同时该方法传入一个**ProcessingEnvironment**对象，我们可以从该对象获取到一些工具类的实例，我们这里获取到了messager，elementUtils和filer。
   messager对象可以用来在注解处理过程中报错，提出警告。
   elementUtils对象可以用来操作当前处理的元素。
   filer对象用来写`.java`文件.
2. **getSupportedSourceVersion()**方法
   返回当前注解处理器支持的Java源码版本，这有点类似Android中的`targetSdkVersion`，所以一般返回最新的版本就可以，这里返回了**SourceVersion**.*latestSupported()*。
3. **getSupportedAnnotationTypes()**方法
   返回当前注解处理器支持处理的全部注解。
   该方法的返回类型是一个**String**类型的**Set**集合，**Set**集合中每个元素应该是一个注解的完整全名(包名跟类名)。
   由于我们这个处理器只处理**@Builder**注解，因此返回了**Collections**.*singleton*(**Builder**.class.*getCanonicalName()*)。
   *singleton*是**Collections**类中的一个静态方法，会返回一个**SingletonSet**对象。
   **Builder**.class.*getCanonicalName()*是获取**@Builder**注解带包名的完整全名。
4. **process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)**方法
   最重要的就是*process*方法了，因为这个方法实现了真正的注解处理生成代码的逻辑。
   该方法在处理的过程中可能会被调用好几次。
   该方法包含两参数，annotations和roundEnv，annotations是需要被处理的注解集合，roundEnv是Java提供的一个实现了**RoundEnvironment**接口的类的对象，该对象最常用的方法就是**getElementsAnnotatedWith(Class<? extends Annotation> a)**。

#### 字节码插桩/增强

简介：

**ASM API**

- 核心 API

  ASM Core API可以类比解析XML文件中的SAX方式，不需要把这个类的整个结构读取进来，就可以用流式的方法来处理字节码文件。好处是非常节约内存，但是编程难度较大。然而出于性能考虑，一般情况下编程都使用Core API。在Core API中有以下几个关键类：

  - ClassReader：用于读取已经编译好的.class文件。
  - ClassWriter：用于重新构建编译后的类，如修改类名、属性以及方法，也可以生成新的类的字节码文件。
  - 各种Visitor类：如上所述，CoreAPI根据字节码从上到下依次处理，对于字节码文件中不同的区域有不同的Visitor，比如用于访问方法的MethodVisitor、用于访问类变量的FieldVisitor、用于访问注解的AnnotationVisitor等。为了实现AOP，重点要使用的是MethodVisitor。

- 树形 API

  ASM Tree API可以类比解析XML文件中的DOM方式，把整个类的结构读取到内存中，缺点是消耗内存多，但是编程比较简单。TreeApi不同于CoreAPI，TreeAPI通过各种Node类来映射字节码的各个区域，类比DOM节点，就可以很好地理解这种编程方式。

**Android-transform插桩流程**

1. 根目录新建buildSrc/src/main/java/package，并新建类实现Plugin<Project>接口，复写apply方法
2. 继承Transform，复写getInputTypes确定处理哪种类型，复写getScopes()方法确定接受范围，复写transform()方法获取jar包，找到需要处理的class后复制到其他目录
3. 使用ClassReader读取class文件，调用accept方法传入ClassVistor处理，处理完成用ClassWriter写回去

**asm-插桩-方法耗时**

1. 写好需要统一插入的Java代码，查看字节码
2. 在MethodVistor的onMethodEnter()和onMethodExit()中分别加入开始计时和结束并打印计时代码
3. 对照第一步的字节码使用newLocal、storeLocal、newInstance等指令来创建新代码

tips：

1. 可以在工程目录下新建buildSrc文件夹，在里面的类所有模块build文件都可以直接引用
2. ClassVistor和MethodVistor

transform api

####动态代理

