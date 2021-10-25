package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Stack;

public class Easy_206_反转链表 {

    /**
     * 给你单链表的头节点 head ，请你反转链表，并返回反转后的链表。
     *  
     * <p>
     * 示例 1：
     * <p>
     * <p>
     * 输入：head = [1,2,3,4,5]
     * 输出：[5,4,3,2,1]
     * 示例 2：
     * <p>
     * <p>
     * 输入：head = [1,2]
     * 输出：[2,1]
     * 示例 3：
     * <p>
     * 输入：head = []
     * 输出：[]
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/reverse-linked-list
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */
    @Test
    public void main() {

    }

    /**
     * 双指针
     * 执行结果：通过
     * 执行用时：1 ms, 在所有 Java 提交中击败了5.48%的用户
     * 内存消耗：38 MB, 在所有 Java 提交中击败了87.69%的用户
     */
    public ListNode reverseList(ListNode head) {
        if (head == null) return null;
        Stack<ListNode> stack = new Stack<>();
        ListNode temp = head;
        while (temp != null) {
            stack.push(new ListNode(temp.val));
            temp = temp.next;
        }
        ListNode root = null;
        while (!stack.empty()) {
            if (root == null) {
                root = stack.pop();
                temp = root;
            } else {
                temp.next = stack.pop();
                temp = temp.next;
            }
        }
        return root;
    }

    /**
     * 评论区答案模仿，节点的下一个节点等于上一个就相当于翻转了，思路相当完美
     * 执行结果：通过
     * 执行用时：0 ms, 在所有 Java 提交中击败了100.00%的用户
     * 内存消耗：37.9 MB, 在所有 Java 提交中击败了94.35%的用户
     */
    public ListNode reverseList2(ListNode head) {
        if (head == null) return null;
        ListNode result = null;
        while (head != null) {
            result = new ListNode(head.val, result);
            head = head.next;
        }
        return result;
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
