package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class WeeklyContest_280 {

    @Test
    public void test1() {
        int num1 = 2;
        int num2 = 3;
        System.out.println(countOperations(num1, num2));
    }

    public int countOperations(int num1, int num2) {
        int count = 0;
        while (num1 != 0 & num2 != 0) {
            if (num1 > num2) {
                num1 -= num2;
            } else {
                num2 -= num1;
            }
            count++;
        }
        return count;
    }

    @Test
    public void test2() {
        int[] nums = new int[]{3, 1, 3, 2, 4, 3};
//        int[] nums = new int[]{1, 2, 2, 2, 2};
        System.out.println(minimumOperations(nums));
    }

    public int minimumOperations(int[] nums) {
        if (nums == null || nums.length == 0) return 0;
        HashMap<Integer, Integer> hashMap = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            if (hashMap.containsKey(nums[i])) {
                hashMap.put(nums[i], hashMap.get(nums[i]) + 1);
            } else hashMap.put(nums[i], 1);
        }
        List<Map.Entry<Integer, Integer>> list = new ArrayList<>(hashMap.entrySet());
        list.sort((integerIntegerEntry, t1) -> {
            if (integerIntegerEntry.getValue() > t1.getValue()) return -1;
            else if (integerIntegerEntry.getValue() < t1.getValue()) return 1;
            else return 0;
        });
        System.out.println(Arrays.toString(new List[]{list}));
        if (list.size() > 2) {
            list = list.subList(0, 2);
        }
        System.out.println(Arrays.toString(new List[]{list}));
        int min = nums.length;
        for (int i = 0; i < list.size(); i++) {
            for (int j = i + 1; j < list.size(); j++) {
                min = Math.min(min, diff(nums, list.get(i).getKey(), list.get(j).getKey()));
            }
        }
        return min;
    }

    private int diff(int[] nums, int a, int b) {
        int cnt1 = 0, cnt2 = 0;
        for (int i = 0; i < nums.length; i++) {
            if (i % 2 == 0 && a == nums[i]) cnt1++;
            else if (i % 2 != 0 && b == nums[i]) cnt1++;
        }
        for (int i = 0; i < nums.length; i++) {
            if (i % 2 == 0 && b == nums[i]) cnt2++;
            else if (i % 2 != 0 && a == nums[i]) cnt2++;
        }
        return nums.length - Math.max(cnt1, cnt2);
    }

    @Test
    public void test3() {
        int[] beans = new int[]{4, 1, 6, 5};
//        int[] beans = new int[]{2, 10, 3, 2};
        System.out.println(minimumRemoval(beans));
        System.out.println(minimumRemoval2(beans));
    }

    public long minimumRemoval2(int[] beans) {
        Arrays.sort(beans);
        long ans = Long.MAX_VALUE, total = 0L;
        for (int bean : beans) {
            total += bean;
        }
        for (int i = 0; i < beans.length; i++) {
            long take = total - (long) beans[i] * (beans.length - i);
            ans = Math.min(ans, take);
        }
        return ans;
    }

    public long minimumRemoval(int[] beans) {
        HashSet<Integer> hashSet = new HashSet<>();
        for (int i = 0; i < beans.length; i++) {
            hashSet.add(beans[i]);
        }
        Iterator<Integer> iterator = hashSet.iterator();
        long min = Long.MAX_VALUE;
        while (iterator.hasNext()) {
            min = Math.min(min(beans, iterator.next()), min);
        }
        return min;
    }

    private long min(int[] beans, int k) {
        int cnt = 0;
        for (int i = 0; i < beans.length; i++) {
            if (beans[i] < k) cnt += beans[i];
            else cnt += beans[i] - k;
        }
        return cnt;
    }

    @Test
    public void test4() {
//        String s = "010110";
//        String s = "1100101";//5
//        String s = "01011010";//7
//        String s = "010011";//4
//        String s = "110010";//4
//        String s = "11101011";//7
        String s = "011001111111101001010000001010011";//25
        System.out.println(minimumTime(s));
    }

    public int minimumTime(String s) {
        if (s == null || s.length() == 0) return 0;
        String reverse = new StringBuffer(s).reverse().toString();
//        return Math.min(getNum(reverse), getNum(s));
        return getNum(s);
    }

    private int getNum(String s) {
        int[] dp = new int[s.length()];
        dp[0] = s.charAt(0) == '1' ? 1 : 2;

        for (int i = 1; i < s.length(); i++) {
            if (s.charAt(i) == '1') {
                boolean flag = true;
                for (int j = i - 1; j >= 0; j--) {
                    if (s.charAt(j) == '1' && dp[j] == 2) break;
                    else if (s.charAt(j) == '0') {
                        flag = false;
                        break;
                    }
                }
                out:
                if (!flag) {
                    for (int j = i + 1; j < s.length(); j++) {
                        if (s.charAt(j) == '0') break out;
                    }
                    flag = true;
                }
                if (flag || i == s.length() - 1) dp[i] = 1;
                else dp[i] = 2;
            }
        }
        int sum = 0;
        for (int i = 0; i < dp.length; i++) {
            sum += dp[i];
        }
        System.out.println(Arrays.toString(dp));
        return sum;
    }

}
