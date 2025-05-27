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

    /**
     * 执行用时分布0ms击败100.00%
     * 消耗内存分布42.31MB击败61.03%
     * */
    public TreeNode sortedArrayToBST(int[] nums) {
        return traverse(nums, 0, nums.length);
    }

    private TreeNode traverse(int[] nums, int start, int end) {
        if (start == end) return new TreeNode(nums[end]);
        if (start > end) return null;
        int mid = start + (end - start) / 2;
        TreeNode node = new TreeNode(nums[mid]);
        //System.out.println("start = " + start + ", end = "+end + ", mid = " + mid);
        if (mid > 0) node.left = traverse(nums, start, mid - 1);
        if (mid + 1 < nums.length) node.right = traverse(nums, mid + 1, end);
        return node;
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
