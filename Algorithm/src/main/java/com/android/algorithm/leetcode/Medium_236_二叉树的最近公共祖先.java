package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class Medium_236_二叉树的最近公共祖先 {

    @Test
    public void main() {
//        TreeNode root = new TreeNode(3,
//                new TreeNode(5, new TreeNode(6), new TreeNode(2, new TreeNode(7), new TreeNode(4)))
//                , new TreeNode(1, new TreeNode(0), new TreeNode(8)));
        TreeNode root = new TreeNode(1, new TreeNode(2, null, new TreeNode(4)), new TreeNode(3));
        TreeNode p = new TreeNode(4);
        TreeNode q = new TreeNode(3);
        System.out.println(lowestCommonAncestor(root, p, q).val);
        ;
    }

    /**
     * 深度优先遍历，思路
     * 1. 深度遍历找到p和q的路径，保存到res集合
     * 2. 从两个路径中找到最短的作为下标，倒序同时遍历两条路径，找到第一个交集的返回即可
     * 执行结果：通过
     * 执行用时：7 ms, 在所有 Java 提交中击败了57.51%的用户
     * 内存消耗：40.5 MB, 在所有 Java 提交中击败了58.22%的用户
     */
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null || p == null || q == null) return null;
        List<TreeNode> list = new ArrayList<>();
        List<List<TreeNode>> res = new ArrayList<>();
        dfs(root, p, q, list, res);
        if (res.size() != 2) return null;
        int count = Math.min(res.get(0).size(), res.get(1).size()) - 1;
        while (count >= 0) {
            if (res.get(0).get(count) == res.get(1).get(count)) break;
            count--;
        }
        return res.get(0).get(count);
    }

    private void dfs(TreeNode root, TreeNode p, TreeNode q, List<TreeNode> list, List<List<TreeNode>> res) {
        if (root == null) return;
        list.add(root);
        if (root.val == p.val || root.val == q.val) {
            res.add(new ArrayList<>(list));
        }
        dfs(root.left, p, q, list, res);
        dfs(root.right, p, q, list, res);
        list.remove(list.size() - 1);
    }

    /**
     * 评论区答案，LCA
     */
    public TreeNode LowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null) {//当遍历到叶结点后就会返回null
            return root;
        }
        if (root == p || root == q) {//当找到p或者q的是时候就会返回pq
            return root;/*当然，值得一提的是，如果公共祖先是自己（pq），并不需要寻找另外
                     一个，我们在执行前序遍历会先找上面的，后找下面的，我们会直接返回公共祖先。*/
        }
        TreeNode left = LowestCommonAncestor(root.left, p, q);//返回的结点进行保存，可能是null
        TreeNode right = LowestCommonAncestor(root.right, p, q);//也可能是pq，还可能是公共祖先
        if (left != null && right != null) {
            return root;//如果左右都存在，就说明pq都出现了，这就是，公共祖先，此时不用考虑公共祖先是自己的情况，因为上面已经做过判断了。
        } else if (left != null) {//否则我们返回已经找到的那个值（存储在left，与right中），p或者q
            return left;//还有一种可能就是，由下面返回的公共祖先，并将这个值一路返回到最表层
        } else if (right != null) {
            return right;
        }
        return null;
    }

    public class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }

        public TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

}
