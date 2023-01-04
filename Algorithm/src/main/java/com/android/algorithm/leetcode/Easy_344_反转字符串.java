package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;

public class Easy_344_反转字符串 {

    /**
     * 编写一个函数，其作用是将输入的字符串反转过来。输入字符串以字符数组 s 的形式给出。
     * <p>
     * 不要给另外的数组分配额外的空间，你必须原地修改输入数组、使用 O(1) 的额外空间解决这一问题。
     * <p>
     *  
     * <p>
     * 示例 1：
     * <p>
     * 输入：s = ["h","e","l","l","o"]
     * 输出：["o","l","l","e","h"]
     * 示例 2：
     * <p>
     * 输入：s = ["H","a","n","n","a","h"]
     * 输出：["h","a","n","n","a","H"]
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/reverse-string
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        char[] s = new char[]{'h', 'e', 'l', 'l', 'o'};
        System.out.println(Arrays.toString(s));
        reverseString(s);
        System.out.println(Arrays.toString(s));
    }

    /**
     * 双指针解法
     * 执行结果：通过
     * 执行用时：1 ms, 在所有 Java 提交中击败了94.4%的用户
     * 内存消耗：45.1 MB, 在所有 Java 提交中击败了24.23%的用户
     */
    public void reverseString(char[] s) {
        /**
         * 简单题，双指针暴力遍历跑一遍就完了，时间复杂度 O(n/2)
         * */
        int left = 0, right = s.length - 1;
        while (left < s.length / 2) {
            char c = s[right];
            s[right--] = s[left];
            s[left++] = c;
        }
    }

}
