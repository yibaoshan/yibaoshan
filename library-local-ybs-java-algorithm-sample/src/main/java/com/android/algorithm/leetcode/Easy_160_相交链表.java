package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.HashSet;

public class Easy_160_相交链表 {

    /**
     * 给你两个单链表的头节点 headA 和 headB ，请你找出并返回两个单链表相交的起始节点。如果两个链表不存在相交节点，返回 null 。
     * <p>
     * 图示两个链表在节点 c1 开始相交：
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/intersection-of-two-linked-lists
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {

        int[] ret = new int[]{1, 8, 4, 5};
        ListNode retNode = new ListNode(0);
        ListNode next = retNode;

        for (int i = 0; i < ret.length; i++) {
            next.next = new ListNode(ret[i]);
            next = next.next;
        }
        retNode = retNode.next;

        int[] arr1 = new int[]{4, 4, 4};
        ListNode list1 = new ListNode(0);
        next = list1;

        for (int i = 0; i < arr1.length; i++) {
            next.next = new ListNode(arr1[i]);
            if (i == arr1.length - 1) next.next.next = retNode;
            else next = next.next;
        }

        int[] arr2 = new int[]{5, 6};
        ListNode list2 = new ListNode(0);
        next = list2;
        for (int i = 0; i < arr2.length; i++) {
            next.next = new ListNode(arr2[i]);
            if (i == arr2.length - 1) next.next.next = retNode;
            else next = next.next;
        }
        System.out.println(getIntersectionNode(list1.next, list2.next).val);
    }

    /**
     * 双指针
     * 执行结果：通过
     * 执行用时：1 ms, 在所有 Java 提交中击败了99.98%的用户
     * 内存消耗：41 MB, 在所有 Java 提交中击败了82.57%的用户
     */
    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        if (headA == null || headB == null) return null;
        ListNode pA = headA, pB = headB;
        while (pA != pB) {
            pA = pA == null ? headB : pA.next;
            pB = pB == null ? headA : pB.next;
        }
        return pA;
    }

    /**
     * hash大法
     */
    public ListNode getIntersectionNode2(ListNode headA, ListNode headB) {
        if (headA == null || headB == null) return null;
        HashSet<ListNode> hashSet = new HashSet<>();
        ListNode node = headA;
        while (node != null) {
            hashSet.add(node);
            node = node.next;
        }
        node = headB;
        while (node != null) {
            if (hashSet.contains(node)) return node;
            node = node.next;
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
    }

}
