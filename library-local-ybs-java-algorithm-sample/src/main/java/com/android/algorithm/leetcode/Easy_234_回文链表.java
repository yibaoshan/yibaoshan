package com.android.algorithm.leetcode;

public class Easy_234_回文链表 {

    /**
     * https://leetcode.cn/problems/palindrome-linked-list
     */


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

    /**
     * 执行用时分布6ms击败47.98%
     * 消耗内存分布62.80MB击败45.36%
     */
    public boolean isPalindrome(ListNode head) {
        int len = 0;
        ListNode node = null, tmp = head;
        // 反转链表，同时计算链表长度
        while (tmp != null) {
            node = new ListNode(tmp.val, node);
            tmp = tmp.next;
            len++;
        }
        len = len / 2;
        // 对比前半段和后半段
        while (len-- > 0) {
            if (node.val != head.val) return false;
            node = node.next;
            head = head.next;
        }
        return true;
    }

}
