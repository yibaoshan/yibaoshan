package com.android.algorithm.leetcode;

import org.junit.Test;

public class Easy_203_移除链表元素 {

    /**
     * 给你一个链表的头节点 head 和一个整数 val ，请你删除链表中所有满足 Node.val == val 的节点，并返回 新的头节点 。
     *  
     * <p>
     * 示例 1：
     * <p>
     * <p>
     * 输入：head = [1,2,6,3,4,5,6], val = 6
     * 输出：[1,2,3,4,5]
     * 示例 2：
     * <p>
     * 输入：head = [], val = 1
     * 输出：[]
     * 示例 3：
     * <p>
     * 输入：head = [7,7,7,7], val = 7
     * 输出：[]
     * <p>
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/remove-linked-list-elements
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */
    @Test
    public void main() {
        ListNode listNode = new ListNode(7, new ListNode(6, new ListNode(7, new ListNode(7))));
        ListNode result = removeElements2(listNode, 7);
        System.out.println(result);
    }

    /**
     * 7 , 6 , 7 , 7
     * <p>
     * 开始拼凑链表
     */
    public ListNode removeElements2(ListNode head, int val) {
        ListNode ret = new ListNode();
        ListNode tmp = ret;
        while (head != null) {
            if (head.val != val) {
                tmp.next = new ListNode(head.val);
                tmp = tmp.next;
            }
            head = head.next;
        }
        return ret.next;
    }

    private void print(ListNode head) {
        StringBuilder sb = new StringBuilder();
        while (head != null) {
            sb.append(head.val);
            head = head.next;
            if (head != null) sb.append(",");
        }
        System.out.println(sb.toString());
    }


    /**
     * 迭代
     * 执行结果：通过
     * 执行用时：1 ms, 在所有 Java 提交中击败了96.29%的用户
     * 内存消耗：39 MB MB, 在所有 Java 提交中击败了94.51%的用户
     */
    public ListNode removeElements(ListNode head, int val) {
        if (head == null) return null;
        ListNode dummyHead = new ListNode(0);
        dummyHead.next = head;
        ListNode temp = dummyHead;
        while (temp.next != null) {
            if (temp.next.val == val) {
                temp.next = temp.next.next;
            } else {
                temp = temp.next;
            }
        }
        return dummyHead.next;
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
