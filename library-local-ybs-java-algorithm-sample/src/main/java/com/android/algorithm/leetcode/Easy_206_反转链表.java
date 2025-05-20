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
        int[] ret = new int[]{1, 8, 4, 5};
        ListNode retNode = new ListNode(0);
        ListNode next = retNode;

        for (int i = 0; i < ret.length; i++) {
            next.next = new ListNode(ret[i]);
            next = next.next;
        }
        retNode = retNode.next;
        System.out.println(reverseList(retNode).val);
    }


    /**
     * head = [1,2,3,4,5]
     */
    public ListNode reverseList(ListNode head) {
        // 传统解决方案
        if (head == null) return null;
        Stack<Integer> stack = new Stack<>();
        while (head != null) {
            stack.push(head.val);
            head = head.next;
        }
        head = new ListNode(0);
        ListNode ret = head;
        while (!stack.empty()) {
            ret.next = new ListNode(stack.pop());
            ret = ret.next;
        }
        return head.next;
    }


    /**
     * 正确打开方式
     * 0 ms 击败 100.00%
     * */
    public ListNode reverseList1(ListNode head) {
        ListNode ret = null;
        while (head != null) {
            ret = new ListNode(head.val, ret);
            head = head.next;
        }
        return ret;
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
