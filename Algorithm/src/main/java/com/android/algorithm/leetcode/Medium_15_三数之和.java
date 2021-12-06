package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Medium_15_三数之和 {

    /**
     * 给你一个包含 n 个整数的数组 nums，判断 nums 中是否存在三个元素 a，b，c ，使得 a + b + c = 0 ？请你找出所有和为 0 且不重复的三元组。
     * <p>
     * 注意：答案中不可以包含重复的三元组。
     * <p>
     *  
     * <p>
     * 示例 1：
     * <p>
     * 输入：nums = [-1,0,1,2,-1,-4]
     * 输出：[[-1,-1,2],[-1,0,1]]
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/3sum
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
//        int[] nums = new int[]{-1, 0, 1, 2, -1, -4};
//        int[] nums = new int[]{0, 0, 0, 0};
        int[] nums = new int[]{-2, 0, 0, 2, 2};
        List<List<Integer>> lists = threeSum(nums);
        for (int i = 0; i < lists.size(); i++) {
            for (int j = 0; j < lists.get(i).size(); j++) {
                System.out.print(lists.get(i).get(j) + " ");
            }
            System.out.println();
        }
    }

    /**
     * 双指针解法，思路：
     * 1. 把数组排序
     * 2. 遍历数组，在循环体中声明左右指针，left=i+1 right=n-1
     * 2.1 判断当前值和左右指针的值，大于0，右指针左移，小于0，左指针右移
     * 2.2 等于0则左指针+1右指针-1继续循环，开始之前，判断左右指针下一个值是否相同，跳过重复值
     * 执行结果：通过
     * 执行用时：17 ms, 在所有 Java 提交中击败了99.76%的用户
     * 内存消耗：41.7 MB, 在所有 Java 提交中击败了97.94%的用户
     */
    public List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> res = new ArrayList<>();
        if (nums == null || nums.length < 3) return res;
        Arrays.sort(nums);
        for (int i = 0; i < nums.length; i++) {
            int num = nums[i];
            if (num > 0) break;//当前值大于0，后面的值相加不可能等于0
            if (i > 0 && num == nums[i - 1]) continue;//跳过重复值
            int left = i + 1;//声明左右指针
            int right = nums.length - 1;
            while (left < right) {
                int sum = num + nums[left] + nums[right];
                if (sum > 0) {
                    right--;
                } else if (sum < 0) {
                    left++;
                } else {
                    List<Integer> list = new ArrayList<>();
                    list.add(num);
                    list.add(nums[left]);
                    list.add(nums[right]);
                    res.add(list);
                    right--;
                    left++;
                    //判断左右指针的下一个值是否和其自身相同，注意下标边界值
                    while (right > 0 && right + 1 < nums.length && nums[right] == nums[right + 1]) right--;
                    while (left - 1 > 0 && left < nums.length && nums[left] == nums[left - 1]) left++;
                }
            }
        }
        return res;
    }

}
