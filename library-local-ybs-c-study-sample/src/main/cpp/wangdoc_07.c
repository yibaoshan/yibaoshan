#include <stdio.h>

void increment(int *p);

// 指针
int main(void) {
    int x = 123;

    // 指针本质是无符号整数，代表了内存地址，占位大小取决于编译期和目标平台
    // 32 系统占 4 个字节 32 位，64 系统占 8 个字节

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
    printf("int4* 值 = %d\n", &x);
    printf("int4* 值 = %d\n", *intPtr4);
    printf("int4* 地址 = %d\n", &intPtr4);
    printf("sizeof(int4*) = %d\n", sizeof(intPtr4));
    // 打印最终指向的类型长度
    printf("sizeof(int4*) = %d\n", sizeof(*intPtr4));

    // 指针指向的值 + 1，地址不变
    // 下面两种方式得到的结果是一样的
    increment(&x);
    increment(intPtr4);
    printf("int4* 加一后值 = %d\n", *intPtr4);
    printf("int4* 加一后地址 = %d\n", &intPtr4);

    // 得到一个未初始化的指针，它将会分配到一个随机地址
    int *intPtr5;

    printf("intPtr5* 地址 = %d\n", &intPtr5);

    // 为这个随机地址赋值
//    *intPtr5 = 1;
    // 执行报错 bus error
//    printf("intPtr5* 值 = %d\n", *intPtr5);

    // null 默认为 0
    int8_t *intPtr6 = NULL;
    // 读写这个 0 地址，会报错
//    printf("intPtr6* 值 = %d\n", *intPtr6);

    return 0;
}

void increment(int *p) {
    *p = *p + 1;
}


