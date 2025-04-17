
# chapter 8 of book xv6 book

磁盘硬件用来持久化数据，文件系统负责组织如何使用磁盘

- 文件系统需要支持 crash recovery、并发

xv6 文件系统实现分为 7 层

1. Disk 磁盘层，读写磁盘硬件
   - xv6 运行在 qemu 虚拟机上，使用 virtio 驱动与磁盘交互，当它就是硬件磁盘好了
   - 以块（block）为单位读写数据，xv6 中块大小为 1kb
2. Buffer cache 缓冲层，缓存数据，减少磁盘读写次数
   - 上一实验重新设计并发的模块，实现在 bio.c
3. Log 日志层，保证数据持久化，保证 crash recovery
   - 保证 crash recovery，实现在 log.c
   - 使用 write-ahead logging 机制，保证文件操作的原子性
4. Inode 索引节点，描述文件元数据，比如文件大小、权限等
   - 实现文件的读写操作，代码在 fs.c
5. Directory 目录层，描述目录结构，比如父目录、子目录、文件名等
6. Pathname 路径层，描述文件路径，比如 /usr/bin/ls
   - 用户可读的路径转换为 inode
7. File descriptor 文件描述层，提供统一的文件抽象，file.c

`fs.h` 定义了磁盘布局，一共可以分为 6 个部分（按顺序）：

1. 引导块（Boot block）：
   - 块 0 ，存储引导代码，系统启动时使用
2. 超级块（Superblock） ：
   - 块 1，存储文件系统的元信息，包含：块的数量、inode 块区的数量、日志区大小等
   - 结构体 `struct superblock`
3. 日志区（Log） ：
   - 从块 2 开始 ，默认大小为 30 个块 ，用于故障恢复
4. inode 块区（Inode blocks） ：
   - 紧接日志区 ，存储所有文件的 inode ，每个 inode 包含文件的元数据
5. 位图块（Bitmap block） ：
   - 一个块 ，记录数据块的使用情况 ，用于分配新的数据块
6. 数据块区（Data blocks） ：
   - 剩余所有块 ，存储文件和目录的实际内容 ，从位图块之后开始

### 日志层

核心数据结构 ：

```c
struct log {
  struct spinlock lock;
  int start;       // 日志区起始块号
  int size;        // 日志区块数
  int outstanding; // 有多少事务在进行中
  int committing;  // 是否正在提交事务
  int dev;         // 设备号
  struct logheader lh;
};

struct logheader {
  int n;           // 日志块数量
  int block[MAXOPBLOCKS];  // 被修改块的块号
};
```

主要函数功能 ：

- `initlog` ：系统启动时初始化日志，如果发现未完成的事务，进行恢复
- `begin_op` ：开始一个新事务并确保日志空间足够，增加outstanding计数
- `log_write` ：记录要修改的块，将修改先写入日志区，不直接写入目标位置
- `end_op` ：结束一个事务，如果是最后一个事务，执行提交，减少outstanding计数
- `commit` ：将日志写入磁盘，将修改应用到实际位置，清除日志

工作流程 ：

```
正常操作流程：
1. begin_op() 开始事务
2. 多次调用 log_write() 记录修改
3. end_op() 结束事务
4. commit() 提交修改

恢复流程：
1. 系统启动时检查日志
2. 如果存在未完成事务
3. 重新应用日志中的修改

- 使用自旋锁保护日志结构
- outstanding计数控制并发事务数
- committing标志防止并发提交
```

### Inode 层

文件系统的核心层，Inode 有两个数据结构，磁盘上的 dinode（Disk inode） ：

```c
kernel/fs.h
struct dinode {
  short type;           // 文件类型：0=未使用，1=普通文件，2=目录，3=设备
  short major;          // 主设备号：如果是设备文件，标识设备类型
  short minor;          // 次设备号：如果是设备文件，标识具体设备
  short nlink;          // 硬链接数：有多少个目录项指向这个inode
  uint size;           // 文件大小：文件包含的字节数
  uint addrs[NDIRECT+1];   // 数据块地址数组：
                          // 前12个是直接块地址
                          // 最后一个是间接块地址
};
```

运行中内存里面的 inode 结构体：

```c
kernel/file.h
struct inode {
  uint dev;           // 设备号：inode所在的设备
  uint inum;          // inode编号：在inode表中的索引
  int ref;           // 引用计数：有多少进程在使用这个inode
  struct sleeplock lock; // 睡眠锁：保护inode的内容
  int valid;         // 有效标志：inode是否从磁盘加载

  // 以下字段从dinode复制而来
  short type;        // 文件类型
  short major, minor;// 设备号
  short nlink;       // 硬链接数
  uint size;        // 文件大小
  uint addrs[NDIRECT+1]; // 数据块地址
};
```

Inode层的主

磁盘块 1 的超级块结构体 ：

```c
struct superblock {
  uint magic;        // 魔数，用于标识文件系统
  uint size;         // 文件系统的总块数
  uint nblocks;      // 数据块数量
  uint ninodes;      // inode数量
  uint nlog;         // 日志块数量
  uint logstart;     // 日志区的起始块号
  uint inodestart;   // inode区的起始块号
  uint bmapstart;    // 位图块的起始块号
};
```

- 文件创建：先分配dinode，再创建对应的inode
- 文件打开：读取dinode到内存，创建inode
- 文件修改：先修改inode，最终同步到dinode
- 文件关闭：释放inode，必要时更新dinode

系统启动后从超级块获取 inode 信息，有多少个，从哪个块开始算

```c
void
fsinit(int dev) {
  readsb(dev, &sb);  // 读取超级块
  // sb.ninodes: inode总数
  // sb.inodestart: inode区起始块号
}
```

# Lec 14 File Systems

从操作系统的角度来说，可以把磁盘看作是一个巨大的 block 数组

- block0要么没有用，要么被用作boot sector来启动操作系统。
- block1通常被称为super block，它描述了文件系统。它可能包含磁盘上有多少个block共同构成了文件系统这样的信息。我们之后会看到XV6在里面会存更多的信息，你可以通过block1构造出大部分的文件系统信息。
- 在XV6中，log从block2开始，到block32结束。实际上log的大小可能不同，这里在super block中会定义log就是30个block。
- 接下来在block32到block45之间，XV6存储了inode。我之前说过多个inode会打包存在一个block中，一个inode是64字节。
- 之后是bitmap block，这是我们构建文件系统的默认方法，它只占据一个block。它记录了数据block是否空闲。
- 之后就全是数据block了，数据block存储了文件的内容和目录的内容。

bitmap block，inode blocks和log blocks被统称为metadata block。它们虽然不存储实际的数据，但是它们存储了能帮助文件系统完成工作的元数据。

# Lec 15 Crash recovery

- 之所以我们认为logging很重要，是因为这是一个巨大的成功和重要的思想，几乎可以认为logging是一种魔法，它可以用在任何一个已知的存储系统的故障恢复流程中，它在很多地方都与你想存储的任何数据相关。所以你们可以在大量的存储场景中看到log，比如说数据库，文件系统，甚至一些需要在crash之后恢复的定制化的系统中。
- 你们也可以发现log作为从故障中恢复的机制，在分布式系统中也有大量的应用。因为log是一种可以用来表示所有在crash之前发生事情的数据结构，如果能够理解log，那么就可以更容易的从crash中恢复过来。
- 除此之外，当你尝试构建高性能logging系统时，log本身也有大量有意思的地方。

1. write_log()：将修改的块写入日志区
2. write_head()：写入日志头
3. install_trans()：将修改应用到实际位置
4. 清除日志