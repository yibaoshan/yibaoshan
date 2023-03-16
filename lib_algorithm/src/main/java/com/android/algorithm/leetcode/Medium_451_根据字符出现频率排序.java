package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Medium_451_根据字符出现频率排序 {

    /**
     * 给定一个字符串，请将字符串里的字符按照出现的频率降序排列。
     * <p>
     * 示例 1:
     * <p>
     * 输入:
     * "tree"
     * <p>
     * 输出:
     * "eert"
     * <p>
     * 解释:
     * 'e'出现两次，'r'和't'都只出现一次。
     * 因此'e'必须出现在'r'和't'之前。此外，"eetr"也是一个有效的答案。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/sort-characters-by-frequency
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        String s = "tree";
        System.out.println(frequencySort(s));
    }

    public String frequencySort(String s) {
        if (s == null) return s;
        char[] chars = s.toCharArray();
        HashMap<Character, Integer> hashMap = new HashMap<>();
        for (char key : chars) {
            if (hashMap.containsKey(key)) {
                int count = hashMap.get(key);
                hashMap.put(key, ++count);
            } else hashMap.put(key, 1);
        }
        Set<Map.Entry<Character, Integer>> entries = hashMap.entrySet();
        List<Map.Entry<Character, Integer>> values = new ArrayList<>(entries);
        values.sort((integerIntegerEntry, t1) -> t1.getValue().compareTo(integerIntegerEntry.getValue()));
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            int count = values.get(i).getValue();
            for (int j = 0; j < count; j++) {
                builder.append(values.get(i).getKey());
            }
        }
        return builder.toString();
    }

}
