#include <stdio.h>
#include  <stdarg.h>

void counter(void) {
    // 函数内支持声明静态变量
    // static 初始化时只能是常量
    const int x = 0;
    static int count = 1 + x;
    printf("%d\n", count);
    count++;
}

// const 修饰的变量、形参都不可以修改
void f(const int *const p) {
    printf("%d\n", *p);
//    *p = 0; // 该行报错
    int a = 10000;
//    p = &a;
}

static int Twice(const int num) {
    int result = num * 2;
    return (result);
}

void p7() {
    printf("> p7 begin（static 修饰符）\n");
    counter();
    counter();

    int a = 10086;

    f(&a);

    printf("%d\n", *&a);

    printf("static method ret: %d\n", Twice(3));

    Twice(3);
}

// ToDo 这几个 va_xx 没看懂干嘛的
double average(int i, ...) {
    double total = 0;
    va_list ap;
    va_start(ap, i);
    for (int j = 1; j <= i; ++j) {
        total += va_arg(ap, double);
    }
    va_end(ap);
    return total / i;
}

int main(void) {
    printf("> 函数篇 \n\n");
    p7();
    printf("平均数：%f\n", average(3, 1.0, 2.0, 3.0));
    return 0;
}