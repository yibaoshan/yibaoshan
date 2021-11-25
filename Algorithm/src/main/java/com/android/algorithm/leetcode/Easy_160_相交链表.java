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
