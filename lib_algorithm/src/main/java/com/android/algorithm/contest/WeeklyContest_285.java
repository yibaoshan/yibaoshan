package com.android.algorithm.contest;

import com.android.algorithm.leetcode.LeetCodeUtil;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class WeeklyContest_285 {

    @Test
    public void test1() {
        int[] nums = new int[]{2, 4, 1, 1, 6, 5};
//        int[] nums = new int[]{6,6,5,5,4,1};
//        int[] nums = new int[]{8, 2, 5, 7, 7, 2, 10, 3, 6, 2}; //6
        System.out.println(countHillValley(nums));
    }

    public int countHillValley(int[] nums) {
        if (nums == null || nums.length < 2) return 0;
        List<Integer> list = new ArrayList<>();
        list.add(nums[0]);
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] == nums[i - 1]) continue;
            list.add(nums[i]);
        }
        nums = new int[list.size()];
//        for (int i = 0; i < nums.length; i++) nums[i] = list.get(i);
//        HashSet<String> hashSet = new HashSet<>();
//        for (int i = 1; i < nums.length - 1; i++) {
//            int[] hill = isHill(nums, i);
//            int[] valley = isValley(nums, i);
//            if (hill != null) hashSet.add(Arrays.toString(hill));
//            if (valley != null) hashSet.add(Arrays.toString(valley));
//        }
        int cnt = 0;
        for (int i = 1; i < list.size() - 1; i++) {
            if (list.get(i - 1) > list.get(i) && list.get(i + 1) > list.get(i) || list.get(i - 1) < list.get(i) && list.get(i + 1) < list.get(i)) cnt++;
        }
//        return hashSet.size();
        return cnt;
    }

    private int[] isHill(int[] nums, int index) {
        int left = index - 1, right = index + 1;
        while (left >= 0 && right < nums.length) {
            if (nums[left] < nums[index] && nums[right] < nums[index]) {
                System.out.println("isHill" + nums[index]);
                return new int[]{left, right};
            }
            if (nums[left] > nums[index]) return null;
            else left--;
            if (nums[right] > nums[index]) return null;
            else right++;
        }
        return null;
    }

    private int[] isValley(int[] nums, int index) {
        int left = index - 1, right = index + 1;
        while (left >= 0 && right < nums.length) {
            if (nums[left] > nums[index] && nums[right] > nums[index]) {
                System.out.println("isValley" + nums[index]);
                return new int[]{left, right};
            }
            if (nums[left] <= nums[index]) left--;
            if (nums[right] <= nums[index]) right++;
        }
        return null;
    }


    @Test
    public void test2() {
        String s = "SSRSSRLLRSLLRSRSSRLRRRRLLRRLSSRR";//20
//        String s = "RLRSLL";//5
//        String s = "LLRR";
        System.out.println(countCollisions(s));
    }

    public int countCollisions(String directions) {
        Boolean last = getDirection(directions.charAt(0));
        int cnt = 0;
        char[] chars = directions.toCharArray();
        for (int i = 1; i < chars.length; i++) {
            Boolean cur = getDirection(chars[i]);
            if (cur == null) {
                if (last != null && !last) {
                    cnt++;//当前是停留，上一个是右转，发生碰撞
                    int temp = i - 2;
                    while (temp >= 0) {
                        Boolean direction = getDirection(chars[temp]);
                        if (direction != null && !direction) {
                            cnt++;
                        } else break;
                        temp--;
                    }
                }
            } else if (cur) {//当前是左转
                if (last == null) {//上一个是停留，发生碰撞，并将当前改为停留
                    chars[i] = 'S';
                    cnt++;
                } else if (!last) {//上一个是右转
                    chars[i] = 'S';
                    cnt += 2;
                }
            }
            last = getDirection(chars[i]);
        }
        return cnt;
    }

    private Boolean getDirection(char c) {
        if (c == 'L') return true;
        if (c == 'R') return false;
        return null;
    }

    @Test
    public void test3() {
        int[] nums = new int[]{91, 98, 17, 79, 15, 55, 47, 86, 4, 5, 17, 79, 68, 60, 60, 31, 72, 85, 25, 77, 8, 78, 40, 96, 76, 69, 95, 2, 42, 87, 48, 72, 45, 25, 40, 60, 21, 91, 32, 79, 2, 87, 80, 97, 82, 94, 69, 43, 18, 19, 21, 36, 44, 81, 99};
        int k = 1;
//        [2,28,6,4,3]
//        1000000000
        System.out.println(maximumTop(nums, k));
    }

    public int maximumTop(int[] nums, int k) {
        if (nums == null || nums.length == 0) return -1;
        if (nums.length == 1 && k % 2 == 1) return -1;
        if (k == 1) return nums.length > 1 ? nums[1] : -1;
        if (k == 2 && nums.length > 2) return Math.max(nums[0], nums[2]);
        int[] range = Arrays.copyOfRange(nums, 0, Math.min(nums.length, k));
        Arrays.sort(range);
        if (k >= nums.length) return range[range.length - 1];
        return Math.max(range[range.length - 1], nums[range.length]);
    }

    @Test
    public void test4() {
    }

}
