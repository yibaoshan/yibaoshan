
在 Web 安全中，Java 服务端通常带有一个称为 RASP (Runtime Application Self-Protection) 的动态防护方案

比如监控某些执行命令的敏感函数调用并进行告警

其实际 hook 点是在 JVM 中，不论是方法直接调用还是反射调用都可以检测到。

因此我们有理由猜测在 Android 中也有类似的调用链路，为了方便观察，这里先看反射调用的场景，一般反射调用的示例如下:

```
public void main() {
    Class c = Class.forName("com.test.test");
    Method m = c.getMethod("foo", null);
    m.invoke();
}
```

因此一个方法的调用会进入到 Method#invoke() 方法

这是一个 native 方法，实际实现在 art/runtime/native/java_lang_reflect_Method.c，大致流程是：

1. 判断方法所属的类是否已经初始化过，如果没有则进行初始化；
2. 将 String.<init> 构造函数调用替换为对应的工厂 StringFactory 方法调用；
3. 如果是虚函数调用，替换为运行时实际的函数；
4. 判断方法是否可以访问，如果不能访问则抛出异常；
5. 调用函数；

最后的调用函数最终进入到 ArtMethod::Invoke 函数，核心代码:

```
void ArtMethod::Invoke( self, args, args_size,  result,  shorty) {
    Runtime* runtime = Runtime::Current();
    if (UNLIKELY(!runtime->IsStarted() ||
               (self->IsForceInterpreter() && !IsNative() && !IsProxyMethod() && IsInvokable()))) {
        art::interpreter::EnterInterpreterFromInvoke(...);
    } else {
        bool have_quick_code = GetEntryPointFromQuickCompiledCode() != nullptr;
        if (LIKELY(have_quick_code)) {
            if (!IsStatic()) {
                (*art_quick_invoke_stub)(this, args, args_size, self, result, shorty);
            } else {
                (*art_quick_invoke_static_stub)(this, args, args_size, self, result, shorty);
            }
        } 
    }
    self->PopManagedStackFragment(fragment);
}
```

ART 对于 Java 方法实现了两种执行模式

一种是像 Dalvik 虚拟机一样解释执行字节码，姑且称为解释模式；

另一种是快速模式，即直接调用通过 OAT 编译后的本地代码。

当 ART 运行时尚未启动或者指定强制使用解释执行时，虚拟机执行函数使用的是解释模式，ART 可以在启动时指定 -Xint 参数强制使用解释执行

但即便指定了使用解释执行模式，还是有一些情况无法使用解释执行，比如:

- 当所执行的方法是 Native 方法时，这时只有二进制代码，不存在字节码，自然无法解释执行；
- 当所执行的方法无法调用，比如 access_flag 判定无法访问或者当前方法是抽象方法时；
- 当所执行的方式是代理方法时，ART 对于代理方法有单独的本地调用方式；

## 解释执行

解释执行的入口是 EnterInterpreterFromInvoke，该函数定义在 art/runtime/interpreter/interpreter.cc，关键代码如下:

```
void EnterInterpreterFromInvoke( self, method, receiver, args, result, stay_in_interpreter) {
    CodeItemDataAccessor accessor(method->DexInstructionData());
    if (accessor.HasCodeItem()) {
        num_regs =  accessor.RegistersSize();
        num_ins = accessor.InsSize();
    }
    // 初始化栈帧 ......
    if (LIKELY(!method->IsNative())) {
        JValue r = Execute(self, accessor, *shadow_frame, JValue(), stay_in_interpreter);
        if (result != nullptr) {
            *result = r;
        }
  }
}
```

## 快速执行

再回到 ArtMethod 真正调用之前

如果不使用解释模式执行，则通过 art_quick_invoke_stub 去调用。

stub 是一小段中间代码，用于跳转到实际的 native 执行，该符号使用汇编实现

在 ARM64 中的定义在 art/runtime/arch/arm64/quick_entrypoints_arm64.S，核心代码如下:

```
.macro INVOKE_STUB_CALL_AND_RETURN
    REFRESH_MARKING_REGISTER
    REFRESH_SUSPEND_CHECK_REGISTER

    // load method-> METHOD_QUICK_CODE_OFFSET
    ldr x9, [x0, #ART_METHOD_QUICK_CODE_OFFSET_64]
    // Branch to method.
    blr x9
.endm

ENTRY art_quick_invoke_stub
    // ...
    INVOKE_STUB_CALL_AND_RETURN
END art_quick_invoke_static_stub
```

中间省略了一些保存上下文以及调用后恢复寄存器的代码，其核心是调用了 ArtMethod 结构体偏移 ART_METHOD_QUICK_CODE_OFFSET_64 处的指针





