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


    /** head = [1,2,3,4,5]  */
    public ListNode reverseList(ListNode head) {

        ListNode ret = null; // 最终结果，把一个个节点拼装，连接在一起
        ListNode tmp = head; // 负责遍历链表，从 [1,2,3,4,5] 陆续变成：[2,3,4,5]、[3,4,5]、[4,5]、[5]

        while (tmp != null) {

            // 第一轮 next = [2,3,4,5]
            // 第一轮 next = [3,4,5]
            ListNode next = tmp.next;

            // 第一轮 ret = null , tmp.next = null ，因此 tmp = [1]
            // 第一轮 ret = [1] , tmp.next = [1] ，因此 tmp = [1,2]
            tmp.next = ret;

            // 第一轮 ret = [1]
            // 第一轮 ret = [1,2]
            ret = tmp;

            // 第一轮 next = [2,3,4,5] ，因此 tmp = [2,3,4,5]
            tmp = next;
        }
        return ret;
    }


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
