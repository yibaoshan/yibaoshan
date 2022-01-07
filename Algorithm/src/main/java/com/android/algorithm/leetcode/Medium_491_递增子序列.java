package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class Medium_491_递增子序列 {

    /**
     * 给你一个整数数组 nums ，找出并返回所有该数组中不同的递增子序列，递增子序列中 至少有两个元素 。你可以按 任意顺序 返回答案。
     * <p>
     * 数组中可能含有重复元素，如出现两个整数相等，也可以视作递增序列的一种特殊情况。
     * <p>
     * <p>
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/increasing-subsequences
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
//        int[] nums = new int[]{4, 6, 7, 7};
//        int[] nums = new int[]{1,2,3,4,5,6,7,8,9,10,1,1,1,1,1};
//        int[] nums = new int[]{1, 3, 2, 2};
        int[] nums = new int[]{1, 2, 3, 1, 1, 1, 1};
        List<List<Integer>> lists = findSubsequences(nums);
        System.out.println(lists.size());
        for (List<Integer> list : lists) System.out.println(Arrays.toString(new List[]{list}));
    }

    /**
     * 执行结果：通过
     * 执行用时：39 ms, 在所有 Java 提交中击败了5.11%的用户
     * 内存消耗：46.9 MB, 在所有 Java 提交中击败了9.00%的用户
     */
    public List<List<Integer>> findSubsequences(int[] nums) {
        if (nums == null || nums.length < 2) return res;
        backtrack(nums, 0);
        return res;
    }

    private List<List<Integer>> res = new LinkedList<>();
    private Deque<Integer> deque = new ArrayDeque<>();
    private HashSet<String> hashSet = new HashSet<>();

    private void backtrack(int[] nums, int start) {
        if (deque.size() > nums.length) return;
        if (deque.size() > 1 && !hashSet.contains(deque.toString())) {
            hashSet.add(deque.toString());
            res.add(new ArrayList<>(deque));
        }
        for (int i = start; i < nums.length; i++) {
            if (i > start && nums[i] == nums[i - 1]) continue;
            if (!deque.isEmpty() && nums[i] < deque.peekLast()) continue;
            deque.addLast(nums[i]);
            backtrack(nums, i + 1);
            deque.removeLast();
        }
    }

}
