# MIT6.S081 - Lab9 File Systems（文件系统）

本篇是 MIT6.S081 2020 操作系统课程 Lab9 的实验笔记，介绍的是 xv6 的文件系统设计

> - Lab9 地址：https://pdos.csail.mit.edu/6.828/2020/labs/fs.html
> - 我的实验记录：https://github.com/yibaoshan/xv6-labs-2020/tree/fs

在开始实验之前，你需要：

1. 观看 Lecture 14 课程录播视频：**File Systems（文件系统）**
    - YouTube 原版：https://www.youtube.com/watch?v=ADzLv1nRtR8
    - 哔哩哔哩中译版：https://www.bilibili.com/video/BV19k4y1C7kA?vd_source=6bce9c6d7d453b39efb8a96f5c8ebb7f&p=13
    - 中译文字版：https://mit-public-courses-cn-translatio.gitbook.io/mit6-s081/lec14-file-systems-frans
2. 观看 Lecture 15 课程录播视频：**Crash recovery（崩溃恢复）**
   - YouTube 原版：https://www.youtube.com/watch?v=7Hk2dIorDkk
   - 哔哩哔哩中译版：https://www.bilibili.com/video/BV19k4y1C7kA?vd_source=6bce9c6d7d453b39efb8a96f5c8ebb7f&p=14
   - 中译文字版：https://mit-public-courses-cn-translatio.gitbook.io/mit6-s081/lec15-crash-recovery-frans
3. 阅读 [《xv6 book》](https://xv6.dgs.zone/tranlate_books/book-riscv-rev1/c3/s0.html) 第八章： **File Systems（文件系统）**
   - 英文原版：https://pdos.csail.mit.edu/6.828/2020/xv6/book-riscv-rev1.pdf
   - 中译版：https://xv6.dgs.zone/tranlate_books/book-riscv-rev1/c8/s0.html
4. 阅读 xv6 源码：
   - https://github.com/mit-pdos/xv6-riscv/blob/riscv/kernel/bio.c
   - https://github.com/mit-pdos/xv6-riscv/blob/riscv/kernel/fs.c
   - https://github.com/mit-pdos/xv6-riscv/blob/riscv/kernel/sysfile.c
   - https://github.com/mit-pdos/xv6-riscv/blob/riscv/kernel/file.c
   - https://github.com/mit-pdos/xv6-riscv/blob/riscv/kernel/log.c

# 参考资料

- CS自学指南：https://csdiy.wiki/%E6%93%8D%E4%BD%9C%E7%B3%BB%E7%BB%9F/MIT6.S081/
- Wings：https://blog.wingszeng.top/series/learning-mit-6-s081/
- Miigon：https://blog.miigon.net/categories/mit6-s081/
- 知乎专栏《28天速通MIT 6.S081操作系统》：https://zhuanlan.zhihu.com/p/632281381
