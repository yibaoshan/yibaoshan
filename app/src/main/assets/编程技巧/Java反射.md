


- 使用 getDeclaredXXX() 获取任意访问修饰符的 Constructor、Method、Field
- 使用 setAccessible(true) 允许访问权限
- 使用 XXX.getGenericType() 获取泛型 Type（声明的类型）

## 反射可以获取泛型类型吗？

反射可以获取声明时的泛型类型，这时候的信息保存在 class 文件中，而运行时的泛型类型无法被获取
