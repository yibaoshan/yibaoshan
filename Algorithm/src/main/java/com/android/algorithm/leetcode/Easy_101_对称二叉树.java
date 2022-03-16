package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

public class Easy_101_对称二叉树 {

    /**
     * 给定一个二叉树，检查它是否是镜像对称的。
     * <p>
     *  
     * <p>
     * 例如，二叉树 [1,2,2,3,4,4,3] 是对称的。
     * <p>
     * 1
     * / \
     * 2   2
     * / \ / \
     * 3  4 4  3
     *  
     * <p>
     * 但是下面这个 [1,2,2,null,3,null,3] 则不是镜像对称的:
     * <p>
     * 1
     * / \
     * 2   2
     * \   \
     * 3    3
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/symmetric-tree
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        TreeNode node1 = new TreeNode(1);
        TreeNode node2 = new TreeNode(2);
        TreeNode node3 = new TreeNode(2);
        node1.left = node2;
        node1.right = node3;
        node2.left = new TreeNode(3);
        node3.right = new TreeNode(2);
        System.out.println(isSymmetric(node1));
        System.out.println(isSymmetric2(node1));
    }

    /**
     * 执行结果：通过
     * 执行用时：0 ms, 在所有 Java 提交中击败了100.00%的用户
     * 内存消耗：36.2 MB, 在所有 Java 提交中击败了94.60%的用户
     */
    public boolean isSymmetric(TreeNode root) {
        return compare(root, root);
    }

    private boolean compare(TreeNode left, TreeNode right) {
        if (left == null && right == null) return true;
        if (left == null || right == null) return false;
        if (left.val != right.val) return false;
        return compare(left.left, right.right) && compare(left.right, right.left);
    }

    /**
     * 思路，层序遍历二叉树，每次比较头一个和最后一个是否相同
     * 执行结果：通过
     * 执行用时：1 ms, 在所有 Java 提交中击败了23.25%的用户
     * 内存消耗：41.3 MB, 在所有 Java 提交中击败了5.02%的用户
     */
    public boolean isSymmetric2(TreeNode root) {
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            int cnt = queue.size();
            if (queue.peek() != root) {
                List<TreeNode> list = new ArrayList<>(queue);
                for (int i = 0; i < list.size(); i++) {
                    if (i >= list.size() / 2) break;
                    TreeNode first = list.get(i);
                    TreeNode last = list.get(list.size() - 1 - i);
                    if (first == null && last == null) continue;
                    if (first == null || last == null) return false;
                    if (first.val != last.val) return false;
                }
            }
            while (cnt-- > 0) {
                TreeNode node = queue.poll();
                if (node == null) continue;
                queue.add(node.left);
                queue.add(node.right);
            }
        }
        return true;
    }

    public class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode() {
        }

        TreeNode(int val) {
            this.val = val;
        }

        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

}
