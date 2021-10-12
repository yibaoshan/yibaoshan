package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.LinkedList;
import java.util.Queue;

public class Medium_L2_两数相加 {

    /**
     * 给你两个 非空 的链表，表示两个非负的整数。它们每位数字都是按照 逆序 的方式存储的，并且每个节点只能存储 一位 数字。
     * <p>
     * 请你将两个数相加，并以相同形式返回一个表示和的链表。
     * <p>
     * 你可以假设除了数字 0 之外，这两个数都不会以 0 开头。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/add-two-numbers
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        ListNode l1 = new ListNode(2, new ListNode(4, new ListNode(3)));
        ListNode l2 = new ListNode(5, new ListNode(6, new ListNode(4, new ListNode(9))));
        System.out.println(l1.toString());
        System.out.println(l2.toString());
        System.out.println(addTwoNumbers(l1, l2).toString());
    }

    /**
     * 执行结果：通过
     * 执行用时：20 ms, 在所有 Java 提交中击败了5.48%的用户
     * 内存消耗：39.1 MB, 在所有 Java 提交中击败了5.13%的用户
     */
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        if (l1 == null) return l2;
        if (l2 == null) return l1;
        Queue<Integer> queue1 = nodeToQueue(l1);
        Queue<Integer> queue2 = nodeToQueue(l2);
        Queue<Integer> queueResult = new LinkedList<>();
        if (queue1.size() < queue2.size()) {
            Queue<Integer> temp = queue1;
            queue1 = queue2;
            queue2 = temp;
        }
        boolean addStep = false;
        while (!queue1.isEmpty()) {
            int once = queue1.poll();
            if (!queue2.isEmpty()) once += queue2.poll();
            if (addStep) once++;
            if (once > 9) {
                queueResult.offer(once % 10);
                addStep = true;
            } else {
                queueResult.offer(once);
                addStep = false;
            }
        }
        if (addStep) queueResult.offer(1);
        System.out.println(queue1.toString());
        System.out.println(queue2.toString());
        System.out.println(queueResult.toString());
        return queueToNode(queueResult);
    }

    private Queue<Integer> nodeToQueue(ListNode node) {
        Queue<Integer> queue = new LinkedList<>();
        ListNode temp = node;
        while (temp != null) {
            queue.offer(temp.val);
            temp = temp.next;
        }
        return queue;
    }

    private ListNode queueToNode(Queue<Integer> queue) {
        ListNode root = new ListNode(queue.poll());
        ListNode temp = root;
        while (!queue.isEmpty()) {
            temp.next = new ListNode(queue.poll());
            temp = temp.next;
        }
        return root;
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

        @Override
        public String toString() {
            return "ListNode{" +
                    "val=" + val +
                    ", next=" + next +
                    '}';
        }
    }

}
