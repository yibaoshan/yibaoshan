package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Medium_347_前K个高频元素 {

    /**
     * 给你一个整数数组 nums 和一个整数 k ，请你返回其中出现频率前 k 高的元素。你可以按 任意顺序 返回答案。
     */

    @Test
    public void main() {
        int[] nums = new int[]{1, 1, 1, 2, 2, 3, 3, 3, 3};
        ;
        System.out.println(Arrays.toString(topKFrequent(nums, 4)));
    }

    /**
     * 执行结果：通过
     * 执行用时：16 ms, 在所有 Java 提交中击败了16.89%的用户
     * 内存消耗：41.1 MB, 在所有 Java 提交中击败了36.33%的用户
     */
    public int[] topKFrequent(int[] nums, int k) {
        if (nums == null) return nums;
        HashMap<Integer, Integer> hashMap = new HashMap<>();
        for (int key : nums) {
            if (hashMap.containsKey(key)) {
                int count = hashMap.get(key);
                hashMap.put(key, ++count);
            } else hashMap.put(key, 1);
        }
        Set<Map.Entry<Integer, Integer>> entries = hashMap.entrySet();
        List<Map.Entry<Integer, Integer>> values = new ArrayList<>(entries);
        if (values.size()<k)return null;
        values.sort((integerIntegerEntry, t1) -> t1.getValue().compareTo(integerIntegerEntry.getValue()));
        int[] res = new int[k];
        for (int i = 0; i < k; i++) {
            res[i] = values.get(i).getKey();
        }
       return res;
    }

}
