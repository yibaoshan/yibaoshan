package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;

public class Medium_105_从前序与中序遍历序列构造二叉树 {

    /**
     * 给定一棵树的前序遍历 preorder 与中序遍历  inorder。请构造二叉树并返回其根节点。
     */

    @Test
    public void main() {
        int[] preorder = new int[]{5, 2, 1, 3, 4, 7, 6, 8, 9};
        int[] inorder = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
        TreeNode treeNode = buildTree(preorder, inorder);
        System.out.println(treeNode.val);
    }

    /**
     * 递归，分治法，思路：
     * 1. 前序首个元素必然为根节点，我们首先找到根节点在中序数组中的下标rootIndex
     * 2. 根据rootIndex将前序和中序数组分成两拨，rootIndex左边的为左子树，右边的为右子树，向下递归即可
     *
     * 执行结果：通过
     * 执行用时：8 ms, 在所有 Java 提交中击败了11.95%的用户
     * 内存消耗：87.1 MB, 在所有 Java 提交中击败了5.37%的用户
     */
    public TreeNode buildTree(int[] preorder, int[] inorder) {
        if (preorder == null || inorder == null || preorder.length == 0 || preorder.length != inorder.length) return null;
        int rootVal = preorder[0];
        int rootIndex = -1;
        for (int i = 0; i < inorder.length; i++) {
            if (inorder[i] == rootVal) {
                rootIndex = i;
                break;
            }
        }
        TreeNode root = new TreeNode(rootVal);
        int[] newPreLeft = Arrays.copyOfRange(preorder, 1, rootIndex + 1);
        int[] newInLeft = Arrays.copyOfRange(inorder, 0, rootIndex);
        System.out.println("start=" + rootIndex + ",newPreLeft=" + Arrays.toString(newPreLeft) + ",newInLeft=" + Arrays.toString(newInLeft));
        root.left = buildTree(newPreLeft, newInLeft);

        int[] newPreRight = Arrays.copyOfRange(preorder, rootIndex + 1, preorder.length);
        int[] newInRight = Arrays.copyOfRange(inorder, rootIndex + 1, inorder.length);
        System.out.println("start=" + rootIndex + ",newPreRight=" + Arrays.toString(newPreRight) + ",newInRight=" + Arrays.toString(newInRight));
        root.right = buildTree(newPreRight, newInRight);
        return root;
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
