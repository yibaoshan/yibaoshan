package com.android.algorithm.leetcode;

import org.junit.Test;

public class Medium_L38_外观数列 {

    /**
     * 给定一个正整数 n ，输出外观数列的第 n 项。
     * <p>
     * 「外观数列」是一个整数序列，从数字 1 开始，序列中的每一项都是对前一项的描述。
     * <p>
     * 你可以将其视作是由递归公式定义的数字字符串序列：
     * <p>
     * countAndSay(1) = "1"
     * countAndSay(n) 是对 countAndSay(n-1) 的描述，然后转换成另一个数字字符串。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/count-and-say
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        System.out.println(countAndSay(4));
    }

    /**
     * 执行结果：通过
     * 执行用时：1 ms, 在所有 Java 提交中击败了98.09%的用户
     * 内存消耗：36.3 MB, 在所有 Java 提交中击败了38.58%的用户
     */

    public String countAndSay(int n) {
        if (n == 1) return "1";
        String say = "1";
        while (n > 1) {
            say = describe(say);
            n--;
        }
        return say;
    }

    private String describe(String desc) {
        char[] chars = desc.toCharArray();
        int count = 1;
        char once = chars[0];
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i < chars.length; i++) {
            if (chars[i] == once) {
                count++;
            } else {
                stringBuilder.append(count).append(once);
                once = chars[i];
                count = 1;
            }
        }
        stringBuilder.append(count).append(once);
        return stringBuilder.toString();
    }

}
