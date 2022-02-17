
struct task_struct {
	volatile long state;	// -1为不可运行, 0为可运行, >0为已中断
	int lock_depth;		// 锁的深度
    unsigned int policy; // 调度策略：一般有FIFO，RR，CFS
	pid_t pid;   // 进程标识符,用来代表一个进程
	struct task_struct *parent;	// 父进程
	struct list_head children;	// 子进程
	struct list_head sibling;   // 兄弟进程
}

struct thread_info {
	struct task_struct	*task;		/* main task structure */
	struct exec_domain	*exec_domain;	/* execution domain */
	unsigned long		flags;		/* low level flags */
	__u32			cpu;
	int			preempt_count; /* 0 => preemptable, <0 => BUG */
	mm_segment_t		addr_limit;	/* thread address space */
	struct restart_block	restart_block;
};
