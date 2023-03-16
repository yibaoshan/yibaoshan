package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;

public class Easy_455_分发饼干 {

    /**
     * 假设你是一位很棒的家长，想要给你的孩子们一些小饼干。但是，每个孩子最多只能给一块饼干。
     * <p>
     * 对每个孩子 i，都有一个胃口值 g[i]，这是能让孩子们满足胃口的饼干的最小尺寸；并且每块饼干 j，都有一个尺寸 s[j] 。如果 s[j] >= g[i]，我们可以将这个饼干 j 分配给孩子 i ，这个孩子会得到满足。你的目标是尽可能满足越多数量的孩子，并输出这个最大数值。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/assign-cookies
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */
    @Test
    public void main() {

    }

    /**
     * 执行结果：通过
     * 耗时和内存忘了保存，测试都没测试一次通过了~
     */
    public int findContentChildren(int[] g, int[] s) {
        int sum = 0;
        Arrays.sort(g);
        Arrays.sort(s);
        int cookies = s.length - 1;
        int child = g.length - 1;
        while (cookies >= 0 && child >= 0) {
            if (s[cookies] >= g[child]) {
                sum++;
                cookies--;
            }
            child--;
        }
        return sum;
    }

}
