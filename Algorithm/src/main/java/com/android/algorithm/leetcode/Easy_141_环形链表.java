package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.HashSet;

public class Easy_141_环形链表 {

    /**
     * 给定一个链表，判断链表中是否有环。
     * <p>
     * 如果链表中有某个节点，可以通过连续跟踪 next 指针再次到达，则链表中存在环。 为了表示给定链表中的环，我们使用整数 pos 来表示链表尾连接到链表中的位置（索引从 0 开始）。 如果 pos 是 -1，则在该链表中没有环。注意：pos 不作为参数进行传递，仅仅是为了标识链表的实际情况。
     * <p>
     * 如果链表中存在环，则返回 true 。 否则，返回 false 。
     * <p>
     *  
     * <p>
     * 进阶：
     * <p>
     * 你能用 O(1)（即，常量）内存解决此问题吗？
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/linked-list-cycle
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */
    @Test
    public void main() {
        ListNode listNode4 = new ListNode(4);
        ListNode listNode3 = new ListNode(3, listNode4);
        ListNode listNode2 = new ListNode(2, listNode3);
        ListNode listNode1 = new ListNode(1, listNode2);
        listNode4.next = listNode1;
        System.out.println(hasCycle2(listNode1));
    }

    /**
     * 执行结果：通过
     * 执行用时：5 ms, 在所有 Java 提交中击败了10.65%的用户
     * 内存消耗：38.7 MB, 在所有 Java 提交中击败了98.97%的用户
     */
    public boolean hasCycle(ListNode head) {
        HashSet<ListNode> hashSet = new HashSet<>();
        while (head != null) {
            if (hashSet.contains(head)) return true;
            hashSet.add(head);
            head = head.next;
        }
        return false;
    }

    /**
     * 快慢指针
     * 执行结果：通过
     * 执行用时：0 ms, 在所有 Java 提交中击败了100.00%的用户
     * 内存消耗：39.6 MB, 在所有 Java 提交中击败了31.04%的用户
     */
    public boolean hasCycle2(ListNode head) {
        if (head == null) return false;
        ListNode slow = head, fast = null;
        if (slow.next != null) fast = slow.next;
        while (slow != null) {
            if (slow == fast) return true;
            slow = slow.next;
            if (fast != null && fast.next != null) fast = fast.next.next;
            else fast = null;
        }
        return false;
    }

    class ListNode {
        int val;
        ListNode next;

        ListNode(int x) {
            val = x;
            next = null;
        }

        public ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
        }
    }

}
