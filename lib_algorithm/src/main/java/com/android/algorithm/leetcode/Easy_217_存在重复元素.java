package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.HashSet;

public class Easy_217_存在重复元素 {

    /**
     * 给定一个整数数组，判断是否存在重复元素。
     * <p>
     * 如果存在一值在数组中出现至少两次，函数返回 true 。如果数组中每个元素都不相同，则返回 false 。
     * <p>
     *  
     * <p>
     * 示例 1:
     * <p>
     * 输入: [1,2,3,1]
     * 输出: true
     * 示例 2:
     * <p>
     * 输入: [1,2,3,4]
     * 输出: false
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/contains-duplicate
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int[] nums = new int[]{1, 2, 3, 1};
        System.out.println(containsDuplicate(nums));
    }

    /**
     * 执行结果：通过
     * 执行用时：7 ms, 在所有 Java 提交中击败了30.95%的用户
     * 内存消耗：44.2 MB, 在所有 Java 提交中击败了33.62%的用户
     */
    public boolean containsDuplicate(int[] nums) {
        HashSet<Integer> hashSet = new HashSet<>();
        for (int i : nums) {
            if (hashSet.contains(i)) return true;
            hashSet.add(i);
        }
        return false;
    }

}
