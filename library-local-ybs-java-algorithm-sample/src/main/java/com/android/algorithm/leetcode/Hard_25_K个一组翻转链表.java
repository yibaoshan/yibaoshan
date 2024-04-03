package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayList;

public class Hard_25_K个一组翻转链表 {

    /**
     * 给你一个链表，每 k 个节点一组进行翻转，请你返回翻转后的链表。
     * <p>
     * k 是一个正整数，它的值小于或等于链表的长度。
     * <p>
     * 如果节点总数不是 k 的整数倍，那么请将最后剩余的节点保持原有顺序。
     * <p>
     * <p>
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/reverse-nodes-in-k-group
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        ListNode node = new ListNode(1, new ListNode(2, new ListNode(3, new ListNode(4, new ListNode(5)))));
        print(node);
        ListNode temp = reverseKGroup(node, 2);
        print(temp);
    }

    /**
     * 评论区思路，可以参考一下，应该比我的快很多~
     * **思路一:**
     *
     * 用栈,我们把k个数压入栈中,然后弹出来的顺序就是翻转的!
     *
     * 这里要注意几个问题
     *
     * 第一,剩下的链表个数够不够k个(因为不够k个不用翻转);
     *
     * 第二,已经翻转的部分要与剩下链表连接起来
     *
     * **思路二:**
     *
     * 尾插法
     *
     * 直接举个例子: k = 3
     *
     * pre
     * tail    head
     * dummy    1     2     3     4     5
     * # 我们用tail 移到要翻转的部分最后一个元素
     * pre     head       tail
     * dummy    1     2     3     4     5
     * 		cur
     * # 我们尾插法的意思就是,依次把cur移到tail后面
     * pre          tail  head
     * dummy    2     3    1     4     5
     * 		cur
     * # 依次类推
     * pre     tail      head
     * dummy    3     2    1     4     5
     * 		cur
     * ....
     *
     * */

    /**
     * 思路：
     * 1. 遍历链表，新建List集合保存当前节点
     * 2. 判断集合长度是否为K，是的话将List集合中所有元素翻转
     * 3. 最后检查集合是否为空，不为空说明有值但长度不满足k，按题意，将集合中元素依次添加到新链表末尾即可
     *
     * 用栈应该简单一点
     *
     * <p>
     * 执行结果：通过
     * 执行用时：2 ms, 在所有 Java 提交中击败了7.60%的用户
     * 内存消耗：38.9 MB, 在所有 Java 提交中击败了5.01%的用户
     */
    public ListNode reverseKGroup(ListNode head, int k) {
        ListNode temp = head, last = null;
        ArrayList<ListNode> listNodes = new ArrayList<>();
        while (temp != null) {
            listNodes.add(new ListNode(temp.val));
            if (listNodes.size() == k) {
                ListNode[] once = swap(listNodes);
                if (last == null) head = once[0];
                else last.next = once[0];
                last = once[1];
                listNodes.clear();
            }
            temp = temp.next;
        }
        if (last != null) {
            for (int i = 0; i < listNodes.size(); i++) {
                last.next = listNodes.get(i);
                last = last.next;
            }
        }
        return head;
    }

    private ListNode[] swap(ArrayList<ListNode> listNodes) {
        int n = listNodes.size();
        ListNode head = listNodes.get(n - 1);
        ListNode temp = head;
        for (int i = n - 2; i >= 0; i--) {
            temp.next = listNodes.get(i);
            temp = temp.next;
        }
        temp.next = null;
        return new ListNode[]{head, temp};
    }

    private void print(ListNode node) {
        ListNode temp = node;
        while (temp != null) {
            System.err.print(temp.val + ",");
            temp = temp.next;
        }
        System.out.println();
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
