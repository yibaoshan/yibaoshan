package com.android.algorithm.leetcode;

import org.junit.Test;

public class Medium_24_两两交换链表中的节点 {

    /**
     * 给定一个链表，两两交换其中相邻的节点，并返回交换后的链表。
     * <p>
     * 你不能只是单纯的改变节点内部的值，而是需要实际的进行节点交换。
     */

    @Test
    public void main() {
        ListNode head = new ListNode(1, new ListNode(2, new ListNode(3, new ListNode(4))));
        head = swapPairs(head);
        head = swapPairs2(head);
        while (head != null) {
            System.out.println(head.val);
            head = head.next;
        }
    }

    /**
     * 执行用时分布0ms击败100.00%
     * 消耗内存分布40.57MB击败24.48%
     */
    public ListNode swapPairs_2025(ListNode head) {
        ListNode ret = new ListNode(), tmp = ret;
        while (head != null) {
            int n = 2;
            ListNode once = null;
            while (n-- > 0 && head != null) {
                //System.out.println("enter traverse " + head.val);
                once = new ListNode(head.val, once);
                head = head.next;
            }
            //tmp.next = once;
            //tmp = tmp.next;
            while (once != null) {
                //System.out.println("print once " + once.val);
                tmp.next = once;
                once = once.next;
                tmp = tmp.next;
            }
        }
        return ret.next;
    }

    /**
     * 递归交换法，较为简单
     * 执行结果：通过
     * 执行用时：0 ms, 在所有 Java 提交中击败了100.00%的用户
     * 内存消耗：35.9 MB, 在所有 Java 提交中击败了69.44%的用户
     */
    public ListNode swapPairs(ListNode head) {
        if (head == null) return null;
        if (head.next != null) head = swap(head, head.next);
        return head;
    }

    private ListNode swap(ListNode node1, ListNode node2) {
        if (node1 == null) return node2;
        if (node2 == null) return node1;
        if (node2.next != null && node2.next.next != null) {
            node1.next = swap(node2.next, node2.next.next);
        } else node1.next = node2.next;
        node2.next = node1;
        return node2;
    }

    /**
     * 思路：每次交互n1，n2，若n2后面有两个节点，先交换后面两个节点
     * 执行结果：通过
     * 执行用时：0 ms, 在所有 Java 提交中击败了100.00%的用户
     * 内存消耗：39 MB, 在所有 Java 提交中击败了27.28%的用户
     */
    public ListNode swapPairs2(ListNode head) {
        return swap2(head, head.next);
    }

    private ListNode swap2(ListNode n1, ListNode n2) {
        if (n1 == null) return n2;
        if (n2 == null) return n1;
        if (n2.next != null && n2.next.next != null) n2.next = swap2(n2.next, n2.next.next);
        n1.next = n2.next;
        n2.next = n1;
        return n2;
    }

    public class ListNode {
        int val;
        ListNode next;

        ListNode() {
        }

        ListNode(int val) {
            this.val = val;
        }

        ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
        }
    }

}
