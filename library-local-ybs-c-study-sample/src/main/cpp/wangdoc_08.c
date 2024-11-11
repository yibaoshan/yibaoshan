#include <stdio.h>
#include <stdlib.h>

int print(int a) {
    printf("call print %d\n", a);
    return a;
}

int compute(int (*myfunc)(int), int a, int b) {
//    exit(0);
    return myfunc(a + b);
}

void p4() {
    printf("> p4 begin（函数指针）\n");

    int (*print_ptr)(int) = &print;

    print_ptr(4);
    (*print_ptr)(10);

    // 函数名本身就是指向函数代码的指针，通过函数名就能获取函数地址
    int ret = print == &print;
    // 说人话，打印 print 即可得到函数在内存中的地址
    printf("print == &print: %d\n", ret);

    // 写法一
    print(1);

    // 写法二
    // 取值
    (*print)(2);

    // 写法三
    // 取地址
    (&print)(3);

    // 写法四
    (*print_ptr)(4);

    // 写法五
    print_ptr(5);

    // 函数指针可以作为参数 和 返回值！
    compute(print, 6, 7);

    printf("> p4 end\n");
}

void p5() {
    printf("> p5 begin（函数的说明符）\n");
}

int main(void) {
    p4(); // 指针也可以指向函数，这样的设计被称为函数指针；函数指针可以作为参数和返回值传递。
    return 0;
}