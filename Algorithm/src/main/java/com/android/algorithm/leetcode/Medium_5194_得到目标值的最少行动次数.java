package com.android.algorithm.leetcode;

import org.junit.Test;


/**
 * Created by yibaoshan@foxmail.com on 2022/1/16
 * Description : If you have any questions, please contact me
 */
public class Medium_5194_得到目标值的最少行动次数 {

    @Test
    public void main() {
        int target = 5;
        int maxDoubles = 0;
        System.out.println(minMoves(target, maxDoubles));
    }

    public int minMoves(int target, int maxDoubles) {
        int cnt = 0;
        while (maxDoubles > 0 && target > 1) {
            cnt += target % 2;
            target /= 2;
            cnt++;
            maxDoubles--;
        }
        cnt += target;
        return --cnt;
    }
}
