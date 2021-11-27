package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayList;

public class Medium_143_重排链表 {

    @Test
    public void main() {
        ListNode node = new ListNode(1, new ListNode(2, new ListNode(3, new ListNode(4, new ListNode(5)))));
        print(node);
        System.out.println();
        reorderList(node);
        print(node);
    }

    /**
     * 双指针，思路：
     * 1. 遍历链表，使用List集合保存结果
     * 2. 左指针从左边右指针从右边开始遍历集合，依次将左右指针节点复制给next即可
     * <p>
     * 执行结果：通过
     * 执行用时：2 ms, 在所有 Java 提交中击败了51.95%的用户
     * 内存消耗：40.8 MB, 在所有 Java 提交中击败了80.80%的用户
     */
    public void reorderList(ListNode head) {
        if (head == null) return;
        ArrayList<ListNode> list = new ArrayList<>();
        ListNode temp = head;
        while (temp != null) {
            list.add(temp);
            temp = temp.next;
        }
        int left = 0, right = list.size() - 1;
        temp = head;
        while (left <= right) {
            if (left != 0 && left != right) {
                temp.next = list.get(left);
                temp = temp.next;
            }
            temp.next = list.get(right);
            temp = temp.next;
            left++;
            right--;
        }
        temp.next = null;
    }

    private void print(ListNode node) {
        ListNode temp = node;
        while (temp != null) {
            System.err.print(temp.val + ",");
            temp = temp.next;
        }
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
