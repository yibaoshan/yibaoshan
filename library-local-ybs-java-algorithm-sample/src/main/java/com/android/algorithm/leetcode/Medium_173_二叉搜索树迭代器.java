package com.android.algorithm.leetcode;

import org.junit.Test;

public class Medium_173_二叉搜索树迭代器 {

    /**
     * 实现一个二叉搜索树迭代器类BSTIterator ，表示一个按中序遍历二叉搜索树（BST）的迭代器：
     * BSTIterator(TreeNode root) 初始化 BSTIterator 类的一个对象。BST 的根节点 root 会作为构造函数的一部分给出。指针应初始化为一个不存在于 BST 中的数字，且该数字小于 BST 中的任何元素。
     * boolean hasNext() 如果向指针右侧遍历存在数字，则返回 true ；否则返回 false 。
     * int next()将指针向右移动，然后返回指针处的数字。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/binary-search-tree-iterator
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {

    }

    class BSTIterator {

        TreeNode list = null;

        public BSTIterator(TreeNode root) {
            parseTree(root);
        }

        private void parseTree(TreeNode node) {
            if (node.right != null) {
                parseTree(node.right);
            }
            node.right = list;
            list = node;
            if (node.left != null) {
                parseTree(node.left);
            }
        }

        public int next() {
            int val = list.val;
            list = list.right;
            return val;
        }

        public boolean hasNext() {
            return list != null;
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
