#include <stdio.h>
#include <stdlib.h>


void p1() {
    int scores[100] = {[2] = 29, [9] = 7, [14] = 48};;
    scores[0] = 13;
    scores[99] = 42;
    printf("scores: %zu\n", sizeof(scores));
}

// 数组指针的加减法 #
void p6() {
    int arr[5] = {11, 22, 33, 44, 55};
    for (int i = 0; i < 5; i++) {
        printf("%d\n", *(arr + i));
    }
}

int main(void) {
    system("clear");
    system("ps");
    printf("\npart of c array\n\n");
    p1();
    p6();
    printf("c 语言需显式传递数组长度");
    return 0;
}