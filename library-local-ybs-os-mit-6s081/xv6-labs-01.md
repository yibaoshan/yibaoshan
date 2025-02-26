

> This lab will familiarize you with xv6 and its system calls.
> 本次实验的目的是让你熟悉 xv6 操作系统和它的一些系统调用。

在开始实验之前，你需要看完[第一节课的视频](https://www.bilibili.com/video/BV19k4y1C7kA)和阅读[配套书籍第一章](https://th0ar.gitbooks.io/xv6-chinese/content/index.html)的内容，不分先后顺序。

个人省流版：

- 课程目标是理解操作系统的设计和实现，为了深入了解具体的工作原理，老师将会带着我们手写一个名为 xv6 的操作系统。

# Boot xv6 (easy)

搭建 xv6 的编译环境，启动系统并运行 `ls` 指令。

网上有很多搭环境的介绍，我这里就不多赘述。如果懒得去折腾的话，可以借助 docker 使用其他同学制作好的镜像：https://zhuanlan.zhihu.com/p/449687883。

- `make qemu`，构建并运行 xv6 操作系统。
- xv6 没有 `ps` 命令，但你可以使用 `Ctrl-p` 打印每个进程的信息。
- 退出 qemu 请输入: `Ctrl-a` `x`
- `make grade` 启动打分系统，测试程序会自动检查你的作业是否通过。

# sleep (easy)

> Implement the UNIX program sleep for xv6; your sleep should pause for a user-specified number of ticks. A tick is a notion of time defined by the xv6 kernel, namely the time between two interrupts from the timer chip. Your solution should be in the file user/sleep.c.
> 为 xv6 系统写一个 sleep 程序，调用系统提供的 sleep() 函数，让程序暂停指定数量的时钟 tick。

Some hints:

- 查看 user 目录下的程序（例如 user/echo.c 、 user/grep.c 和 user/rm.c ），看看他们怎么获取从命令行输入的参数。
- 因为命令行参数的类型是字符串，你需要调用 atoi() 将参数转为 int 类型。
- 使用系统提供的 sleep() 函数完成暂停功能。
- 在程序最后，记得调用 exit() 函数退出进程。
- 将 sleep.c 文件添加到 Makefile 的 UPROGS 中，然后使用 make qemu 编译。

在 `user` 目录下，新建 `sleep.c` 文件

```c
#include "kernel/types.h"
#include "user/user.h"

int main(int argc, char *argv[]) {
    // 参数校验，如果输入错误，给用户提示正确用法。
    if (argc != 2) {
        printf("Usage: %s <ticks>\n", argv[0]);
        exit(1);
    }
    // 获取命令行传进来的参数，假如输入： sleep 3
    // argv[0] = sleep，第一个参数是程序名称 sleep。
    // argv[1] = 3，第二个参数是需要暂停多少个 tick
    char *arg = argv[1];
    // string 转 int，如果输入值非阿拉伯数字（比如输入字母或中文）则返回默认值 0
    int ticks = atoi(arg);
    printf("Ready to sleep for %d ticks...\n", ticks);
    // 调用系统提供的函数进行暂停
    sleep(ticks);
    printf("Sleep finished. Goodbye!\n");
    exit(0);
}
```

打开根目录下的 `Makefile` 文件，找到 `UPROGS` 块，在最后一行新增 `$U/_sleep\`

```makefile
UPROGS=\
$U/_cat\
$U/_echo\
...
$U/_wc\
$U/_zombie\
$U/_sleep\ # 新增项
```

最后运行 `./grade-lab-util sleep` 命令，来测试代码是否通过。

![img.png](imgs/xv6-lab1-sleep-grade.png)

# pingpong (easy)

> Write a program that uses UNIX system calls to ''ping-pong'' a byte between two processes over a pair of pipes, one for each direction. The parent should send a byte to the child; the child should print "<pid>: received ping", where <pid> is its process ID, write the byte on the pipe to the parent, and exit; the parent should read the byte from the child, print "<pid>: received pong", and exit. Your solution should be in the file user/pingpong.c.
> 利用 fork()、pipe()、read()、write() 等系统函数，写个名为乒乓的程序，完成父子进程通信，子进程收到以后打印 ‘<pid>: received ping’ 然后将字节发送给父进程后退出，父进程收到回复打印 ‘<pid>: received pong’，over。

- 父进程先说话，子进程收到消息后打印 ‘<pid>: received ping’ 然后退出自己。
- 父进程接收到子进程消息，打印 ‘<pid>: received pong’ 然后退出进程。

在 `user` 目录下，新建 `pingpong.c` 文件

```c
#include "kernel/types.h"
#include "user/user.h"

int main(int argc, char *argv[]) {

    // 题目无关，测试 fork 后，父进程修改普通变量子进程是否会同步修改
    int i = 100;

    // 创建两个管道描述符，前者用于父进程向子进程通信（读和写），后者用于子进程向父进程通信（读和写）
    // 每个管道描述符长度为 2，其中，0 用于读数据，1 用于写数据。
    int fd_p2c[2], fd_c2p[2];

    // 调用 pipe() 系统函数，创建两个管道
    pipe(fd_p2c);
    pipe(fd_c2p);

    // 调用系统函数 fork() 创建新的进程，父子进程都会接着从本行代码开始向下执行，上面的代码就不用管了。
    if (fork() == 0) {
        // 从这里开始，执行的是子进程代码
        sleep(10);
        // 打印当前 pid、i 的地址和值，以及两个管道地址，fork 会复制父进程的文件描述符表，所以父子进程打印的地址是相同的
        printf("here's child process, pid = %d, i = %d, i addr is %pn, fd_p2c addr is %pn, fd_c2p addr is %pn \n\n", getpid(), i, &i, &fd_p2c, &fd_c2p);

        int port_read = fd_p2c[0];
        int port_write = fd_c2p[1];

        char content_receive[1024] = {0};
        char content_send[1024] = {"daddy!\n"};

        // 读取父进程发送的消息，默认阻塞调用
        read(port_read, content_receive, sizeof(content_receive));
        printf("child received: %s", content_receive);
        printf("%d: received ping\n\n", getpid());

        // 写入消息至管道，回复父进程
        write(port_write, content_send, sizeof(content_send));

        sleep(10);

        exit(0);
    } else {
        // 不为 0 则表示是父进程的代码
        i = 101;
        printf("here's parent process, pid = %d, i = %d, i addr is %pn, fd_p2c addr is %pn, fd_c2p addr is %pn \n", getpid(), i, &i, &fd_p2c, &fd_c2p);

        int port_read = fd_c2p[0];
        int port_write = fd_p2c[1];

        char content_receive[1024] = {0};
        char content_send[1024] = {"call me daddy please \n"};

        // 题目要求父进程先发送消息
        write(port_write, content_send, sizeof(content_send));

        // 发完消息等待子进程回复
        read(port_read, content_receive, sizeof(content_receive));
        printf("parent received: %s", content_receive);
        printf("%d: received pong\n\n", getpid());

        sleep(10);

        exit(0);
    }
}
```

打开根目录下的 `Makefile` 文件，找到 `UPROGS` 块，在最后一行新增 `$U/_pingpong\`

```makefile
UPROGS=\
$U/_cat\
$U/_echo\
...
$U/_wc\
$U/_zombie\
$U/_sleep\
$U/_pingpong\ # 新增项
```

make qemu 编译启动，运行 pingpong 查看结果

![img.png](imgs/xv6-lab1-pingpong-result.png)

最后运行 `./grade-lab-util pingpong` 命令，来测试代码是否通过。

![img.png](imgs/xv6-lab1-pingpong-grade.png)

