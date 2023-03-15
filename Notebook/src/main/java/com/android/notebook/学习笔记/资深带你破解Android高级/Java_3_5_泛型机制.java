package com.android.notebook.学习笔记.资深带你破解Android高级;

import java.util.ArrayList;

/**
 * author :Bob.
 * date : 2020/10/28
 * desc :
 */
public class Java_3_5_泛型机制 {

    /*
     * @description: 泛型在编译器会被类型擦除为object
     *
     * 泛型的使用范围：
     * 泛型类、泛型接口、泛型方法
     *
     * 通配符：
     * 上界通配符：class Test<? extends Object>
     * 下界通配符：class Test<? super Integer>
     *
     *
     *
     * 泛型的优点：
     * 1.类型安全，在编码过程中编译器会检查类型
     * 2.同上，消除强转
     * 3.编译期有效
     *
     * @param
     * @return
     */
    public void main(String[] args) {
        System.out.println(getValue("e"));
        ArrayList<Integer> integers = new ArrayList<>();
        printList(integers);
    }

    private void printList(ArrayList<Integer> integers) {
        for (int i : integers) System.out.println(i);
    }

    private <E> E getValue(E e) {
        return e;
    }

    public class Test<T> {
        T data;
    }

}
