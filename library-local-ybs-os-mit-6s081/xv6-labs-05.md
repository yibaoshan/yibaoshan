
本篇是 MIT6.S081 操作系统课程 Lab5 的实验笔记

> - Lab5 地址：https://pdos.csail.mit.edu/6.828/2020/labs/lazy.html
> - 我的实验记录：https://github.com/yibaoshan/xv6-labs-2020/tree/lazy

在开始实验之前，你需要：

1. 观看 Lecture 8 课程录播视频：**Page Faults**
    - YouTube 原版：https://www.youtube.com/watch?v=KSYO-gTZo0A
    - 哔哩哔哩中译版：https://www.bilibili.com/video/BV19k4y1C7kA?vd_source=6bce9c6d7d453b39efb8a96f5c8ebb7f&p=7
2. 阅读 [《xv6 book》](https://xv6.dgs.zone/tranlate_books/book-riscv-rev1/c3/s0.html) 4.6 **页面错误异常**
    - 英文原版：https://pdos.csail.mit.edu/6.828/2020/xv6/book-riscv-rev1.pdf
    - 中译版：https://xv6.dgs.zone/tranlate_books/book-riscv-rev1/c4/s0.html