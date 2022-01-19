package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Easy_219_存在重复元素2 {

    @Test
    public void main() {
        int[] nums = new int[]{1, 0, 1, 1};
        int k = 1;
        System.out.println(containsNearbyDuplicate(nums, k));
    }

    /**
     * 执行结果：通过
     * 执行用时：37 ms, 在所有 Java 提交中击败了22.57%的用户
     * 内存消耗：63.8 MB, 在所有 Java 提交中击败了5.10%的用户
     */
    public boolean containsNearbyDuplicate(int[] nums, int k) {
        HashMap<Integer, List<Integer>> hashMap = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            if (hashMap.containsKey(nums[i])) hashMap.get(nums[i]).add(i);
            else {
                List<Integer> list = new ArrayList<>();
                list.add(i);
                hashMap.put(nums[i], list);
            }
        }
        Iterator<Map.Entry<Integer, List<Integer>>> iterator = hashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            List<Integer> list = iterator.next().getValue();
            for (int i = 1; i < list.size(); i++) {
                if (Math.abs(list.get(i) - list.get(i - 1)) <= k) return true;
            }
        }
        return false;
    }

}
