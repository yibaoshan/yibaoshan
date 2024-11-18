#include <stdio.h>
#include <stdlib.h>
#include <string.h>

void p2() {
    printf("\n二、字符串变量的声明\n\n");
    printf("1.字符串变量可以声明成一个字符数组，也可以声明成一个指针，指向字符数组。（看 p2 代码）\n");

    // 写法一，数组声明长度只能大于等于实际字符串长度
    char s[14] = "Hello, world!";

    // 声明为数组类型时，数组元素可以被修改，，，
    // 如果想要达到不可修改的效果，可以用 const 修饰数组
    s[0] = 'h';

    // 写法二
    char *s1 = "Hello, world!";
    // 指针类型的字符串，可以指向其他的字符串
    s1 = "h2";

    printf("数组类型: %s, 指针类型: %s\n", s, s1);
}

void p3() {
    printf("\n三、字符相关函数\n\n");
    char *str = "hello";
    int len = strlen(str); // 5
    printf("1. 长度函数 strlen()，字符串 %s,长度: %d\n", str, len);

    char str_copy[5];
    strncpy(str_copy, str, 5);
    printf("2. 拷贝函数 strncpy()，值: %s\n", str_copy);

    char s1[12] = "hello";
    char s2[6] = "world";

    strcat(s1, s2);

    printf("3. 连接函数 strcat(): %s\n", s1);
}

int main(void) {
    system("clear");
    printf("--- c 语言 字符串 ---\n\n");
    printf("一、前言\n\n");
    printf("1. C 语言没有单独的字符串类型，字符串被当作字符数组，即 char 类型的数组。比如，字符串 “Hello” 是当作数组{'H', 'e', 'l', 'l', 'o'}处理的。\n");
    printf("2. 在字符串结尾，C 语言会自动\\0字符，表示字符串结束，所以，字符串“Hello”实际储存的数组是{'H', 'e', 'l', 'l', 'o', '\\0'}。\n");
    p2();
    p3();
    // 剩下的函数懒得看了，有机会用的话再百度
    return 0;
}