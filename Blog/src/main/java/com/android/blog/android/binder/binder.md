
今天换个姿势来聊 binder

- 既然 binder 是驱动层提供的功能，首先我们先来看，如果我们写内核代码，应该怎么使用 binder
- 然后，如果我们使用 c/C++ 开发，应该怎么使用 binder
- 最后，才是 Java 开发，有哪些类，分别提供了哪些功能？
- binder 和 epoll 是同级别的，都是内核提供的功能
- 大量介绍 handler 和 binder 的文章
- binder 复杂的原因之一是跨层太多了，
- 我们要知道，Google 引进 binder 到 Android ，是为了在保证性能的同时，最大程度的方便开发者。使用匿名内存
- 第一步，我们先来看一看 binder.c 的 include 列表，就能大致猜到 binder 的实现原理

### tips

#### binder 驱动层，mmap 一次拷贝

binder_mmap() 函数中，会申请一块物理内存

然后将这块内存，同时映射到内核空间和用户空间，换句话说，内核空间和用户空间，无论哪一方在读写，操作的都是同一块内存

#### 一次请求过程

当一个Client想要对Server发出请求时，它首先将请求发送到Binder设备上，由Binder驱动根据请求的信息找到对应的目标节点，然后将请求数据传递过去。

进程通过ioctl系统调用来发出请求：ioctl(mProcess->mDriverFD, BINDER_WRITE_READ, &bwr)

PS：这行代码来自于Framework层的IPCThreadState类。在后文中，我们将看到，IPCThreadState类专门负责与驱动进行通信

这里的mProcess->mDriverFD对应了打开Binder设备时的fd。BINDER_WRITE_READ对应了具体要做的操作码，这个操作码将由Binder驱动解析。bwr存储了请求数据，其类型是binder_write_read。

binder_write_read其实是一个相对外层的数据结构，其内部会包含一个binder_transaction_data结构的数据。binder_transaction_data包含了发出请求者的标识，请求的目标对象以及请求所需要的参数

#### service_manager 的查找

每一个Binder服务都需要有一个唯一的名称。由ServiceManager来管理这些服务的注册和查找。

ServiceManager本身也实现为一个Server对象。任何进程在使用ServiceManager的时候，都需要先拿到指向它的标识。然后通过这个标识来使用ServiceManager

1. 通过ServiceManager我们才能拿到Server的标识
2. ServiceManager本身也是一个Server

解决这个矛盾的办法其实也很简单：Binder机制为ServiceManager预留了一个特殊的位置。

这个位置是预先定好的，任何想要使用ServiceManager的进程只要通过这个特定的位置就可以访问到ServiceManager了（而不用再通过ServiceManager的接口）。

在Binder驱动中，有一个全局的变量：

static struct binder_node *binder_context_mgr_node;

当有进程通过ioctl函数并指定命令为BINDER_SET_CONTEXT_MGR的时候，驱动被认定这个进程是ServiceManager，具体逻辑在 binder_ioctl 函数中

ServiceManager应当要先于所有Binder Server之前启动。在它启动完成并告知Binder驱动之后，驱动便设定好了这个特定的节点。

在这之后，当有其他模块想要使用ServerManager的时候，只要将请求指向ServiceManager所在的位置即可。

在Binder驱动中，通过handle = 0这个位置来访问ServiceManager。

例如，binder_transaction中，判断如果target.handler为0，则认为这个请求是发送给ServiceManager的

### 驱动设计

有缘千里来相会

### framework 设计


### java 设计


### service_manager 启动流程

### app 启动流程，建立通信
