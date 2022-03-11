package com.android.algorithm.contest;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
//        int[] nums = new int[]{53, 41, 90, 33, 84, 26, 50, 32, 63, 47, 66, 43, 29, 88, 71, 28, 83};
//        int[] nums = new int[]{5, 6};
        int k = 6;
        System.out.println(minimalKSum(nums, k));
        System.out.println(minimalKSum2(nums, k));
    }

    /**
     * 题解答案
     */
    public long minimalKSum3(int[] nums, int k) {
        Arrays.sort(nums);
        long ans = 0, start = 1;
        for (int i = 0; i < nums.length && k > 0; i++) {
            // 存在未出现的数字
            if (start < nums[i]) {
                int cnt = Math.min((int) (nums[i] - start), k);
                // 不存在的数据累计
                ans += (2L * start + cnt - 1) * cnt / 2;
                k -= cnt;
            }
            start = nums[i] + 1;
        }
        // 不存在的数据累计
        if (k > 0) {
            ans += (2L * start + k - 1) * k / 2;
        }
        return ans;
    }

    public long minimalKSum(int[] nums, int k) {
        Arrays.sort(nums);
        long res = 0L;
        if (nums[0] > 1) {
            res += sum(1, Math.min(nums[0] - 1, k));
            k -= nums[0] - 1;
            if (k <= 0) return res;
        }
        for (int i = 1; i < nums.length; i++) {
            int diff = nums[i] - nums[i - 1];
            if (diff > 1) {
                int start = nums[i - 1] + 1;
                int end = nums[i] - 1;
                if (k < diff - 1) end = start + k - 1;
                res += sum(start, end);
                k -= diff - 1;
                if (k <= 0) return res;
            }
        }
        if (k > 0) {
            int temp = nums[nums.length - 1] + 1;
            res += sum(temp, temp + k - 1);
        }
        return res;
    }

    private long sum(int m, int n) {
        return ((long) n * (n - 1) / 2 + n) - ((m - 1) * (m - 2) / 2L + m - 1);
    }

    public long minimalKSum2(int[] nums, int k) {
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
