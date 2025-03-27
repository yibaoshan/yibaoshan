
本篇是 MIT6.S081 操作系统课程 Lab4 的实验笔记。

> - Lab3 地址：https://pdos.csail.mit.edu/6.828/2020/labs/traps.html
> - 我的实验记录：xxx

在开始实验之前，你需要：

1. 观看 Lecture 5 课程录播视频：**RISC-V Calling Convention and Stack Frames**
    - YouTube 原版：https://www.youtube.com/watch?v=s-Z5t_yTyTM
    - 哔哩哔哩中译版：https://www.bilibili.com/video/BV19k4y1C7kA?vd_source=6bce9c6d7d453b39efb8a96f5c8ebb7f&p=4
2. 阅读 [《xv6 book》](https://xv6.dgs.zone/tranlate_books/book-riscv-rev1/c3/s0.html) 第四章：**陷阱指令和系统调用**
    - 英文原版：https://pdos.csail.mit.edu/6.828/2020/xv6/book-riscv-rev1.pdf
    - 中译版：https://xv6.dgs.zone/tranlate_books/book-riscv-rev1/c4/s0.html
3. 观看 Lecture 6 课程录播视频：**Isolation & System Call Entry/Exit**
    - YouTube 原版：https://www.youtube.com/watch?v=T26UuauaxWA
    - 哔哩哔哩中译版：https://www.bilibili.com/video/BV19k4y1C7kA?vd_source=6bce9c6d7d453b39efb8a96f5c8ebb7f&p=5
