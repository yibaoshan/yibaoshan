package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Easy_5980_将字符串拆分为若干长度为k的组 {

    /**
     * 字符串 s 可以按下述步骤划分为若干长度为 k 的组：
     * <p>
     * 第一组由字符串中的前 k 个字符组成，第二组由接下来的 k 个字符串组成，依此类推。每个字符都能够成为 某一个 组的一部分。
     * 对于最后一组，如果字符串剩下的字符 不足 k 个，需使用字符 fill 来补全这一组字符。
     * 注意，在去除最后一个组的填充字符 fill（如果存在的话）并按顺序连接所有的组后，所得到的字符串应该是 s 。
     * <p>
     * 给你一个字符串 s ，以及每组的长度 k 和一个用于填充的字符 fill ，按上述步骤处理之后，返回一个字符串数组，该数组表示 s 分组后 每个组的组成情况 。
     */

    @Test
    public void main() {
        String s = "abcdefghi";
        int k = 3;
        char fill = 'x';
        System.out.println(Arrays.toString(divideString(s, k, fill)));
    }

    public String[] divideString(String s, int k, char fill) {
        if (s == null || s.length() == 0) return null;
        StringBuilder sb = new StringBuilder(s);
        int diff = s.length() % k;
        if (diff != 0) {
            for (int i = 0; i < k - diff; i++) {
                sb.append(fill);
            }
        }
        s = sb.toString();
        List<String> list = new ArrayList<>();
        for (int i = 0; i < s.length(); i += k) {
            list.add(s.substring(i, i + k));
        }
        return list.toArray(new String[list.size()]);
    }

}
