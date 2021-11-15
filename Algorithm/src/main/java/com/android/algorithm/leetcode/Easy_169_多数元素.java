package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.HashMap;

public class Easy_169_多数元素 {

    /**
     * 给定一个大小为 n 的数组，找到其中的多数元素。多数元素是指在数组中出现次数 大于 ⌊ n/2 ⌋ 的元素。
     * <p>
     * 你可以假设数组是非空的，并且给定的数组总是存在多数元素。
     * <p>
     *  
     * <p>
     * 示例 1：
     * <p>
     * 输入：[3,2,3]
     * 输出：3
     * <p>
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/majority-element
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {

    }

    /**
     * 执行结果：通过
     * 执行用时：13 ms, 在所有 Java 提交中击败了21.46%的用户
     * 内存消耗：43.7 MB, 在所有 Java 提交中击败了80.86%的用户
     */
    public int majorityElement(int[] nums) {
        if (nums == null) return 0;
        HashMap<Integer, Integer> hashMap = new HashMap<>();
        int maxCount = 0, maxValue = 0;
        for (int i = 0; i < nums.length; i++) {
            int num = nums[i];
            int count = 1;
            if (hashMap.containsKey(num)) {
                count = hashMap.get(num);
                hashMap.put(nums[i], ++count);
            } else hashMap.put(num, 1);
            if (count > maxCount) {
                maxCount = count;
                maxValue = num;
            }
        }
        return maxValue;
    }

}
