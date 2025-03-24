
`MIT6.S081` 是麻省理工的操作系统公开课，前身是 `MIT6.82`8，它包含操作系统的知识和前沿 research 内容，2020 年以后，`6.828` 被拆分成两个课程，`6.828` 和 `6.S081`。

新的 `6.S081` 课程去除了原课程中的 research 部分，定位是 **更适合入门的本科课程**，二者的区别之一：

- `6.828` 基于英特尔 **`IA-32`** 开发名为 `JOS` 的操作系统
- `6.S081` 课程则基于 **`RISC-V`** 开发名为 `xv6` 的操作系统

对操作系统感兴趣的同学可以上手 `6.S081`，这样可以避开 `Intel x86` 在发展过程中，为了向下兼容引发的一些历史遗留问题，这部分内容理解起来还是有点头疼的。

`MIT6.S081` 课程目前有三个版本，`2020`、`2021`、`2022`，课程之间区别不大，建议看 `2020` 版，网上资料比较多。

- 课程地址：https://pdos.csail.mit.edu/6.828/2020/schedule.html
- 课程录播：https://www.youtube.com/watch?v=L6YqHxYHa7A
- 《xv6 os book》：https://pdos.csail.mit.edu/6.S081/2020/xv6/book-riscv-rev1.pdf
- 课后 lab：https://pdos.csail.mit.edu/6.S081/2020/labs
- 源码仓库：git://g.csail.mit.edu/xv6-labs-2020 
- 中文翻译版
  - 视频课程：https://www.bilibili.com/video/BV19k4y1C7kA/
  - 视频课程 - 文字版：https://mit-public-courses-cn-translatio.gitbook.io/mit6-s081
  - 《xv6 os book》：https://xv6.dgs.zone/
- 其他博客：
  - CS自学指南：https://csdiy.wiki/%E6%93%8D%E4%BD%9C%E7%B3%BB%E7%BB%9F/MIT6.S081/
  - Miigon：https://blog.miigon.net/categories/mit6-s081/
  - Wings：https://blog.wingszeng.top/series/learning-mit-6-s081/
  - 知乎专栏《28天速通MIT 6.S081操作系统》：https://zhuanlan.zhihu.com/p/632281381

优点：

- xv6 完成度非常高,包括进程调度、内存管理、内核态切换、锁等等，几乎可以满足你对 OS 所有的未知探索欲，
- 课程安排合理，难度循序渐进，课后 lab 的质量也非常高

缺点：

- 需要一定的自学能力，并不是和文章一样事无巨细，一步步喂到嘴里。
- 我的 c 语言基础从学完以后几乎就没用到过，这也算是我的第一次 c 语言实战项目，加上前期对工程不是很熟悉、OS 新的概念理解的不是很到位、要做的事情也不，导致我做题速度很慢，有点像刚刷 leetcode 的状态，一道题可以做大半天，有的题我甚至做了快两天，一直在 debug。