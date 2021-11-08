package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.LinkedList;
import java.util.Queue;

public class Medium_116_填充每个节点的下一个右侧节点指针 {

    /**
     * 给定一个 完美二叉树 ，其所有叶子节点都在同一层，每个父节点都有两个子节点。二叉树定义如下：
     * <p>
     * struct Node {
     * int val;
     * Node *left;
     * Node *right;
     * Node *next;
     * }
     * 填充它的每个 next 指针，让这个指针指向其下一个右侧节点。如果找不到下一个右侧节点，则将 next 指针设置为 NULL。
     * <p>
     * 初始状态下，所有 next 指针都被设置为 NULL。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/populating-next-right-pointers-in-each-node
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        Node root = new Node(0
                , new Node(1, new Node(3, null, null, null), new Node(4, null, null, null), null)
                , new Node(2, new Node(5, null, null, null), new Node(6, null, null, null), null), null);
        print(root);
    }

    private void print(Node node) {
        if (node == null) return;
        Queue<Node> stack = new LinkedList<>();
        stack.offer(node);
        while (!stack.isEmpty()) {
            int count = stack.size();
            while (count-- > 0) {
                Node temp = stack.poll();
                if (count != 0) temp.next = stack.peek();
                System.out.println(temp.val + "->" + (temp.next == null ? "" : temp.next.val));
                if (temp.left != null) stack.offer(temp.left);
                if (temp.right != null) stack.offer(temp.right);
            }
        }
    }

    /**
     * 广度优先遍历
     * 执行结果：通过
     * 执行用时：3 ms, 在所有 Java 提交中击败了35.63%的用户
     * 内存消耗：38.4 MB, 在所有 Java 提交中击败了91.23%的用户
     */
    public Node connect(Node root) {
        if (root == null) return null;
        Queue<Node> stack = new LinkedList<>();
        stack.offer(root);
        while (!stack.isEmpty()) {
            int count = stack.size();
            while (count-- > 0) {
                Node temp = stack.poll();
                if (count != 0) temp.next = stack.peek();
                if (temp.left != null) stack.offer(temp.left);
                if (temp.right != null) stack.offer(temp.right);
            }
        }
        return root;
    }

    class Node {
        public int val;
        public Node left;
        public Node right;
        public Node next;

        public Node() {
        }

        public Node(int _val) {
            val = _val;
        }

        public Node(int _val, Node _left, Node _right, Node _next) {
            val = _val;
            left = _left;
            right = _right;
            next = _next;
        }
    }

    ;

}
