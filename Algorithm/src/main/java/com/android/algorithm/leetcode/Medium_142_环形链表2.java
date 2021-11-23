package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.HashSet;

public class Medium_142_环形链表2 {

    @Test
    public void main() {
        ListNode n2 = new ListNode(2);
        ListNode n4 = new ListNode(-4, n2);
        ListNode n0 = new ListNode(0, n4);
        n2.next = n0;
        ListNode head = new ListNode(3, n2);
//        ListNode res = detectCycle(head);
        ListNode res = detectCycle2(head);
        System.out.println(res.val);
    }

    /**
     * 暴力hash
     * 执行结果：通过
     * 执行用时：3 ms, 在所有 Java 提交中击败了20.97%的用户
     * 内存消耗：39.5 MB, 在所有 Java 提交中击败了5.03%的用户
     */
    public ListNode detectCycle(ListNode head) {
        HashSet<ListNode> hashSet = new HashSet<>();
        while (head != null) {
            if (hashSet.contains(head)) return head;
            hashSet.add(head);
            head = head.next;
        }
        return null;
    }

    /**
     * 快慢指针不通过
     * 快慢指针只能判断两个指针相等即为有环，但是不知道哪个是起点
     * 得加点逻辑
     */
    public ListNode detectCycle2(ListNode head) {
        if (head == null) return null;
        ListNode slow = head, fast = null;
        if (slow.next != null) fast = slow.next;
        while (slow != null) {
            if (slow == fast) {
                return slow;
            }
            slow = slow.next;
            if (fast != null && fast.next != null) fast = fast.next.next;
        }

        return null;
    }

    class ListNode {
        int val;
        ListNode next;

        ListNode(int x) {
            val = x;
            next = null;
        }

        public ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
        }
    }

}
