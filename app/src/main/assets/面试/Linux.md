
Linux中的进程组织方式：

- 进程链表
- 哈希表
- 就绪队列
- 等待队列

进程调度基本算法：

- 时间片轮转调度算法
- 优先权调度算法
- 多级反馈队列调度
- 抢占式实时调度方式

进程调度的依据：

- need_resched
- counter
- nice
- policy

## task_struct

Linux 中使用 task_struct 结构描述进程生命周期中涉及的所有信息，这样的数据结构一般被称作 PCB（Process Control Block）

### 任务 ID 相关：

```
long pid; // 唯一标识符
long tgid; // 线程组 id ，即 thread group id
```
### 任务状态：

```
long state; // 就绪、运行、阻塞等
long flags; 
```
### 亲缘关系：

```
task_struct parent;
task_struct children;
```
### 内存管理：

```
mm_struct *mm;
```

### 权限相关：

```
```
### 任务调度：

```
```

## 线程 / 进程区别

- 共用 MMU 内存映射表
- 共用 PID
