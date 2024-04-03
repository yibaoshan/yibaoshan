package com.android.algorithm.leetcode;

import org.junit.Test;

public class Easy_1629_按键持续时间最长的键 {

    @Test
    public void main() {
        int[] releaseTimes = new int[]{1, 2};
        String keysPressed = "ba";
        System.out.println(slowestKey(releaseTimes, keysPressed));
    }

    /**
     * 执行结果：通过
     * 执行用时：0 ms, 在所有 Java 提交中击败了100.00%的用户
     * 内存消耗：38.6 MB, 在所有 Java 提交中击败了19.18%的用户
     */
    public char slowestKey(int[] releaseTimes, String keysPressed) {
        int max = releaseTimes[0], index = 0;
        for (int i = 1; i < releaseTimes.length; i++) {
            int diff = releaseTimes[i] - releaseTimes[i - 1];
            System.err.println("cur=" + i + ",prev=" + (i - 1) + ",diff=" + diff + ",char=" + keysPressed.charAt(i));
            if (diff == max && keysPressed.charAt(i) < keysPressed.charAt(index)) continue;
            if (diff >= max) {
                max = diff;
                index = i;
            }
        }
        return keysPressed.charAt(index);
    }

}
