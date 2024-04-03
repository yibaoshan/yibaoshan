package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class Medium_230_二叉搜索树中第K小的元素 {

    /**
     * 给定一个二叉搜索树的根节点 root ，和一个整数 k ，请你设计一个算法查找其中第 k 个最小元素（从 1 开始计数）。
     */

    @Test
    public void main() {
        TreeNode root = new TreeNode(3, new TreeNode(1, null, new TreeNode(2)), new TreeNode(4));
        System.out.println(kthSmallest(root, 3));
    }

    public int kthSmallest(TreeNode root, int k) {
        if (root == null || k <= 0) return 0;
        List<Integer> list = new LinkedList<>();
        loop(root, k, list);
        return list.get(list.size() - 1);
    }

    private void loop(TreeNode root, int k, List<Integer> list) {
        if (root == null) return;
        loop(root.left, k, list);
        if (k == list.size()) return;
        list.add(root.val);
        loop(root.right, k, list);
    }

    public int kthSmallest(TreeNode root) {
        if (root == null) return 0;
        Stack<TreeNode> stack = new Stack<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            System.out.println(node.val);
            if (node.right != null) {
                stack.push(node.right);
            }
            if (node.left != null) {
                stack.push(node.left);
            }
        }
        return 0;
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
