#include <stdio.h>
#include <stddef.h>

void p1();

void p2();

void p3();

void p4();

void p5();

void increment(int *p);

// 指针
int main(void) {
    int x = 123;

    int *intPtr;
    // c 可以声明不赋值就使用，并且没有默认值，打印出来的是原来内存空间的值，随机的
//    printf("int* = %d\n", intPtr);
//    printf("sizeof(int*) = %d\n", sizeof(intPtr));

    // 并列在一行声明时，后面的 intPtr3 必须加上 *，否则就是个普通的 int 变量
    int8_t *intPtr2, intPtr3;
//    printf("int2* = %d\n", intPtr2);
//    printf("sizeof(int2*) = %d\n", sizeof(intPtr2));

//    printf("int3* = %d\n", intPtr3);
//    printf("sizeof(int3*) = %d\n", sizeof(intPtr3));

    // 两个 * 号，表示一个指向指针的指针，最终指向的时一个 int 类型的变量

    // & 运算符用来取出一个变量所在的内存地址。

    int *intPtr4 = &x;
//    printf("int4* 值 = %d\n", &x);
//    printf("int4* 值 = %d\n", *intPtr4);
//    printf("int4* 地址 = %d\n", &intPtr4);
//    printf("sizeof(int4*) = %d\n", sizeof(intPtr4));
    // 打印最终指向的类型长度
//    printf("sizeof(int4*) = %d\n", sizeof(*intPtr4));

    // 得到一个未初始化的指针，它将会分配到一个随机地址
    int *intPtr5;

//    printf("intPtr5* 地址 = %d\n", &intPtr5);

    // 为这个随机地址赋值
//    *intPtr5 = 1;
    // 执行报错 bus error
//    printf("intPtr5* 值 = %d\n", *intPtr5);

    // null 默认为 0
    int8_t *intPtr6 = NULL;
    // 读写这个 0 地址，会报错
//    printf("intPtr6* 值 = %d\n", *intPtr6);

    printf("\n\n\n");

    p1();
    p2();
    p3();
    p4();
    p5();

    return 0;
}

void increment(int *p) {
    // 对*p赋值，就表示改变指针所指向的那个地址里面的值
    *p = *p + 1; // *p就表示指针p所指向的那个值。
}

void p1() {
    // 指针的写法
    // c 语言要求比较低，下面四种写法都可以通过编译
    //int   *intPtr;
    //int * intPtr;
    //int*  intPtr;
    //int*intPtr;
    int *intPtr;
    // 不过，现在的 IDE 格式化以后，都是上面👆🏻的格式
}

void p2() {
    printf("> p2 begin（*号运算符）\n");
    int x = 1;
    int *intPtr = &x;
    // *运算符用来取出一个指针所指向的值。
    printf("intPtr value is %d\n", *intPtr);

    // 指针指向的值 + 1，地址不变
    // 下面两种方式得到的结果是一样的
    increment(&x);
    printf("取出 x 的地址并调用 increment 函数 = %d\n", *intPtr);

    increment(intPtr);
    printf("直接使用指针 intPtr 调用 increment 函数 = %d\n", x);

    printf("\n\n");
}

void p3() {
    printf("> p3 begin（&号运算符）\n");
    int x = 1;
    // &运算符用来取出一个变量所在的内存地址。
    printf("x's address is %p\n", &x);

    // 利用 & 运算符取出 x 的内存地址，并调用 increment 函数修改 x 的值
    increment(&x);
    printf("%d\n", x); // 2
    printf("\n\n");
}

void p4() {
    printf("> p4 begin（指针的初始化）\n");

    int *p;
    // 未赋值的指针 p，打印出来是随机的，而且，不能指针 p 进行读写操作，编译可以通过，但运行会报错
    printf("声明指针 p 但不赋值，直接打印 p = %d\n", p);

    int *p1 = NULL;
    // 赋值为 null 时，打印出来是 0，同样不能读写
    printf("声明指针 p1 并赋值为 null，打印 p1 = %d\n", p1);

    // 如果创建一个指针遍历，赋值时，该指针变量必须指向一个 已！经！存！在！的！地！址！
    // like this
    int x = 1;
    p = &x;
    printf("赋值 &x 到 p = %d\n", *p);

    printf("\n\n");
}

void p5() {
    printf("> p5 begin（指针的运算）\n");
    printf("指针本质是无符号整数，代表了内存地址，占位大小取决于编译期和目标平台\n在 32 系统占 4 个字节 32 位，64 系统占 8 个字节\n");

    short *j;
    j = (short *) 0x1234;
    j = j + 1; // 0x1236
    // 如果直接对 指针 进行 +1 -1 操作，表示当前指针向前/向后移动 1 位
    // 这里演示的是 short ，加减 1 都会移动 2 位，如果是 int ，就是 4 位，long 一般是 8 位
    printf("j = %p\n", j);

    short *j1;
    short *j2;

    j1 = (short *) 0x1234;
    j2 = (short *) 0x1236;

    // ptrdiff_t 是一个有符号整数，用来表示两个指针之间的距离
    // 用 int 接受也可以，但 ptrdiff_t 是标准库提供的类型，可以保证 跨平台的兼容性和 差值过大的范围问题
    ptrdiff_t dist = j2 - j1;
    printf("指针距离 %td\n", dist); // 1
    printf("比大小 %td\n", j1 < j2); // 1

    printf("\n\n");
}


