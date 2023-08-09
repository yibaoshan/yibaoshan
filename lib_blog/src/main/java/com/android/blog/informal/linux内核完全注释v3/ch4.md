
第四章：80X86 保护模式及其编程

## 4.1 80X86 系统寄存器和系统指令

80X86 提供了 EFLAGS 标志寄存器和几个系统寄存器，用于控制处理器的运行模式、权限等等，这些都是提供给操作系统的。

操作系统本质也是个大一点的应用程序，所以，也可以说是提供给使用者的，本书里面指的就是 Linux 了。

EFLAGS 包括了几个系统标识，一共有 32 位 bit，看图，黄色是系统标识位，黄色的不知道，灰色的是保留位，看起来好像没用到。

flag 的 1、3、5、12、13、14、15 位在 80X86 CPU 中没有使用，不具有任何含义. 而 0、2、4、6、7、8、9、10、11 位都具有特殊的含义.

这些比特位非常非常非常非常重要，理解它，就能理解当前处理器的运行状态，模式，对后续的 io ，特权级帮助非常大。

- EFLAGS （标志寄存器）

- Memory-Management Registers （内存管理寄存器）

- Control Registers （控制寄存器）

- Debug Registers （调试寄存器）

- Test Registers （测试寄存器）

系统标志寄存器：

- 0，CF，进位标识，Carry Flag。用来反应运算是否产生了进位或者借位，如果运算的最高位产生了进位或借位，CF = 1，否则为 0 
- 1，无名。值为1，干嘛的书里面没说
- 2，PF，奇偶标志，Parity Flag，运算结果中，1 的个数为偶数，PF = 1，否则为 0 。ps，这里图片标错了，标成了恢复标志（RF）
- 3，无名。值为0 ，作用未知
- 4，AF，辅助进位，Assistent Carry Flag。用来反应运算过程中有没有发生进位/借位。CF 是表示结果的最高位，注意区分两者。
- 5，无名。D5 值为 0 ，作用未知。
- 6，ZF，零标志位，Zero Flag，表示计算结果是不是 0 。
- 7，SF，负号标志。表示计算结果是不是负数
- 8，TF，跟踪标志位。置为则进入单步调试模式
- 9，IF，中断标志位。Interrupt Flag，置为 1 处理器才会接收外部中断请求。
- 10，DF，方向标志位，Direct Flag，看不懂。
- 11，OF，溢出标志，Over flow Flag。表示计算结果是否超出当前运算位数的表示范围。
- 12，I/OPL，I/O 特权级标志，I/O Privilege Level
- 13，I/OPL
- 14，NT，嵌套任务标志位，Nested Task，Call、中断、异常时处理器会设置该标志位。？？？啥玩意，看不懂
- 15，空
- 16，RF，恢复标志，Rest Flag，不管
- 17，VM，虚拟 8086 模式标志，Virtual-8086 Mode。置为 1 开启虚拟 8086 模式，否则保护模式。这又是啥，实模式吗？
- 18，以后都是空的

内存管理寄存器：

80X86 提供了 4 个内存管理寄存器：

- GDTR，Global Descriptor Table Register ，全局描述表寄存器，16 位的表长度 + 32 / 64 线性基址
  - 保存 Global Descriptor Table 的基址，32 / 64 bit 位
  - 保存 Table Limit 表限制，16 bit 位
  - 在后面出现的系统指令中，LGDT —— 加载全局描述符表寄存器 (Load GDT Register)
  - SGDT —— 存储全局描述符表寄存器 (Store GDT Register)
- IDTR，Interrupt Descriptor Table Register，中断描述表寄存器
- LDTR，Local Descriptor Table Register，
- TR，Task Register

这几个寄存器用来保护，所以又称保护寄存器

page 83

## 参考资料

- Linux 内存地址映射，https://lrita.github.io/images/posts/memory/Linux_Memory_Address_Mapping.pdf
- Intel 80386 程序员参考手册，https://wizardforcel.gitbooks.io/intel-80386-ref-manual/content/index.html