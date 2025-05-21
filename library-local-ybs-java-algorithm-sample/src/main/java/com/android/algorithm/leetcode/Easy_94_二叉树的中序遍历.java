package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class Easy_94_二叉树的中序遍历 {

    /**
     * 给定一个二叉树的根节点 root ，返回它的 中序 遍历。
     * <p>
     *  
     * <p>
     * 示例 1：
     * <p>
     * <p>
     * 输入：root = [1,null,2,3]
     * 输出：[1,3,2]
     * 示例 2：
     * <p>
     * 输入：root = []
     * 输出：[]
     * 示例 3：
     * <p>
     * 输入：root = [1]
     * 输出：[1]
     * 示例 4：
     * <p>
     * <p>
     * 输入：root = [1,2]
     * 输出：[2,1]
     * 示例 5：
     * <p>
     * <p>
     * 输入：root = [1,null,2]
     * 输出：[1,2]
     * <p>
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/binary-tree-inorder-traversal
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        TreeNode root = new TreeNode(1, new TreeNode(4), new TreeNode(2, new TreeNode(3), null));
        System.out.println(inorderTraversal(root));
        System.out.println(stack(root));
    }

    public List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> ret = new LinkedList<>();
        traverse(root, ret);
        return ret;
    }

    private void traverse(TreeNode node, List<Integer> list){
        if(node == null)return;
        traverse(node.left, list);
        list.add(node.val);
        traverse(node.right, list);
    }

    private List<Integer> stack(TreeNode root) {
        Stack<TreeNode> stack = new Stack<>();
        List<Integer> ret = new LinkedList<>();
        while (root != null || !stack.isEmpty()) {
            // 1. 先处理左边的，将左边节点依次压栈
            while (root != null) {
                stack.push(root);
                root = root.left;
            }
            // 2. 取出栈顶，也就是树最底层左节点，取出值
            TreeNode tmp = stack.pop();
            ret.add(tmp.val);
            // 3. 接着遍历右节点
            root = tmp.right;
        }
        return ret;
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
