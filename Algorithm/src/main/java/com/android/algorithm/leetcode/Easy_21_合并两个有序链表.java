package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Easy_21_合并两个有序链表 {

    /**
     * 将两个升序链表合并为一个新的 升序 链表并返回。新链表是通过拼接给定的两个链表的所有节点组成的。 
     * <p>
     *  
     * <p>
     * 示例 1：
     * <p>
     * <p>
     * 输入：l1 = [1,2,4], l2 = [1,3,4]
     * 输出：[1,1,2,3,4,4]
     * 示例 2：
     * <p>
     * 输入：l1 = [], l2 = []
     * 输出：[]
     * 示例 3：
     * <p>
     * 输入：l1 = [], l2 = [0]
     * 输出：[0]
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/merge-two-sorted-lists
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        ListNode node = new ListNode(2);
        for (int i = 0; i < 4; i++) {
            node = new ListNode(new Random().nextInt(10), node);
        }

        ListNode res = null, temp = node;

        List<Integer> list = new ArrayList<>();
        while (temp != null) {
            list.add(temp.val);
            temp = temp.next;
        }

        Collections.sort(list);
        Collections.reverse(list);

        for (int v : list) {
            if (res == null) res = new ListNode(v);
            else res = new ListNode(v, res);
        }
    }

    /**
     * 双指针
     * 执行结果：通过
     * 执行用时：0 ms, 在所有 Java 提交中击败了100.00%的用户
     * 内存消耗：37.9 MB, 在所有 Java 提交中击败了37.65%的用户
     */
    public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        if (l1 == null) return l2;
        if (l2 == null) return l1;
        ListNode root = null, temp = null;
        while (l1 != null || l2 != null) {
            ListNode cur;
            if (l1 == null) {
                cur = l2;
                l2 = l2.next;
            } else if (l2 == null) {
                cur = l1;
                l1 = l1.next;
            } else if (l1.val < l2.val) {
                cur = l1;
                l1 = l1.next;
            } else {
                cur = l2;
                l2 = l2.next;
            }
            if (root == null) {
                root = new ListNode(cur.val);
                temp = root;
            } else {
                temp.next = new ListNode(cur.val);
                temp = temp.next;
            }
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
    }

}
