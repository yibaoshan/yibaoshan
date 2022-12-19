
通过设立 state 变量来表示有锁无锁

如果 state 为0 ，表示无锁，使用 cas 尝试占用，成功返回 true

如果 state 不为 0 ，表示有锁，看看锁持有者是不是自己，是自己 返回 true ，否则 false