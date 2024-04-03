package com.android.algorithm.leetcode;

import org.junit.Test;

public class Medium_82_删除排序链表中的重复元素2 {

    @Test
    public void main() {
//        ListNode head = new ListNode(1, new ListNode(2, new ListNode(3, new ListNode(3, new ListNode(4, new ListNode(4, new ListNode(5)))))));
        ListNode head = new ListNode(1, new ListNode(1));
        ListNode res = deleteDuplicates(head);
        while (res != null) {
            System.out.println(res.val);
            res = res.next;
        }
    }

    /**
     * 双指针，思路
     * 两个指针分别记录上一个和上上个节点，当前节点和上个节点比较：
     * 1. 相同，则循环遍历下一个相同的，将相同项全部删除
     * 2. 不同，分别更新上个和上上个节点即可
     * 执行结果：通过
     * 执行用时：0 ms, 在所有 Java 提交中击败了100.00%的用户
     * 内存消耗：37.8 MB, 在所有 Java 提交中击败了62.10%的用户
     */
    public ListNode deleteDuplicates(ListNode head) {
        if (head == null) return null;
        ListNode dummyNode = new ListNode(0);
        dummyNode.next = head;
        ListNode preNode = head, prePreNode = dummyNode;
        ListNode temp = preNode.next;
        while (temp != null) {
            if (temp.val == preNode.val) {
                while (temp != null) {
                    if (temp.val != preNode.val) break;
                    temp = temp.next;
                }
                prePreNode.next = temp;
                preNode = temp;
                if (temp != null) temp = temp.next;
                continue;
            }
            prePreNode = preNode;
            preNode = temp;
            temp = temp.next;
        }
        return dummyNode.next;
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
