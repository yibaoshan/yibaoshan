package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.HashSet;

public class Easy_面试题_02_07_链表相交 {

    @Test
    public void main() {
        ListNode headA = new ListNode(4, new ListNode(1, new ListNode(8, new ListNode(4, new ListNode(5)))));
        ListNode headB = new ListNode(5, new ListNode(0, new ListNode(1, new ListNode(8, new ListNode(4, new ListNode(5))))));
        ListNode node = getIntersectionNode(headA, headB);
        while (node != null) {
            System.out.println(node.val);
            node = node.next;
        }
    }



    public ListNode getIntersectionNode2(ListNode headA, ListNode headB) {

        HashSet<ListNode> hashSet = new HashSet<>();
        while (headA!=null){
            hashSet.add(headA);
            headA = headA.next;
        }
        while (headB!=null){
            if (hashSet.contains(headB))return headB;
            headB = headB.next;
        }
        return null;

    }













    /**
     * 搞了半天求的是相交的节点指针是否相同，不是比较值，暴力循环
     * 执行结果：通过
     * 执行用时：5 ms, 在所有 Java 提交中击败了8.75%的用户
     * 内存消耗：44.1 MB, 在所有 Java 提交中击败了34.49%的用户
     */
    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        HashSet<ListNode> hashSet = new HashSet<>();
        while (headA != null) {
            hashSet.add(headA);
            headA = headA.next;
        }
        while (headB != null) {
            if (hashSet.contains(headB)) return headB;
            headB = headB.next;
        }
        return null;
    }

    public class ListNode {
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
