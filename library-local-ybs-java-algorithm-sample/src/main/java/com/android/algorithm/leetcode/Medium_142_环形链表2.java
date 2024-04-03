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
     * 快慢指针通过
     * 快指针每次两步，慢指针每次一步
     * 快慢指针相遇的地方再燃头节点和相遇节点一起向下走，遇到的节点为入口
     */
    public ListNode detectCycle2(ListNode head) {
        /**
         * 思路，经典快慢指针
         *
         * 快指针每次两步，慢指针每次一步
         * 快慢指针相遇的地方，再燃头节点和相遇节点一起向下走，遇到的节点为入口
         *
         * 时间复杂度 O(n)
         * */
        ListNode slow = head;
        ListNode fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) {// 有环
                ListNode index1 = fast;
                ListNode index2 = head;
                // 两个指针，从头结点和相遇结点，各走一步，直到相遇，相遇点即为环入口
                while (index1 != index2) {
                    index1 = index1.next;
                    index2 = index2.next;
                }
                return index1;
            }
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
