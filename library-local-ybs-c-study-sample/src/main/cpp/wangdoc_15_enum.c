#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include<stdio.h>

enum colors {RED, GREEN, BLUE};

int main(void) {
    system("clear");
    printf("--- c 语言 enum 枚举类型 ---\n\n");

    printf("%d\n", RED); // 0
    printf("%d\n", GREEN);  // 1
    printf("%d\n", BLUE);  // 2

    return 0;
}