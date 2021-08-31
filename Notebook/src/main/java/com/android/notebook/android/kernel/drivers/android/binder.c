
static HLIST_HEAD(binder_devices);
/*链表结构，记录每个binder实体？*/
static HLIST_HEAD(binder_procs);

struct binder_proc
{
    struct hlist_node proc_node;
    /*记录操作的线程，rb=red black红黑树*/
    struct rb_root threads;
    /*binder实体，自己进程由多少个实体被其他进程引用，这里就会有多少个对应的节点*/
    struct rb_root nodes;
    /*下面两个记录binder代理，自己进程引用其他进程的binder实体就保存在这，用两颗树是为了加快查找速度*/
    struct rb_root refs_by_desc;
    struct rb_root refs_by_node;
    int pid;
};

/*打开binder驱动程序*/
static int binder_open(struct inode *nodp, struct file *filp)
{
    /*打开binder驱动会初始化binder_proc，它对应c++层面的ProcessState
    *每个进程只拥有一个单例对象，它保存着binder实体和代理对象以及操作线程的信息
    */
    struct binder_proc *proc;
}