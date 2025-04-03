# MIT6.S081 - Lab7 Multithreading（多线程）

本篇是 MIT6.S081 操作系统课程 Lab7 的实验笔记，任务是完成多线程并发功能。

> - Lab7 地址：https://pdos.csail.mit.edu/6.828/2020/labs/thread.html
> - 我的实验记录：https://github.com/yibaoshan/xv6-labs-2020/tree/thread

在开始实验之前，你需要：

1. 观看 Lecture 10 课程录播视频：**Multiprocessors and Locks（多处理器和锁）**
    - YouTube 原版：https://www.youtube.com/watch?v=NGXu3vN7yAk
    - 哔哩哔哩中译版：https://www.bilibili.com/video/BV19k4y1C7kA?vd_source=6bce9c6d7d453b39efb8a96f5c8ebb7f&p=9
    - 中译文字版：https://mit-public-courses-cn-translatio.gitbook.io/mit6-s081/lec10-multiprocessors-and-locking
2. 阅读 [《xv6 book》](https://xv6.dgs.zone/tranlate_books/book-riscv-rev1/c3/s0.html) 第六章： **Lock**
    - 英文原版：https://pdos.csail.mit.edu/6.828/2020/xv6/book-riscv-rev1.pdf
    - 中译版：https://xv6.dgs.zone/tranlate_books/book-riscv-rev1/c6/s0.html
3. 观看 Lecture 11 课程录播视频：**Thread Switching（线程切换）**
    - YouTube 原版：https://www.youtube.com/watch?v=vsgrTHY5tkg
    - 哔哩哔哩中译版：https://www.bilibili.com/video/BV19k4y1C7kA?vd_source=6bce9c6d7d453b39efb8a96f5c8ebb7f&p=10
    - 中译文字版：https://mit-public-courses-cn-translatio.gitbook.io/mit6-s081/lec11-thread-switching-robert
4. 阅读 [《xv6 book》](https://xv6.dgs.zone/tranlate_books/book-riscv-rev1/c3/s0.html) 第七章： **Scheduling（调度）**
    - 英文原版：https://pdos.csail.mit.edu/6.828/2020/xv6/book-riscv-rev1.pdf
    - 中译版：https://xv6.dgs.zone/tranlate_books/book-riscv-rev1/c7/s0.html
