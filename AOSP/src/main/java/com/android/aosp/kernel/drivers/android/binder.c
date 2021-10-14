
static HLIST_HEAD(binder_devices);
/*链表结构，记录总的进程数量对应的binder_proc*/
static HLIST_HEAD(binder_procs);

/*结构体，里面保存着各个进程自个儿的记录链表，其中包含自己的binder实体以及拥有其他进程实体的代理BpBinder*/
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

/*结构体，记录binder实体信息*/
struct binder_node
{
    int debug_id;
    struct binder_work work;
    union {
        struct rb_node rb_node;
        struct hlist_node dead_node;
    };
    struct binder_proc *proc;
    struct hlist_head refs;
    int internal_strong_refs;
    int local_weak_refs;
    int local_strong_refs;
    void __user *ptr;       // 注意这个域！
    void __user *cookie;    // 注意这个域！
};

/*结构体，记录拥有的binder代理*/
struct binder_ref
{
    int debug_id;
    struct rb_node rb_node_desc;
    struct rb_node rb_node_node;
    struct hlist_node node_entry;
    struct binder_proc *proc;
    struct binder_node *node;   // 注意这个node域
    uint32_t desc;
    int strong;
    int weak;
    struct binder_ref_death *death;
};

/*打开binder驱动程序，由c++层的ProcessState的open_driver方法调用*/
static int binder_open(struct inode *nodp, struct file *filp)
{
    /*打开binder驱动会初始化binder_proc，它对应c++层面的ProcessState
    *每个进程只拥有一个单例对象，它保存着binder实体和代理对象以及操作线程的信息
    */
    struct binder_proc *proc;
    binder_procs.add(proc)
}