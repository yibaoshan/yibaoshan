#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include<stdio.h>

union var {
    char c[4];
    int i;
};

int main(void) {
    system("clear");
    printf("--- c 语言 union 结构体 ---\n\n");

    // 类似 struct，原理和使用上有些区别，一般用于节省内存。

    // 详细介绍：https://blog.csdn.net/huqinweI987/article/details/23597091

    union var data;
    data.c[0] = 0x04;//因为是char类型，数字不要太大，算算ascii的范围~
    data.c[1] = 0x03;//写成16进制为了方便直接打印内存中的值对比
    data.c[2] = 0x02;
    data.c[3] = 0x11;
//数组中下标低的，地址也低，按地址从低到高，内存内容依次为：04,03,02,11。总共四字节！
//而把四个字节作为一个整体（不分类型，直接打印十六进制），应该从内存高地址到低地址看，0x11020304，低位04放在低地址上。
    printf("%x\n", data.i);

    return 0;
}