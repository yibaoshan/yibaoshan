package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.LinkedList;
import java.util.Queue;

public class Easy_111_二叉树的最小深度 {

    /**
     * 给定一个二叉树，找出其最小深度。
     * <p>
     * 最小深度是从根节点到最近叶子节点的最短路径上的节点数量。
     * <p>
     * 说明：叶子节点是指没有子节点的节点。
     */

    @Test
    public void main() {
//        TreeNode root = new TreeNode(3, new TreeNode(9), new TreeNode(20, new TreeNode(15), new TreeNode(7)));
        TreeNode root = new TreeNode(2, null, new TreeNode(3, null, new TreeNode(4, null, new TreeNode(5, null, new TreeNode(6)))));
        System.out.println(minDepth(root));
        System.out.println(minDepth2(root));
        print(root);
    }

    public int minDepth(TreeNode root) {
        if (root == null) return 0;
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        int depth = 1;
        while (!queue.isEmpty()) {
            int cnt = queue.size();
            while (cnt-- > 0) {
                TreeNode node = queue.poll();
                if (node == null) continue;
                if (node.left == null && node.right == null) return depth;
                queue.add(node.left);
                queue.add(node.right);
            }
            if (!queue.isEmpty()) depth++;
        }
        return depth;
    }

    public int minDepth2(TreeNode root) {
        if (root == null) {
            return 0;
        }
        if (root.left == null && root.right == null) {
            return 1;
        }
        int min_depth = Integer.MAX_VALUE;
        if (root.left != null) {
            min_depth = Math.min(minDepth2(root.left), min_depth);
        }
        if (root.right != null) {
            min_depth = Math.min(minDepth2(root.right), min_depth);
        }
        return min_depth + 1;
    }

    private void print(TreeNode root) {
        Queue<TreeNode> queue = new LinkedList();
        queue.add(root);
        while (!queue.isEmpty()) {
            int cnt = queue.size();
            while (cnt-- > 0) {
                TreeNode node = queue.poll();
                if (node == null) System.out.print("null,");
                else System.out.print(node.val + ",");
                if (node == null) continue;

                queue.add(node.left);
                queue.add(node.right);
            }
            System.out.println();
        }
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
