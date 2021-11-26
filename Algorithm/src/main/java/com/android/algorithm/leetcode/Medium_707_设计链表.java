package com.android.algorithm.leetcode;

import org.junit.Test;

public class Medium_707_设计链表 {

    /**
     * ["MyLinkedList","addAtHead","get","addAtHead","addAtHead","deleteAtIndex","addAtHead","get","get","get","addAtHead","deleteAtIndex"]
     * [[],[4],[1],[1],[5],[3],[7],[3],[3],[3],[1],[4]]
     * */

    @Test
    public void main() {
        MyLinkedList list = new MyLinkedList();
        list.addAtHead(4);
        list.get(1);
        list.addAtHead(1);
        list.addAtHead(5);
        list.deleteAtIndex(3);
        list.addAtHead(7);
        System.out.println(list.get(3));
        System.out.println(list.get(3));
        System.out.println(list.get(3));
        list.print();
    }

    class MyLinkedList {

        private Node root;
        private int length = 0;

        public MyLinkedList() {

        }

        public int get(int index) {
            if (index > length - 1 || index < 0) return -1;
            int count = 0;
            Node node = root;
            while (node != null) {
                if (count == index) {
                    return node.val;
                }
                count++;
                node = node.next;
            }
            return -1;
        }

        public void addAtHead(int val) {
            if (root == null) root = new Node(val);
            else root = new Node(val, root);
            length++;
        }

        public void addAtTail(int val) {
            if (root == null) {
                root = new Node(val);
            } else {
                Node node = root;
                while (node.next != null) {
                    node = node.next;
                }
                node.next = new Node(val);
            }
            length++;
        }

        public void addAtIndex(int index, int val) {
            if (index < 0) return;
            if (index >= length) {
                return;
            }
            if (index == 0) {
                addAtHead(val);
                return;
            }
            int count = 0;
            Node node = root, pre = root;
            while (node != null) {
                if (count == index) {
                    pre.next = new Node(val, node);
                    break;
                }
                count++;
                pre = node;
                node = node.next;
            }
            length++;
        }

        public void deleteAtIndex(int index) {
            if (index < 0 || index >= length) return;
            int count = 0;
            Node node = root, pre = root;
            while (node != null) {
                if (count == index) {
                    if (node == root) root = root.next;
                    else pre.next = node.next;
                    break;
                }
                count++;
                pre = node;
                node = node.next;
            }
            length--;
        }

        private void print() {
            Node node = root;
            while (node != null) {
                System.err.print(node.val+" ");
                node = node.next;
            }
        }

        class Node {

            int val;
            Node next;

            public Node(int val) {
                this.val = val;
            }

            public Node(int val, Node next) {
                this.val = val;
                this.next = next;
            }
        }
    }

}
