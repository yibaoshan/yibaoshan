package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Hard_297_二叉树的序列化与反序列化 {

    @Test
    public void main() {
        TreeNode root = new TreeNode(1, new TreeNode(2), new TreeNode(3, null, new TreeNode(4, null, new TreeNode(5))));
        System.out.println(serialize(root));
        StringBuilder res = ser_help(root, new StringBuilder());
        System.out.println(res.toString());
    }

    public String serialize(TreeNode root) {      //用StringBuilder
        StringBuilder res = ser_help(root, new StringBuilder());
        return res.toString();
    }

    // Encodes a tree to a single string.
    public String serialize2(TreeNode root) {
        if (root == null) return null;
        StringBuilder sb = new StringBuilder();
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            int count = queue.size();
            while (count > 0) {
                TreeNode node = queue.poll();
                if (node == null) sb.append("null,");
                else sb.append(node.val).append(",");
                if (node != null) queue.offer(node.left);
                if (node != null) queue.offer(node.right);
                count--;
            }
        }
        String res = sb.toString();
        return res.substring(0, res.lastIndexOf(","));
    }

    public StringBuilder ser_help(TreeNode root, StringBuilder str){
        if(null == root){
            str.append("null,");
            return str;
        }
        str.append(root.val);
        str.append(",");
        str = ser_help(root.left, str);
        str = ser_help(root.right, str);
        return str;
    }

    public TreeNode deserialize(String data) {
        String[] str_word = data.split(",");
        List<String> list_word = new LinkedList<>(Arrays.asList(str_word));
        return deser_help(list_word);
    }

    public TreeNode deser_help(List<String> li){
        if(li.get(0).equals("null")){
            li.remove(0);
            return null;
        }
        TreeNode res = new TreeNode(Integer.parseInt(li.get(0)));
        li.remove(0);
        res.left = deser_help(li);
        res.right = deser_help(li);
        return res;
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
