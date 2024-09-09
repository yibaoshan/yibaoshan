#include <stdio.h>

// 变量
int main(void) {
    int i;
    int j; // 变量 j 运行时会分配空间，分配到空间里面原来的值不会被清空，所以，变量声明后要及时赋值，否则是随机值
    i = 1;
    printf("Hello World %i %i \n", i, j);
    return i;
}

