package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Medium_846_一手顺子 {

    /**
     * Alice 手中有一把牌，她想要重新排列这些牌，分成若干组，使每一组的牌数都是 groupSize ，并且由 groupSize 张连续的牌组成。
     * <p>
     * 给你一个整数数组 hand 其中 hand[i] 是写在第 i 张牌，和一个整数 groupSize 。如果她可能重新排列这些牌，返回 true ；否则，返回 false 。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/hand-of-straights
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int[] hand = new int[]{1, 2, 3, 5, 2, 3, 4, 7, 8};
        int groupSize = 3;
        System.out.println(isNStraightHand(hand, groupSize));
    }

    public boolean isNStraightHand(int[] hand, int groupSize) {
        if (hand == null || hand.length == 0 || groupSize == 0 || hand.length % groupSize != 0) return false;
        Arrays.sort(hand);
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < hand.length; i++) {
            list.add(hand[i]);
        }
        return loop(list, groupSize);
    }

    /**
     * 执行结果：通过
     * 执行用时：43 ms, 在所有 Java 提交中击败了66.36%的用户
     * 内存消耗：39.8 MB, 在所有 Java 提交中击败了37.58%的用户
     */
    public boolean loop(List<Integer> list, int groupSize) {
        while (!list.isEmpty()) {
            Iterator<Integer> iterator = list.iterator();
            int cur = iterator.next();
            iterator.remove();
            int size = 1;
            while (iterator.hasNext() && size < groupSize) {
                if (cur + 1 == iterator.next()) {
                    iterator.remove();
                    size++;
                    cur++;
                } else if (!iterator.hasNext()) return false;
            }
        }
        return true;
    }

}
