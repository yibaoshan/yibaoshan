package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class Medium_763_划分字母区间 {

    /**
     * 字符串 S 由小写字母组成。我们要把这个字符串划分为尽可能多的片段，同一字母最多出现在一个片段中。返回一个表示每个字符串片段的长度的列表。
     * <p>
     *  
     * <p>
     * 示例：
     * <p>
     * 输入：S = "ababcbacadefegdehijhklij"
     * 输出：[9,7,8]
     * 解释：
     * 划分结果为 "ababcbaca", "defegde", "hijhklij"。
     * 每个字母最多出现在一个片段中。
     * 像 "ababcbacadefegde", "hijhklij" 的划分是错误的，因为划分的片段数较少。
     * <p>
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/partition-labels
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {

    }

    public List<Integer> partitionLabels(String s) {
        int array[] = new int[26];//记录每个小写字母最后出现的位置
        int len = s.length();
        List<Integer> res = new ArrayList();
        int start = 0;//片段的起始位置
        int end = 0;//片段的结束位置
        for (int i = 0; i < len; i++) {
            array[s.charAt(i) - 'a'] = i;
        }
        for (int i = 0; i < len; i++) {
            end = Math.max(end, array[s.charAt(i) - 'a']);
            if (end == i) {//说明后面的片段没有出现重复的字母了
                res.add(end - start + 1);
                start = end + 1;//新的起始位置是结束位置 + 1
            }
        }
        return res;
    }

}
