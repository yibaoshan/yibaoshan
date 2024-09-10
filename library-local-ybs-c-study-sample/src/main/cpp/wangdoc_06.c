#include <stdio.h>

int dostuff(int x, unsigned char c);

// 数据类型
int main(void) {
    if (0.1 + 0.2 == 0.3) {
        printf("true\n");
    } else {
        printf("false\n");
    }
    // IDE 会提示，编译期也会提示，但不会报错，能够正常运行
    unsigned char x = 256;
    printf("%u\n", x); // 溢出，输出 0
    printf("sizeof %d\n", sizeof(1.0L));
    // 浮点数赋予整数变量时，C 语言直接丢弃小数部分，而不是四舍五入。
    int a = 3.14;
    printf("%d\n", a);

    // 弱类型语言，不管入参传的啥，都按照形参类型强转
    // 返回值也一样，不管用什么类型接受，都只会返回函数定义的 int 类型
    long long ret = dostuff('1', 1);

    printf("ret: %lld\n", ret);

    printf("sizeof %d\n", sizeof(int64_t));

    return 0;
}

int dostuff(int x, unsigned char c) {
    printf("dostuff: %d %d\n", x, c);
    return 0;
}

