#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include<stdio.h>

#define MAX 100

int main(void) {
    system("clear");
    printf("--- c 语言的预处理器 ---\n\n");

    // #define 常见的预处理器，可以简单理解为字符串替换，每条替换规则成为 宏。
    // 高级功能，宏还有类似正则表达式的功能，支持带参数的宏，支持运算符，知道有这么回事儿就行，用到的时候再查资料。

    // #undef,取消已定义的宏

    // #include，导入文件
    // #include <foo.h> 左右尖括号表示 加载系统提供的文件，写文件名即可，不需要给路径
    // #include "foo.h" 双引号表示 加载自己写的文件，需要写文件路径

    // #if...#endif emm，条件编译，类似 if else，但是条件编译的判断条件是编译时才判断的，而不是运行时。
    // 还有一堆 # 号开头的预处理语句，懒得看了

    return 0;
}