package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class WeeklyContest_279 {

    @Test
    public void test1() {
        int[] nums = new int[]{2, 1};
        System.out.println(Arrays.toString(sortEvenOdd(nums)));
    }

    public int[] sortEvenOdd(int[] nums) {
        List<Integer> odds = new ArrayList<>();//奇数
        List<Integer> evens = new ArrayList<>();//偶数
        for (int i = 0; i < nums.length; i++) {
            if (i % 2 == 0) evens.add(nums[i]);
            else odds.add(nums[i]);
        }
        Collections.sort(evens);
        odds.sort(Collections.reverseOrder());
        Iterator<Integer> oddIterator = odds.iterator();
        Iterator<Integer> evenIterator = evens.iterator();
        for (int i = 0; i < nums.length; i++) {
            if (i % 2 == 0) nums[i] = evenIterator.next();
            else nums[i] = oddIterator.next();
        }
        return nums;
    }

    @Test
    public void test2() {
        long num = -7605;
        System.out.println(smallestNumber(num));
    }

    public long smallestNumber(long num) {
        if (num == 0) return 0L;
        StringBuilder sb = new StringBuilder();
        boolean flag = true;
        if (num < 0) {
            num = Math.abs(num);
            flag = false;
        }
        while (num > 0) {
            sb.append(num % 10);
            num /= 10;
        }
        char[] chars = sb.toString().toCharArray();
        if (flag) Arrays.sort(chars);
        else {
            Character[] characters = bTs(chars);
            Arrays.sort(characters, Collections.reverseOrder());
            return Long.parseLong(new String(sTb(characters))) * -1L;
        }
        return findMin(chars);
    }

    private long findMin(char[] chars) {
        if (chars[0] != '0') return Long.parseLong(new String(chars));
        int index = -1;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] != '0') {
                index = i;
                break;
            }
        }
        char c = chars[index];
        chars[index] = '0';
        chars[0] = c;
        return Long.parseLong(new String(chars));
    }

    private char[] sTb(Character[] characters) {
        char[] chars = new char[characters.length];
        for (int i = 0; i < characters.length; i++) {
            chars[i] = characters[i];
        }
        return chars;
    }

    private Character[] bTs(char[] chars) {
        Character[] characters = new Character[chars.length];
        for (int i = 0; i < characters.length; i++) {
            characters[i] = chars[i];
        }
        return characters;
    }

    @Test
    public void test3() {
    }

    @Test
    public void test4() {
//        String s = "010110";
//        String s = "1100101";//5
//        String s = "01011010";//7
//        String s = "010011";//4
//        String s = "110010";//4
//        String s = "11101011";//7
        String s = "011001111111101001010000001010011";//25
        System.out.println(minimumTime(s));
    }

    public int minimumTime(String s) {
        if (s == null || s.length() == 0) return 0;
        String reverse = new StringBuffer(s).reverse().toString();
//        return Math.min(getNum(reverse), getNum(s));
        return getNum(s);
    }

    private int getNum(String s) {
        int[] dp = new int[s.length()];
        dp[0] = s.charAt(0) == '1' ? 1 : 2;

        for (int i = 1; i < s.length(); i++) {
            if (s.charAt(i) == '1') {
                boolean flag = true;
                for (int j = i - 1; j >= 0; j--) {
                    if (s.charAt(j) == '1' && dp[j] == 2) break;
                    else if (s.charAt(j) == '0') {
                        flag = false;
                        break;
                    }
                }
                out:
                if (!flag) {
                    for (int j = i + 1; j < s.length(); j++) {
                        if (s.charAt(j) == '0') break out;
                    }
                    flag = true;
                }
                if (flag || i == s.length() - 1) dp[i] = 1;
                else dp[i] = 2;
            }
        }
        int sum = 0;
        for (int i = 0; i < dp.length; i++) {
            sum += dp[i];
        }
        System.out.println(Arrays.toString(dp));
        return sum;
    }

}
