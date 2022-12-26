package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

public class Easy_349_两个数组的交集 {

    /**
     * 给定两个数组 nums1 和 nums2 ，返回 它们的交集 。输出结果中的每个元素一定是 唯一 的。我们可以 不考虑输出结果的顺序 。
     * <p>
     * 示例 1：
     * <p>
     * 输入：nums1 = [1,2,2,1], nums2 = [2,2]
     * 输出：[2]
     * 示例 2：
     * <p>
     * 输入：nums1 = [4,9,5], nums2 = [9,4,9,8,4]
     * 输出：[9,4]
     * 解释：[4,9] 也是可通过的
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode.cn/problems/intersection-of-two-arrays/description/
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     * <p>
     */

    @Test
    public void main() {
        int[] nums1 = new int[]{4, 9, 5};
        int[] nums2 = new int[]{9, 4, 9, 8, 4};
        System.out.println(Arrays.toString(intersection(nums1, nums2)));
    }

    public int[] intersection(int[] nums1, int[] nums2) {
        /**
         * 思路：利用哈希集合去重
         *
         * 遍历 nums2 时，如果有重复元素，添加到 ret
         *
         * 时间复杂度 O(m+n)
         * */
        if (nums1 == null || nums2 == null) return null;
        HashSet<Integer> total = new HashSet<>(), keep = new HashSet<>(), ret = new HashSet<>();
        for (int val : nums1) total.add(val);
        for (int val : nums2) if (keep.add(val) && !total.add(val)) ret.add(val);
        int[] arr = new int[ret.size()];
        Iterator<Integer> iterator = ret.iterator();
        for (int i = 0; iterator.hasNext(); i++) arr[i] = iterator.next();
        return arr;
    }


}
