package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.LinkedList;
import java.util.Queue;

public class Easy_617_合并二叉树 {

    /**
     * 给定两个二叉树，想象当你将它们中的一个覆盖到另一个上时，两个二叉树的一些节点便会重叠。
     * <p>
     * 你需要将他们合并为一个新的二叉树。合并的规则是如果两个节点重叠，那么将他们的值相加作为节点合并后的新值，否则不为 NULL 的节点将直接作为新二叉树的节点。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/merge-two-binary-trees
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        TreeNode root1 = new TreeNode(1, new TreeNode(3, new TreeNode(5), null), new TreeNode(2));
        TreeNode root2 = new TreeNode(2, new TreeNode(1, null, new TreeNode(4)), new TreeNode(3, null, new TreeNode(7)));
        TreeNode res = mergeTrees(root1, root2);
        print(res);
    }

    private void print(TreeNode node) {
        if (node == null) return;
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(node);
        StringBuilder sb = new StringBuilder();
        while (!queue.isEmpty()) {
            int length = queue.size();
            while (length-- > 0) {
                TreeNode tmp = queue.poll();
                if (tmp == null) {
                    sb.append(tmp).append(",");
                    continue;
                }
                sb.append(tmp.val).append(",");
                queue.add(tmp.left);
                queue.add(tmp.right);
            }
        }
        System.out.println(sb.toString());
    }

    public TreeNode mergeTrees(TreeNode root1, TreeNode root2) {
        if (root1 == null && root2 == null) return root1;
        if (root1 == null) root1 = root2;
        else if (root2 != null) root1.val = root1.val + root2.val;
        if (root1.left == null && root2 != null && root2.left != null) root1.left = new TreeNode(0);
        if (root1.right == null && root2 != null && root2.right != null) root1.right = new TreeNode(0);
        mergeTrees(root1.left, root2 == null ? null : root2.left);
        mergeTrees(root1.right, root2 == null ? null : root2.right);
        return root1;
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

        @Override
        public String toString() {
            return "TreeNode{" +
                    "val=" + val +
                    ", left=" + left +
                    ", right=" + right +
                    '}';
        }
    }

}
