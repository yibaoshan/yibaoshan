package com.android.algorithm.contest;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class WeeklyContest_283 {

    @Test
    public void test1() {
        String s = "K1:L2";
        System.out.println(Arrays.toString(new List[]{cellsInRange(s)}));
    }

    public List<String> cellsInRange(String s) {
        Character firstChar = s.charAt(0);
        Character firstNumber = s.charAt(1);
        Character lastChar = s.charAt(3);
        Character lastNumber = s.charAt(4);
        List<String> list = new ArrayList<>();

        for (char i = firstChar; i <= lastChar; i++) {
            for (char j = firstNumber; j <= lastNumber; j++) {
                list.add(String.valueOf(i) + j);
            }
        }
        return list;
    }

    @Test
    public void test2() {
        int[] nums = new int[]{1, 4, 25, 10, 25};
//        int[] nums = new int[]{5, 6};
        int k = 2;
        System.out.println(minimalKSum(nums, k));
    }

    public long minimalKSum(int[] nums, int k) {
        Arrays.sort(nums);
        long res = 0L;
        if (nums[0] > 1) {
            for (int i = 1; i < nums[0]; i++) {
                res += (long) i;
                k--;
                if (k == 0) return res;
            }
        }
        for (int i = 1; i < nums.length; i++) {
            int diff = nums[i] - nums[i - 1];
            if (diff > 1) {
                for (int j = nums[i - 1] + 1; j < nums[i]; j++) {
                    res += (long) j;
                    k--;
                    if (k == 0) return res;
                }
            }
        }
        if (k > 0) {
            long temp = nums[nums.length - 1] + 1L;
            while (k-- > 0) {
                res += temp;
                temp += 1L;
            }
        }
        return res;
    }

    @Test
    public void test3() {
//        int[][] des = new int[][]{
//                new int[]{20, 15, 1},
//                new int[]{20, 17, 0},
//                new int[]{50, 20, 1},
//                new int[]{50, 80, 0},
//                new int[]{80, 19, 1},
//        };
        int[][] des = new int[][]{
                new int[]{1, 2, 1},
                new int[]{2, 3, 0},
                new int[]{3, 4, 1},
        };
        TreeNode treeNode = createBinaryTree(des);
        System.out.println(treeNode.toString());
    }

    public TreeNode createBinaryTree(int[][] descriptions) {
        HashMap<Integer, TreeNode> hashMap = new HashMap<>();
        HashMap<Integer, Boolean> first = new HashMap<>();
        HashMap<Integer, Boolean> second = new HashMap<>();
        for (int i = 0; i < descriptions.length; i++) {
            int[] tree = descriptions[i];
            second.put(tree[1], false);
            TreeNode treeNode = buildTreeNode(hashMap, tree[0]);
            if (tree[2] == 1) {
                treeNode.left = buildTreeNode(hashMap, tree[1]);
            } else treeNode.right = buildTreeNode(hashMap, tree[1]);
            hashMap.put(tree[0], treeNode);
        }
        for (int i = 0; i < descriptions.length; i++) {
            int[] tree = descriptions[i];
            first.put(tree[0], second.containsKey(tree[0]));
            TreeNode treeNode = buildTreeNode(hashMap, tree[0]);
            if (tree[2] == 1) {
                treeNode.left = buildTreeNode(hashMap, tree[1]);
            } else treeNode.right = buildTreeNode(hashMap, tree[1]);
            hashMap.put(tree[0], treeNode);
        }
        Iterator<Map.Entry<Integer, Boolean>> iterator = first.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Boolean> entry = iterator.next();
            if (!entry.getValue()) return buildTreeNode(hashMap, entry.getKey());
        }
        return null;
    }

    private TreeNode buildTreeNode(HashMap<Integer, TreeNode> hashMap, int value) {
        if (hashMap.containsKey(value)) return hashMap.get(value);
        else return new TreeNode(value);
    }

    @Test
    public void test4() {
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
