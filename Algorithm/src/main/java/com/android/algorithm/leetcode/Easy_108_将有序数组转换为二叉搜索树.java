package com.android.algorithm.leetcode;

import org.junit.Test;

public class Easy_108_将有序数组转换为二叉搜索树 {

    /**
     * 给你一个整数数组 nums ，其中元素已经按 升序 排列，请你将其转换为一棵 高度平衡 二叉搜索树。
     * <p>
     * 高度平衡 二叉树是一棵满足「每个节点的左右两个子树的高度差的绝对值不超过 1 」的二叉树。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/convert-sorted-array-to-binary-search-tree
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int[] nums = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
//        int[] nums = new int[]{-10, -3, 0, 5, 9};
        TreeNode build = sortedArrayToBST(nums);
        print(build);
    }

    public TreeNode sortedArrayToBST(int[] nums) {
        if (nums == null || nums.length == 0) return null;
        return build(nums, 0, nums.length - 1);
    }

    /**
     * 递归，分治法
     * 执行结果：通过
     * 执行用时：0 ms, 在所有 Java 提交中击败了100.00%的用户
     * 内存消耗：38 MB, 在所有 Java 提交中击败了87.40%的用户
     */
    private TreeNode build(int[] nums, int left, int right) {
        if (left > right) return null;
        int m = (left + right) / 2;
        TreeNode treeNode = new TreeNode(nums[m]);
        treeNode.left = build(nums, left, m - 1);
        treeNode.right = build(nums, m + 1, right);
        return treeNode;
    }

    private void print(TreeNode root) {
        if (root == null) return;
        System.out.println(root.val);
        print(root.left);
        print(root.right);
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
