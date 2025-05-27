package com.android.algorithm.leetcode;

import org.junit.Test;

public class Easy_543_二叉树的直径 {

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


    /**
     * 给你一棵二叉树的根节点，返回该树的 直径 。
     * <p>
     * 二叉树的 直径 是指树中任意两个节点之间最长路径的 长度 。这条路径可能经过也可能不经过根节点 root 。
     * <p>
     * 两节点之间路径的 长度 由它们之间边数表示。
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode.cn/problems/diameter-of-binary-tree
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
    }

    private int max = 0;


    /**
     * 执行用时分布1ms击败99.46%
     * 消耗内存分布44.17MB击败9.27%
     */
    public int diameterOfBinaryTree(TreeNode root) {
        if (root == null) return 0;
        traverse(root);
        return max;
    }

    private int traverse(TreeNode node) {
        if (node == null) return 0;
        int left = node.left == null ? 0 : traverse(node.left) + 1;
        int right = node.right == null ? 0 : traverse(node.right) + 1;
        max = Math.max(max, left + right);
        return Math.max(left, right);
    }
}
