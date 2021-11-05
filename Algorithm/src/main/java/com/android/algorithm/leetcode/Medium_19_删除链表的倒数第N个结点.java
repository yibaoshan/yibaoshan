package com.android.algorithm.leetcode;

import org.junit.Test;

public class Medium_19_删除链表的倒数第N个结点 {

    /**
     * 给你一个链表，删除链表的倒数第 n 个结点，并且返回链表的头结点。
     * <p>
     * 进阶：你能尝试使用一趟扫描实现吗？
     * <p>
     *  
     * <p>
     * 示例 1：
     * <p>
     * <p>
     * 输入：head = [1,2,3,4,5], n = 2
     * 输出：[1,2,3,5]
     * 示例 2：
     * <p>
     * 输入：head = [1], n = 1
     * 输出：[]
     * 示例 3：
     * <p>
     * 输入：head = [1,2], n = 1
     * 输出：[1]
     * <p>
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/remove-nth-node-from-end-of-list
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        ListNode head = new ListNode(1, new ListNode(2));
        ListNode result = removeNthFromEnd(head, 2);
        while (result != null) {
            System.out.println(result.val);
            result = result.next;
        }
    }



    public ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode slow = head, fast = head;
        while (n >= 0) {
            if (fast != null) fast = fast.next;
            n--;
        }
        while (fast != null) {
            fast = fast.next;
            slow = slow.next;
        }
        if (slow == head) return head.next;
        else if (slow != null && slow.next != null) slow.next = slow.next.next;
        else return null;
        return head;
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
