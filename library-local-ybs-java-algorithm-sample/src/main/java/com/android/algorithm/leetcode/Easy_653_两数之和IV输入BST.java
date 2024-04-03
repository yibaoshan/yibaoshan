package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Easy_653_两数之和IV输入BST {

    /**
     * 给定一个二叉搜索树 root 和一个目标结果 k，如果 BST 中存在两个元素且它们的和等于给定的目标结果，则返回 true。
     * <p>
     *  
     * <p>
     * 示例 1：
     * <p>
     * <p>
     * 输入: root = [5,3,6,2,4,null,7], k = 9
     * 输出: true
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/two-sum-iv-input-is-a-bst
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {

    }

    /**
     * 执行结果：通过
     * 执行用时：4 ms, 在所有 Java 提交中击败了25.40%的用户
     * 内存消耗：39.5 MB, 在所有 Java 提交中击败了44.80%的用户
     */
    public boolean findTarget(TreeNode root, int k) {
        List<Integer> list = new ArrayList<>();
        HashMap<Integer, Integer> hashMap = new HashMap();
        getAll(root, list);
        for (int i = 0; i < list.size(); i++) {
            int value = list.get(i);
            if (hashMap.containsKey(value)) return true;
            hashMap.put(k - value, value);
        }
        return false;
    }

    private void getAll(TreeNode node, List<Integer> list) {
        if (node == null) return;
        getAll(node.left, list);
        list.add(node.val);
        getAll(node.right, list);
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
