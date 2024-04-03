package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.PriorityQueue;

public class Medium_373_查找和最小的K对数字 {

    /**
     * 给定两个以升序排列的整数数组 nums1 和 nums2 , 以及一个整数 k 。
     * <p>
     * 定义一对值 (u,v)，其中第一个元素来自 nums1，第二个元素来自 nums2 。
     * <p>
     * 请找到和最小的 k 个数对 (u1,v1),  (u2,v2)  ...  (uk,vk) 。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/find-k-pairs-with-smallest-sums
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int[] nums1 = new int[]{-10, -4, 0, 0, 6};
        int[] nums2 = new int[]{3, 5, 6, 7, 8, 100};
        int k = 10;
        List<List<Integer>> lists = kSmallestPairs(nums1, nums2, k);
        for (int i = 0; i < lists.size(); i++) {
            System.out.println(Arrays.toString(new List[]{lists.get(i)}));
        }
    }

    /**
     * 评论区答案
     * 执行结果：通过
     * 执行用时：7 ms, 在所有 Java 提交中击败了89.85%的用户
     * 内存消耗：49.6 MB, 在所有 Java 提交中击败了61.58%的用户
     */
    public List<List<Integer>> kSmallestPairs(int[] nums1, int[] nums2, int k) {
        List<List<Integer>> res = new ArrayList<>();
        if (nums1 == null || nums2 == null || k == 0) return res;
        PriorityQueue<int[]> priorityQueue = new PriorityQueue<>((a, b) -> (nums1[a[0]] + nums2[a[1]]) - (nums1[b[0]] + nums2[b[1]]));
        for (int i = 0; i < Math.min(nums1.length, k); i++) {
            priorityQueue.add(new int[]{i, 0});
        }
        while (k > 0 && !priorityQueue.isEmpty()) {
            int[] indexs = priorityQueue.poll();
            List<Integer> list = new ArrayList<>();
            list.add(nums1[indexs[0]]);
            list.add(nums2[indexs[1]]);
            res.add(list);
            if (indexs[1] + 1 < nums2.length) {
                priorityQueue.add(new int[]{indexs[0], indexs[1] + 1});
            }
            k--;
        }
        return res;
    }

    public List<List<Integer>> kSmallestPairs2(int[] nums1, int[] nums2, int k) {
        List<List<Integer>> res = new ArrayList<>();
        if (nums1 == null || nums2 == null || k == 0) return res;
        int start1 = 0, start2 = 0;
        while (start1 < nums1.length && start2 < nums2.length) {
            if (nums1[start1] < nums2[start2]) {
                start1++;
            }
        }
        res.sort((list, t1) -> {
            if (list.get(0) + list.get(1) == t1.get(0) + t1.get(1)) return 0;
            return list.get(0) + list.get(1) < t1.get(0) + t1.get(1) ? -1 : 1;
        });
        if (res.size() <= k) return res;
        return res.subList(0, k);
    }

    private List<List<Integer>> res = new ArrayList<>();
    private Deque<Integer> deque = new ArrayDeque<>();

    private void backtrack(int[] nums1, int[] nums2, int start1, int start2, int k) {
        if (res.size() == k) return;
        if (deque.size() == 2) {
            res.add(new ArrayList<>(deque));
            return;
        }
        if (start1 >= nums1.length - 1 || start2 >= nums2.length - 1) return;
        for (int i = start1; i < nums1.length; i++) {
            deque.addLast(nums1[i]);
            for (int j = start2; j < nums2.length; j++) {
                deque.addLast(nums1[i]);
                backtrack(nums1, nums2, start1, j + 1, k);
                deque.removeLast();
            }
            deque.removeLast();
        }
    }

}
