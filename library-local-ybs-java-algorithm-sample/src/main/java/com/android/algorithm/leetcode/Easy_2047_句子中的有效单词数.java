package com.android.algorithm.leetcode;

import org.junit.Test;

public class Easy_2047_句子中的有效单词数 {

    /**
     * 句子仅由小写字母（'a' 到 'z'）、数字（'0' 到 '9'）、连字符（'-'）、标点符号（'!'、'.' 和 ','）以及空格（' '）组成。每个句子可以根据空格分解成 一个或者多个 token ，这些 token 之间由一个或者多个空格 ' ' 分隔。
     * <p>
     * 如果一个 token 同时满足下述条件，则认为这个 token 是一个有效单词：
     * <p>
     * 仅由小写字母、连字符和/或标点（不含数字）。
     * 至多一个 连字符 '-' 。如果存在，连字符两侧应当都存在小写字母（"a-b" 是一个有效单词，但 "-ab" 和 "ab-" 不是有效单词）。
     * 至多一个 标点符号。如果存在，标点符号应当位于 token 的 末尾 。
     * 这里给出几个有效单词的例子："a-b."、"afad"、"ba-c"、"a!" 和 "!" 。
     * <p>
     * 给你一个字符串 sentence ，请你找出并返回 sentence 中 有效单词的数目 。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/number-of-valid-words-in-a-sentence
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
//        String s = "!this  1-s b8d!";
//        String s = "alice and  bob are playing stone-game10";
//        String s = "he bought 2 pencils, 3 erasers, and 1  pencil-sharpener.";
//        String s = "cat and  dog";
        String s = " 62   nvtk0wr4f  8 qt3r! w1ph 1l ,e0d 0n 2v 7c.  n06huu2n9 s9   ui4 nsr!d7olr  q-, vqdo!btpmtmui.bb83lf g .!v9-lg 2fyoykex uy5a 8v whvu8 .y sc5 -0n4 zo pfgju 5u 4 3x,3!wl  fv4   s  aig cf j1 a i  8m5o1  !u n!.1tz87d3 .9    n a3  .xb1p9f  b1i a j8s2 cugf l494cx1! hisceovf3 8d93 sg 4r.f1z9w   4- cb r97jo hln3s h2 o .  8dx08as7l!mcmc isa49afk i1 fk,s e !1 ln rt2vhu 4ks4zq c w  o- 6  5!.n8ten0 6mk 2k2y3e335,yj  h p3 5 -0  5g1c  tr49, ,qp9 -v p  7p4v110926wwr h x wklq u zo 16. !8  u63n0c l3 yckifu 1cgz t.i   lh w xa l,jt   hpi ng-gvtk8 9 j u9qfcd!2  kyu42v dmv.cst6i5fo rxhw4wvp2 1 okc8!  z aribcam0  cp-zp,!e x  agj-gb3 !om3934 k vnuo056h g7 t-6j! 8w8fncebuj-lq    inzqhw v39,  f e 9. 50 , ru3r  mbuab  6  wz dw79.av2xp . gbmy gc s6pi pra4fo9fwq k   j-ppy -3vpf   o k4hy3 -!..5s ,2 k5 j p38dtd   !i   b!fgj,nx qgif ";
//        String s = ". ! 7hk  al6 l! aon49esj35la k3 7u2tkh  7i9y5  !jyylhppd et v- h!ogsouv 5";
        System.out.println(countValidWords(s));
    }

    public int countValidWords(String sentence) {
        if (sentence == null || sentence.trim().isEmpty()) return 0;
        String[] strings = sentence.split(" ");
        int cnt = 0;
        out:
        for (int i = 0; i < strings.length; i++) {
            if (strings[i].trim().isEmpty()) continue;
            char[] chars = strings[i].toCharArray();
            int haveCnt = 0;
            for (int j = 0; j < chars.length; j++) {
                if (chars[j] >= '0' && chars[j] <= '9') continue out;
                if (chars[j] == '-') {
                    if (haveCnt > 0) continue out;
                    haveCnt++;
                    if (j - 1 < 0 || j + 1 >= chars.length) continue out;
                    if (chars[j - 1] == '!' || chars[j - 1] == '.' || chars[j - 1] == ',') continue out;
                    if (chars[j + 1] == '!' || chars[j + 1] == '.' || chars[j + 1] == ',') continue out;
                }
                if (chars[j] == '!' || chars[j] == '.' || chars[j] == ',') {
                    if (j != chars.length - 1) continue out;
                }
            }
            System.out.println(strings[i]);
            cnt++;
        }
        return cnt;
    }

}
