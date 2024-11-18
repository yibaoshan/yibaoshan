#include <stdio.h>
#include <stdlib.h>
#include <string.h>

struct fraction {
    int numerator;
    int denominator;
};

int main(void) {
    system("clear");
    printf("--- c 语言 struct 结构体 ---\n\n");

    // 区别于面向对象编程，c 声明了就会创建内存，即刻能用
    struct fraction f1, f2;

    f1.numerator = 22;
    f1.denominator = 7;

    // 声明并利用大括号赋值
    struct fraction f3 = {1, 2};

    printf("f1.numerator = %d\n", f2.numerator);
    printf("f1.denominator = %d\n", f1.denominator);

    return 0;
}