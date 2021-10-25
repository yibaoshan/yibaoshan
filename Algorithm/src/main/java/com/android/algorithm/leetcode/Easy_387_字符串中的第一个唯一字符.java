package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class Easy_387_字符串中的第一个唯一字符 {

    /**
     * 给定一个字符串，找到它的第一个不重复的字符，并返回它的索引。如果不存在，则返回 -1。
     * <p>
     *  
     * <p>
     * 示例：
     * <p>
     * s = "leetcode"
     * 返回 0
     * <p>
     * s = "loveleetcode"
     * 返回 2
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/first-unique-character-in-a-string
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        String s = "leetcode";
        System.out.println(firstUniqChar(s));
    }

    /**
     * 执行结果：通过
     * 执行用时：29 ms, 在所有 Java 提交中击败了20.39%的用户
     * 内存消耗：38.9 MB, 在所有 Java 提交中击败了58.87%的用户
     */
    public int firstUniqChar(String s) {
        LinkedHashMap<Character, Content> linkedHashMap = new LinkedHashMap<>();
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (linkedHashMap.containsKey(c)) {
                int count = linkedHashMap.get(c).count;
                linkedHashMap.put(c, new Content(++count, i));
            } else linkedHashMap.put(c, new Content(1, i));
        }
        Iterator<Map.Entry<Character, Content>> iterator = linkedHashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Content content = iterator.next().getValue();
            if (content.count == 1) return content.index;
        }
        return -1;
    }

    private class Content {
        int count = 1;
        int index;

        public Content(int count, int index) {
            this.count = count;
            this.index = index;
        }
    }

}
