### Overview
1. 什么是双亲委托？如何破坏？为什么要破坏？
2. 类加载流程
3. 类加载器，如何保证多线程安全以及防止系统类被替换
4. 常见问题

### 一、双亲委托机制

#### 1、什么是双亲委托机制？以Java举例：
1. 当 AppClassLoader 加载一个 class 时，它首先不会自己去尝试加载这个类，而是把类加载请求委派给父类加载器 ExtClassLoader 去完成。
2. 当 ExtClassLoader 加载一个 class 时，它首先也不会自己去尝试加载这个类，而是把类加载请求委派给 BootStrapClassLoader 去完成。
3. 如果 BootStrapClassLoader 加载失败 (例如在 $JAVA_HOME/jre/lib 里未查找到该 class)，会使用 ExtClassLoader 来尝试加载；
4. 若 ExtClassLoader 也加载失败，则会使用 AppClassLoader 来加载，如果 AppClassLoader 也加载失败，则会报出异常 ClassNotFoundException。

#### 2、如何自定义类加载器，以Java举例：

```
public class MyClassLoader extends ClassLoader {

    private String root;

    // 重写findClass来查找类
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] classData = loadClassData(name);
        if (classData == null) {
            throw new ClassNotFoundException();
        } else {
            //拿到二进制数据后丢给ClassLoader的defineClass方法解析
            return defineClass(name, classData, 0, classData.length);
        }
    }

    //读到class文件的二进制数据byte[]
    private byte[] loadClassData(String className) {
        String fileName = root + File.separatorChar
                + className.replace('.', File.separatorChar) + ".class";
        try {
            InputStream ins = new FileInputStream(fileName);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int length = 0;
            while ((length = ins.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
```

#### 3、如何破坏双亲委托

1. Java：重写loadClass方法就行了，可以把Java开头的交给父加载器，其他的自己来
2. Android：

### 二、类加载流程

#### 1、Java的类加载流程：
1. 走I/O找到class文件，对应流程：加载
2. 解析class文件，校验是否合法等，对应流程：连接(验证、准备、解析)
3. 初始化构造函数、成员变量等，对应流程：初始化
4. 活着等着被使用，对应流程：使用
5. 死亡，没有地方使用且classloader也被回收，那么这个类信息就可以卸载了，比如进程结束，对应流程：卸载

1~3步应该都是线程安全的，也就是说直到类初始化完成才会释放锁

#### 2、Android的类加载流程：

```
PathClassLoader
    ->BaseDexClassLoader
        ->DexPathList
            ->DexFile
                ->findClass()->loadClassBinaryName()->defineClass()
```

```
public Class<?> findClass(String name, List<Throwable> suppressed) {
    for (Element element : dexElements) {
        Class<?> clazz = element.findClass(name, definingContext, suppressed);
        if (clazz != null) {
            return clazz;
        }
    }
    if (dexElementsSuppressedExceptions != null) {
        suppressed.addAll(Arrays.asList(dexElementsSuppressedExceptions));
    }
    return null;
}
```

#### 3、类加载触发时机

常见的有5种方式：
1. 使用new关键字创建对象时
2. 访问某个类的静态成员或静态方法
3. 反射，Class.forName/newInstance啥的
4. 访问子类时，它的爸爸会被初始化
5. Java虚拟机启动时被标明为启动类的类，不重要

#### 3、如何保证类加载时是安全的？

### 三、类加载器

#### 1、Java中的类加载器

1. 启动类加载器: Bootstrap ClassLoader
负责加载JDK所有类库，Java程序无法调用该加载器

2. 扩展类加载器: Extension ClassLoader
负责加载JDK\jre\lib\ext中类库，普通Java程序可以调用该加载器

3. 应用程序类加载器: Application ClassLoader
负责加载用户类路径 (ClassPath) 所指定的类，开发者可以直接使用该类加载器，未自定义的情况下该类为默认加载器

#### 2、Android中的类加载器

1. BootClassLoader
负责Framework系统类的加载，Context啊等等

2. PathClassLoader
负责App的类加载

3. DexClassLoader
负责加载自定义路径dex文件

### 四、常见问题

1. Class.forName() 和 ClassLoader.loadClass() 区别?
答：前者会初始化类并且执行static静态方法块，后者不会

