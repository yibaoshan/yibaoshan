package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
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
//        int[] nums = new int[]{-2, 0, 1, 1, 2};
//        int[] nums = new int[]{-1, 0, 1, 2, -1, -4, -2, -3, 3, 0, 4};
//        int[] nums = new int[]{-2, 0, 0, 2, 2};
        int[] nums = new int[]{0, 0, 0};
        List<List<Integer>> lists = threeSum(nums);
        for (int i = 0; i < lists.size(); i++) {
            System.out.println(Arrays.toString(new List[]{lists.get(i)}));
        }
        lists = threeSum4(nums);
        for (int i = 0; i < lists.size(); i++) {
            System.err.println(Arrays.toString(new List[]{lists.get(i)}));
        }
    }

    /**
     * 三数之和改成两数之和a+b+c=0 a+b=-c
     * 超时，妈的不做了
     */
    public List<List<Integer>> threeSum4(int[] nums) {
        Arrays.sort(nums);
        List<List<Integer>> res = new ArrayList<>();
        HashSet<String> hashSet = new HashSet<>();
        for (int i = 0; i < nums.length; i++) {
            List<int[]> list = find(nums, i, nums[i] * -1);
            for (int j = 0; j < list.size(); j++) {
                int[] ints = list.get(j);
                if (ints != null) {
                    List<Integer> once = new ArrayList<>();
                    once.add(nums[i]);
                    once.add(nums[ints[0]]);
                    once.add(nums[ints[1]]);
                    Collections.sort(once);
                    if (!hashSet.contains(once.toString())) res.add(once);
                    hashSet.add(once.toString());
                }
            }

        }
        return res;
    }

    private List<int[]> find(int[] nums, int cur, int val) {
        List<int[]> res = new ArrayList<>();
        HashMap<Integer, Integer> hashMap = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            if (i == cur) continue;
            hashMap.put(val - nums[i], i);
        }
        for (int i = 0; i < nums.length; i++) {
            if (i == cur) continue;
            if (hashMap.containsKey(nums[i])) {
                if (hashMap.get(nums[i]) != i) {
                    res.add(new int[]{i, hashMap.get(nums[i])});
                }
            }
        }
        return res;
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

    private List<List<Integer>> res = new ArrayList<>();
    private Deque<Integer> deque = new ArrayDeque<>();
    private HashSet<String> hashSet = new HashSet<>();
    private int sum = 0;

    /**
     * 两层遍历，超时不通过
     */
    public List<List<Integer>> threeSum2(int[] nums) {
        Arrays.sort(nums);
        List<List<Integer>> res = new ArrayList<>();
        HashSet<String> hashSet = new HashSet<>();
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] > 0) break;
            if (i > 0 && nums[i] == nums[i - 1]) {
                int once = Arrays.binarySearch(nums, -(nums[i] * 2));
                if (once == i || once == i - 1 || i < 0) continue;
                addToRes(nums, i, i, once, res, hashSet);
                continue;
            }
            for (int j = nums.length - 1; j >= 0; j--) {
                if (nums[j] < 0 || j <= i) break;
                int once = Arrays.binarySearch(nums, -(nums[i] + nums[j]));
                if (once < 0 || once == i || once == j) continue;
                addToRes(nums, i, j, once, res, hashSet);
            }
        }
        return res;
    }

    private void addToRes(int[] nums, int a, int b, int c, List<List<Integer>> res, HashSet<String> hashSet) {
        List<Integer> list = new ArrayList<>();
        list.add(nums[a]);
        list.add(nums[b]);
        list.add(nums[c]);
        Collections.sort(list);
        if (!hashSet.contains(list.toString())) res.add(list);
        hashSet.add(list.toString());
    }

    /**
     * 回溯超时不通过
     */
    public List<List<Integer>> threeSum3(int[] nums) {
        Arrays.sort(nums);
        backtrack(nums, 0);
        return res;
    }

    private void backtrack(int[] nums, int start) {
        if (deque.size() == 3) {
            if (sum == 0) {
                ArrayList<Integer> list = new ArrayList<>(deque);
                if (!hashSet.contains(list.toString())) res.add(list);
//                res.add(list);
                hashSet.add(list.toString());
            }
            return;
        }
        for (int i = start; i < nums.length; i++) {
            if (i > 0 && nums[i] == nums[i - 1] && nums[i] != 0) continue;
            deque.addLast(nums[i]);
            sum += nums[i];
            backtrack(nums, i + 1);
            sum -= nums[i];
            deque.removeLast();
        }
    }

}
