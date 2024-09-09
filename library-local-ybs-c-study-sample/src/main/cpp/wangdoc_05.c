#include <stdio.h>

// 流程控制
int main(void) {

    // if switch while 啥的和 Java 相同
    for (int i = 0; i < 10; i++) {
        printf("%d\n", i);
        if (i == 2) goto top; // 跳转到 top 执行 top 及后面的代码
    }

    top :
    printf("top\n");

    return 0;
}

