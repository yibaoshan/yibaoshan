#include <stdio.h>

// 运算符
int main(void) {
    int i = 1;
    // = 是赋值操作
    // == 是比较操作
    if (i = 2) {
        // c 语言允许在 if 条件中赋值，i = 2 在 c 语言中不会报错，但是和预期逻辑是不符的
        // 所以，才会有要把常量放在判断表达式左边的要求
        // 当然，现在的 IDE 都非常智能，会提示你此操作的结果 always is true
    }
    i = (1, 2, 3);
    return i;
}

