package com.android.algorithm.leetcode;

import org.junit.Test;

public class Easy_572_另一棵树的子树 {

    /**
     * 给你两棵二叉树 root 和 subRoot 。检验 root 中是否包含和 subRoot 具有相同结构和节点值的子树。如果存在，返回 true ；否则，返回 false 。
     * <p>
     * 二叉树 tree 的一棵子树包括 tree 的某个节点和这个节点的所有后代节点。tree 也可以看做它自身的一棵子树。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/subtree-of-another-tree
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {

    }

    /**
     * 这套题我们可以先简化为第100道题，是否相同的树：https://leetcode-cn.com/problems/same-tree/
     * 那么我们的问题就演变成如何判断两个树是否相同？
     * 可以使用深度/广度优先遍历二叉树，比较当前节点的值，相同就继续向下遍历，不相同返回结果false
     * 笔者这里使用深度优先中的前序遍历，对比步骤：
     *
     * 1. 若两个节点都为null，即表示相同，返回true
     * 2. 两个节点有一个为null，那树结构肯定不相同，返回false
     * 3. 对比两个节点中的值是否相同
     * 3.1 不相同返回false
     * 3.2 相同向下遍历该节点的左右子节点是否相同
     *
     * 执行结果：通过
     * 执行用时：2 ms, 在所有 Java 提交中击败了95.78%的用户
     * 内存消耗：38.1 MB, 在所有 Java 提交中击败了96.66%的用户
     */
    public boolean isSubtree(TreeNode root, TreeNode subRoot) {
        if (subRoot == null) return true;//子树为空的情况下必定为true
        if (root == null) return false;//root为空就没法继续了，返回false
        return isSameTree(root, subRoot) || isSubtree(root.left, subRoot) || isSubtree(root.right, subRoot);
    }

    public boolean isSameTree(TreeNode root, TreeNode sub) {
        if (root == null && sub == null) return true;//树结构相同，返回true
        if (root == null || sub == null) return false;//树结构不同，返回false
        if (root.val != sub.val) return false;//值不同，返回false
        return isSameTree(root.left, sub.left) && isSameTree(root.right, sub.right);//值相同，判断当前节点的左右节点是否也相同
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
