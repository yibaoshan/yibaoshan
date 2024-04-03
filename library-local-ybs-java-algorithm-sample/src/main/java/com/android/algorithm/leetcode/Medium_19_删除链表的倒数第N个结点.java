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
        ListNode head = new ListNode(1, new ListNode(2, new ListNode(3, new ListNode(4))));
        ListNode result = removeNthFromEnd3(head, 4);
        while (result != null) {
            System.out.println(result.val);
            result = result.next;
        }
    }

    public ListNode removeNthFromEnd3(ListNode head, int n) {
        if (n < 1 || head == null) return head;
        /**
         * 经典快慢指针
         *
         * 1. 新建头节点 dummy ，next 指向 head （所有删除节点的都建议使用虚拟头节点）
         * 2. 新建慢指针 slow，自身指向虚拟头节点
         * 3. 新建快节点 fast，自身指向头节点，用于遍历链表
         *
         * 使用 fast 遍历链表，当快慢指针间隔差满足 n ，并且链表已经遍历到底
         *
         * slow 的 next 指针，指向的就是要删除的节点
         * */
        ListNode dummy = new ListNode(0, head), slow = dummy, fast = head;
        while (fast != null) {
            if (n-- <= 0) slow = slow.next; // 快慢指针间隔满足 n ，慢指针也要向下走
            fast = fast.next; // 快指针正常遍历，向下走
            if (fast == null) slow.next = slow.next.next; // 链表走到底了，删除 slow 指向的节点即可
        }
        return dummy.next;
    }

    public ListNode removeNthFromEnd(ListNode head, int n) {
        /**
         * 传统写法，思路：双指针 + 两轮遍历，左指针和右指针的间距为 n
         *
         * 第一轮遍历，找到倒数第n个节点，也就是左指针指向的节点
         * 第二轮遍历，新建节点赋值，除了倒数第N个节点外，其他节点依次添加到新节点的尾部
         *
         * 时间复杂度 O(2n) ，空间复杂度 O(n)
         * */
        if (n < 1 || head == null) return head;
        ListNode tmp = head, left = head, right = null, ret = new ListNode();
        while (tmp != null) {
            if (n == 1) right = tmp;
            if (n-- < 1) {
                left = left.next;
                right = right.next;
            }
            tmp = tmp.next;
        }
        tmp = ret;
        while (head != null) {
            if (head != left) {
                tmp.next = new ListNode(head.val);
                tmp = tmp.next;
            }
            head = head.next;
        }
        return ret.next;
    }


    private String print(ListNode head) {
        StringBuilder stringBuilder = new StringBuilder();
        while (head != null) {
            stringBuilder.append(head.val);
            head = head.next;
            if (head != null) stringBuilder.append(",");
        }
        return stringBuilder.toString();
    }


    /**
     * 思路：创建两个节点，假节点和目标节点
     * 1. 假节点的next指向传进来的节点
     * 2. 目标节点默认指向假节点
     * 3. 开始遍历入参节点，并计数
     * 4. 当计数值大于要求倒数的数字就就将目标节点向后移一位
     * 执行结果：通过
     * 执行用时：0 ms, 在所有 Java 提交中击败了100.00%的用户
     * 内存消耗：39.4 MB, 在所有 Java 提交中击败了34.43%的用户
     */
    public ListNode removeNthFromEnd2(ListNode head, int n) {
        if (n < 1 || head == null) return head;
        ListNode dummy = new ListNode(0, head);
        ListNode temp = dummy.next;
        ListNode target = dummy;
        int cnt = 1;
        while (temp != null) {
            if (cnt++ > n) target = target.next;
            temp = temp.next;
        }
        target.next = target.next.next;
        return dummy.next;
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
