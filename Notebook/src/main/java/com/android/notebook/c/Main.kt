package com.android.notebook.c

class Main {

    /**
     * 如何运行c程序：
     * @see Run
     *
     * C语言关键字：
     * @see KeyWord
     *
     * C常量和变量
     * @see Const
     *
     * C指针
     * @see Pointer
     *
     * C结构体
     * @see Struct
     *
     * C共用体
     * @see Union
     *
     * C typedef
     * @see Typedef
     *
     * 其他：
     * 左值和右值：
     * @see LRValue
     *
     * TODO 还没搞懂的：
     * 1. 函数指针和回调函数
     * 2. 位域
     *
     * */

    /**
     * #include <stdio.h>
     * include导入语句=import
     * stdio.h 调用printf输出时使用的头文件
     * */

    private class Run {

        /**
         *
        #include <stdio.h>

        int main()
        {
        /* 我的第一个 C 程序 */
        printf("Hello, World! \n");

        return 0;
        }
         * */

        /**
         * 1. 随便打开文本编辑器，将上面的内容拷贝进入，保存文件名test.c
         * 2. 终端输入gcc test.c，根目录会生成a.out文件
         * 3. 终端运行./a.out
         *
         * */

    }

    private class KeyWord {

        /**
         * 和Java比较，相同项：
         * 关键字：const、static、void
         * 逻辑控制：if、break、case、continue、default、do、else、for、return、switch、while
         * 变量类型：double、char、enum、float、int、long、short
         *
         * 不相同项：
         * 1. auto 声明自动变量
         * 2. extern 告诉编译器这个变量使用其他地方的值
         * @see KeyExtern
         * 3. goto
         * 4. register 声明把变量丢在寄存器里，不可进行&运算
         * 5. signed 声明有符号类型变量或函数
         * 6. sizeof 计算数据类型或变量长度（即所占字节数）
         * 7. struct 声明结构体类型
         * 8. typedef 用以给数据类型取别名
         * 9. unsigned 声明无符号类型变量或函数
         * 10. union 声明共用体类型
         * 11. volatile 说明变量在程序执行中可被隐含地改变
         * */

        private class KeyExtern {

            /**
             * extern用在变量声明中常常有这样一个作用，你在*.c文件中声明了一个全局的变量
             * 这个全局的变量如果要被引用，就放在*.h中并用extern来声明。
             *
             * 用法1：
             * father.h中有变量int num = 0
             * child1.c和child2.c同时include father.h会编译失败，因为num重复定义
             * 如果father.h使用extern int num = 0，则child.c和child.c中的num指向同一地址
             *
             * */

        }

    }

    private class LRValue {

        /**
         * 左值Lvalues
         * 可以在=号左边或者右边，指向内存位置
         *
         * 右值Rvalues
         * 只能出现在=号右边，表示值
         * */

    }

    private class Const {

        /**
         * 常量是固定值，在程序执行期间不会改变。这些固定的值，又叫做字面量。
         * 在 C 中，有两种简单的定义常量的方式：
         * 1. 使用 #define 预处理器。
         * 举例：#define NUM = 10
         * 2. 使用 const 关键字。
         * 举例：const int NUM = 10
         * */

    }

    private class Pointer {

        /**
        通过&获取指针指向的地址
        void test(){
        int num = 10;
        int *p;
        p=&num
        print(&num)0x7ffeeaae08d8
        print(p)0x7ffeeaae08d8
        print(*p)10
        }
         * */

        /**
         * 作为参数
         * void add(int x, int y)传入的是值
         * void add(int *x,int *y)传入的是地址
         * */

        /**
         * 指向指针的指针
         * **num 双星号声明即可
         * */

        /**
         * 从函数返回指针
         * 由于c不支持返回数组类型，可以用指针替代
         * getNums(){
        }
         * */

    }

    private class Struct {

        /**
         * 等同于Java中的对象
         * 调用时稍有不同
         *
         * struct People{
         *  char name;
         * }
         *
         * Java调用
         * people.name
         *
         * c调用
         * people->name
         *
         * */

    }

    private class Union {

        /**
         * 任何时候都只能有一个成员有值的神器对象
         * union People{
         *  int age;
         *  char name;
         * }
         *
         * 示例一：
         * people.age = 10
         * people.name = 'bob'
         *
         * print(age)1917853763
         * print(name)bob
         * 打印出来的age值不对，name由于是最后一个设置的值，所以值是正确的
         *
         * 示例二：
         * people.age = 10
         * print(age)10
         *
         * people.name = 'bob'
         * print(name)bob
         *
         * */

    }

    private class Typedef {

        /**
         * 定义类型
         * 举例：typedef unsigned char byte
         * */

    }

}