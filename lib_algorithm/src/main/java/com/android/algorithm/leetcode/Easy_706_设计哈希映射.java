package com.android.algorithm.leetcode;

import org.junit.Test;

public class Easy_706_设计哈希映射 {

    /**
     * 不使用任何内建的哈希表库设计一个哈希映射（HashMap）。
     * <p>
     * 实现 MyHashMap 类：
     * <p>
     * MyHashMap() 用空映射初始化对象
     * void put(int key, int value) 向 HashMap 插入一个键值对 (key, value) 。如果 key 已经存在于映射中，则更新其对应的值 value 。
     * int get(int key) 返回特定的 key 所映射的 value ；如果映射中不包含 key 的映射，返回 -1 。
     * void remove(key) 如果映射中存在 key 的映射，则移除 key 和它所对应的 value 。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/design-hashmap
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        MyHashMap hashMap = new MyHashMap();
        hashMap.put(504, 155);
        hashMap.remove(504);
        hashMap.get(334);
        hashMap.put(570, 521);
        hashMap.remove(504);
        hashMap.remove(504);
        hashMap.put(507, 661);
        hashMap.put(175, 641);
        hashMap.get(504);
        hashMap.print();
    }

    /**
     * 链表实现
     * 执行结果：通过
     * 执行用时：176 ms, 在所有 Java 提交中击败了9.32%的用户
     * 内存消耗：40.8 MB, 在所有 Java 提交中击败了99.90%的用户
     */
    class MyHashMap {

        private Node root;

        public MyHashMap() {

        }

        public void put(int key, int value) {
            if (root == null) {
                root = new Node(key, value);
            } else {
                Node temp = root, last = null;
                while (temp != null) {
                    if (temp.k == key) {
                        temp.v = value;
                        return;
                    }
                    last = temp;
                    temp = temp.next;
                }
                last.next = new Node(key, value);
            }
        }

        public int get(int key) {
            Node temp = root;
            while (temp != null) {
                if (temp.k == key) {
                    return temp.v;
                }
                temp = temp.next;
            }
            return -1;
        }

        public void remove(int key) {
            Node temp = root, last = null;
            while (temp != null) {
                if (temp.k == key) {
                    if (last != null) {
                        last.next = temp.next;
                    } else root = temp.next;
                    return;
                }
                last = temp;
                temp = temp.next;
            }
        }

        public void print() {
            Node temp = root;
            while (temp != null) {
                System.out.print("k=" + temp.k + ",v=" + temp.v + " ");
                temp = temp.next;
            }
        }

        class Node {

            Node next;

            int k, v;

            public Node(int k, int v) {
                this.k = k;
                this.v = v;
            }
        }

    }

}
