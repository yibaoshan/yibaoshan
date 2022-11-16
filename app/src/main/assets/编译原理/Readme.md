
### 概述

传统的编译器通常分为三个部分，前端(frontEnd)，优化器(Optimizer)和后端(backEnd)。

在编译过程中，前端主要负责词法和语法分析，将源代码转化为抽象语法树；

优化器则是在前端的基础上，对得到的中间代码进行优化，使代码更加高效；

后端则是将已经优化的中间代码转化为针对各自平台的机器代码。

编译原理入门即可，非重点

### 编译器和解释器

首先，要明确一个概念，编译与解释描述的是程序的执行方式，与语言无关

并且，程序的编译与解释并没有太大的区别，判断的依据是，源文件是在什么时候被翻译成目标 CPU（指令集） 的指令

#### 编译

编译，指的是编译器会将源文件预先处理成目标文件，这个目标文件可能是汇编/机器语言，但也可能是其他语言。比如，Java 源文件编译后就是 class 字节码

对 C 语言或者其他编译型语言来说，编译生成了目标文件，而这个目标文件是针对特定的 CPU 体系的

为 ARM 生成的目标文件，就不能被用于 MIPS 的 CPU，如果这个程序需要在另外一种 CPU 上面运行，这段代码就必须重新编译

#### 解释

解释，指的是边处理源文件，边执行

对于各种非编译型语言（例如python/java）来说，同样也可能存在某种编译过程，但他们编译生成的通常是一种『平台无关』的中间代码，比如 class 字节码

这种代码一般不是针对特定的 CPU 平台，他们是在运行过程中，才被翻译成目标 CPU 指令的

因而，在 ARM CPU 上能执行，换到 MIPS 也能执行，换到 X86 也能执行，不需要重新对源代码进行编译。


### 编译过程

#### 指令重排

所谓编译器重排，指的是在生成目标代码的过程中，可能会发生交换前后没有依赖关系的内存访问顺序的行为，比如

``` java
int a = a1;
int b = b1;
```

编译器不保证在最终生成的 class 代码中，对 a 内存的写入在对 b1 内存的读取之前

如果上面两行代码的顺序，是有要求的，比如 b1 可能在其他线程中和 a1 有关联，那么我们就需要用一些手段来保证编译器不会进行错误的优化，方案有三

- 把对应的变量声明为 volatile 的，C++ 标准保证对 volatile 变量间的访问编译器不会进行重排，不过仅仅是 volatile 变量之间， volatile 变量和其他变量间还是有可能会重排的；
- 在需要的地方手动添加合适的 Memory Barrier 指令，Memory Barrier 指令的语义保证了编译器不会进行错误的重排操作；
- 把对应变量声明为 atomic 的， 与 volatile 类似，C++ 标准也保证 atomic 变量间的访问编译器不会进行重排。不过 C++ 中不存在所谓的 “atomic pointer” 这种东西，如果需要对某个确定的地址进行 atomic 操作，需要靠一些技巧性的手段来实现，比如在那个地址上进行 placement new 操作强制生成一个 atomic 等；

### 参考资料

- [C++ 中的 volatile，atomic 及 memory barrier](https://gaomf.cn/2020/09/11/Cpp_Volatile_Atomic_Memory_barrier/)


### GCC

在学习GCC之前，您需要首先了解GNU工程。

理查德·斯托曼（Richard Stallman）于1984年发起了GNU工程，以构建一个类似UNIX的开源软件系统。

随着时间的流逝，GNU操作系统尚未广泛发展。但是，它已经孵化了许多出色且有用的开源软件工具，例如Make，Sed，Emacs，Glibc，GDB和GCC。

这些GNU开源软件和Linux内核共同构成了GNU / Linux系统。

最初，GCC为CNU语言提供了基于C编程语言的稳定可靠的编译器。

它的全名是GNU C编译器。后来，支持了更多的语言（例如Fortran，Obj-C和Ada），并且GCC的全名更改为GNU Compiler Collection。

GCC（GNU Compiler Collection，GNU编译器套装），是一套由 GNU 开发的编程语言编译器。

GCC 原名为 GNU C 语言编译器，因为它原本只能处理 C语言。GCC 快速演进，变得可处理 C++、Fortran、Pascal、Objective-C、Java, 以及 Ada 等他语言

### LLVM

LLVM (Low Level Virtual Machine，底层虚拟机) 提供了与编译器相关的支持，能够进行程序语言的编译期优化、链接优化、在线编译优化、代码生成。简而言之，可以作为多种编译器的后台来使用。

该项目由编译器大神 Chris Lattner 发起，他同时也是 Apple Swift 的创造者

### LLVM2.0 - Clang

Clang 是 LLVM 的前端，可以用来编译 C，C++，ObjectiveC 等语言。Clang 则是以 LLVM 为后端的一款高效易用，并且与IDE结合很好的编译前端。

### 相关链接

- [编译器领域非常火热的项目：LLVM 官网地址](https://llvm.org/)
- [北京航空航天大学：LLVM 课程](https://buaa-se-compiling.github.io/miniSysY-tutorial/pre/llvm_ir_quick_primer.html)
- [知乎：如何学习编译原理？](https://www.zhihu.com/question/21515496)
- [GCC与Clang / LLVM：C / C ++编译器的深度比较 ](https://www.cnblogs.com/findumars/p/14213309.html)
