package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

public class Hard_1220_统计元音字母序列的数目 {

    /**
     * 给你一个整数 n，请你帮忙统计一下我们可以按下述规则形成多少个长度为 n 的字符串：
     * <p>
     * 字符串中的每个字符都应当是小写元音字母（'a', 'e', 'i', 'o', 'u'）
     * 每个元音 'a' 后面都只能跟着 'e'
     * 每个元音 'e' 后面只能跟着 'a' 或者是 'i'
     * 每个元音 'i' 后面 不能 再跟着另一个 'i'
     * 每个元音 'o' 后面只能跟着 'i' 或者是 'u'
     * 每个元音 'u' 后面只能跟着 'a'
     * 由于答案可能会很大，所以请你返回 模 10^9 + 7 之后的结果。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/count-vowels-permutation
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int n = 5;
        System.out.println(countVowelPermutation(n));
        System.out.println(countVowelPermutation2(n));
        for (int i = 0; i < res.size(); i++) {
            System.out.println(Arrays.toString(new List[]{res.get(i)}));
        }
    }

    /**
     * 动态规划，评论区答案
     * 执行结果：通过
     * 执行用时：15 ms, 在所有 Java 提交中击败了20.75%的用户
     * 内存消耗：38.5 MB, 在所有 Java 提交中击败了25.47%的用户
     */
    public int countVowelPermutation(int n) {
        int mod = (int) 1e9 + 7;
        long[][] dp = new long[n][5];
        Arrays.fill(dp[0], 1L);
        for (int i = 1; i < n; i++) {
            dp[i][0] = (dp[i - 1][1] + dp[i - 1][2] + dp[i - 1][4]) % mod;
            dp[i][1] = (dp[i - 1][0] + dp[i - 1][2]) % mod;
            dp[i][2] = (dp[i - 1][1] + dp[i - 1][3]) % mod;
            dp[i][3] = dp[i - 1][2] % mod;
            dp[i][4] = (dp[i - 1][2] + dp[i - 1][3]) % mod;
        }
        long res = 0;
        for (int i = 0; i < 5; i++)
            res = (res + dp[n - 1][i]) % mod;
        return (int) res;
    }

    /**
     * 回溯法，自己写的
     * 执行结果：不通过，超时
     */
    public int countVowelPermutation2(int n) {
        char[] chars = new char[5];
        chars[0] = 'a';
        chars[1] = 'e';
        chars[2] = 'i';
        chars[3] = 'o';
        chars[4] = 'u';
        backtrack(chars, n);
        return cnt;
    }

    private int cnt = 0;
    private final List<List<Character>> res = new ArrayList<>();
    private final Deque<Character> deque = new ArrayDeque<>();

    private void backtrack(char[] chars, int n) {
        if (deque.size() >= n) {
            cnt++;
            res.add(new ArrayList<>(deque));
            return;
        }
        for (int i = 0; i < chars.length; i++) {
            if (!deque.isEmpty() && deque.peekLast() == 'a' && chars[i] != 'e') continue;
            if (!deque.isEmpty() && deque.peekLast() == 'e') {
                if (chars[i] != 'a' && chars[i] != 'i') continue;
            }
            if (!deque.isEmpty() && deque.peekLast() == 'i' && chars[i] == 'i') continue;
            if (!deque.isEmpty() && deque.peekLast() == 'o') {
                if (chars[i] != 'i' && chars[i] != 'u') continue;
            }
            if (!deque.isEmpty() && deque.peekLast() == 'u' && chars[i] != 'a') continue;
            deque.addLast(chars[i]);
            backtrack(chars, n);
            deque.removeLast();
        }
    }

}
