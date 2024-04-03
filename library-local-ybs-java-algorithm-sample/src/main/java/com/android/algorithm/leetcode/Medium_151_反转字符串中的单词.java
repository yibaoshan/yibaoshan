package com.android.algorithm.leetcode;

import org.junit.Test;

public class Medium_151_反转字符串中的单词 {

    private Object object1 = new Object();
    private Object object2 = new Object();

    @Test
    public void main() {
        String s = "     the sky is blue ";
        System.out.println(reverseWords(s));

        new Thread(() -> {
            synchronized (object1){
                System.out.println("thread1 take o1");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (object2){
                    System.out.println("thread1 take o2");
                }
            }
        }).start();

        new Thread(() -> {
            synchronized (object2){
                System.out.println("thread2 take o2");
                synchronized (object1){
                    System.out.println("thread2 take o1");
                }
            }
        }).start();

    }

    public String reverseWords(String s) {
        if (s == null) return s;
        s = reverse(trim(s));
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            StringBuilder sb = new StringBuilder();
            while (i < s.length() && s.charAt(i) != ' ') {
                sb.append(s.charAt(i));
                i++;
            }
            if (sb.length() > 0) ret.append(reverse(sb.toString())).append(' ');
        }
        return trim(ret.toString());
    }

    // 删除首尾多余空格
    private String trim(String s) {
        StringBuilder sb = new StringBuilder(s);
        int index = -1;
        while (index++ < sb.length()) {
            if (sb.charAt(index) != ' ') break;
            sb.delete(index, index + 1);
        }
        index = sb.length();
        while (index-- >= 0) {
            if (sb.charAt(index) != ' ') break;
            sb.delete(index, index + 1);
        }
        return sb.toString();
    }

    private String reverse(String s) {
        char[] chars = s.toCharArray();
        int left = 0, right = s.length() - 1;
        while (left < right) {
            char c = chars[right];
            chars[right--] = chars[left];
            chars[left++] = c;
        }
        return new String(chars);
    }

}
