#include <stdio.h>
#include <stdlib.h>
#include <string.h>

int main(void) {
    system("clear");
    printf("--- c 语言 内存管理 ---\n\n");
    printf("一、前言\n\n");

    int *p = malloc(sizeof(int));

    *p = 12;
    printf("%d\n", *p); // 12

    free(p);
    printf("%d\n", *p); // 12

    return 0;
}