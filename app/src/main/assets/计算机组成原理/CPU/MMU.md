
### 概述

MMU 是 CPU 片上的硬件电路，它包含两个部件，一个是分段部件，一个是分页部件

功能是负责 CPU 的内存管理，将 CPU 发送的虚拟地址转为物理地址

TLB 是 Translation Lookaside Buffer的简称，可翻译为“地址转换后援缓冲器”，也可简称为“快表”。

简单地说，TLB 就是页表的 Cache，其中存储了当前最可能被访问到的页表项

### 参考资料

- [CPU体系架构-MMU](https://nieyong.github.io/wiki_cpu/CPU%E4%BD%93%E7%B3%BB%E6%9E%B6%E6%9E%84-MMU.html)