package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class Medium_113_路径总和2 {

    /**
     * 给你二叉树的根节点 root 和一个整数目标和 targetSum ，找出所有 从根节点到叶子节点 路径总和等于给定目标和的路径。
     * <p>
     * 叶子节点 是指没有子节点的节点。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/path-sum-ii
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {

    }

    /**
     * 思路，深度优先遍历，每次记录访问路径，符合条件的路径保存到结果集合
     * 执行结果：通过
     * 执行用时：1 ms, 在所有 Java 提交中击败了100.00%的用户
     * 内存消耗：38.8 MB, 在所有 Java 提交中击败了41.23%的用户
     */
    public List<List<Integer>> pathSum(TreeNode root, int targetSum) {
        List<Integer> cur = new ArrayList<>();
        List<List<Integer>> res = new ArrayList<>();
        depthFirstSearch(root, cur, res, targetSum);
        return res;
    }

    private void depthFirstSearch(TreeNode root, List<Integer> cur, List<List<Integer>> res, int target) {
        if (root == null) return;
        if (target < 0) {
            cur.remove(cur.size() - 1);
            return;
        }
        cur.add(root.val);
        if (root.left == null && root.right == null && root.val == target) {
            res.add(new ArrayList<>(cur));
        }
        depthFirstSearch(root.left, cur, res, target - root.val);
        depthFirstSearch(root.right, cur, res, target - root.val);
        cur.remove(cur.size() - 1);
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
