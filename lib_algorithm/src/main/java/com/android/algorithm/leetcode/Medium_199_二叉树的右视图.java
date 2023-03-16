package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

public class Medium_199_二叉树的右视图 {

    /**
     * 给定一个二叉树的 根节点 root，想象自己站在它的右侧，按照从顶部到底部的顺序，返回从右侧所能看到的节点值。
     */

    @Test
    public void main() {
        TreeNode root = new TreeNode(1, new TreeNode(2, new TreeNode(4), null), new TreeNode(3));
//        TreeNode root = new TreeNode(1, null, new TreeNode(2, null, new TreeNode(5, new TreeNode(4), new TreeNode(6, new TreeNode(3), null))));

        List<Integer> list = rightSideView(root);
        System.out.println(Arrays.toString(list.toArray()));

        //深度遍历找出所有路线
        List<Integer> cur = new ArrayList<>();
        List<List<Integer>> res = new ArrayList<>();
        depthFirstSearch(root, cur, res, 4);
        for (int i = 0; i < res.size(); i++) {
            System.out.println(Arrays.toString(res.get(i).toArray()));
        }
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

    /**
     * 思路，层序/广度优先遍历，每层保留最后一个值即可
     * 执行结果：通过
     * 执行用时：1 ms, 在所有 Java 提交中击败了83.19%的用户
     * 内存消耗：37 MB, 在所有 Java 提交中击败了57.74%的用户
     */
    public List<Integer> rightSideView(TreeNode root) {
        List<Integer> list = new LinkedList<>();
        if (root == null) return list;
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            int count = queue.size();
            while (count-- > 0) {
                TreeNode node = queue.poll();
                if (node.left != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
                if (count == 0) list.add(node.val);//保存每一层最后一个值
            }
        }
        return list;
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
