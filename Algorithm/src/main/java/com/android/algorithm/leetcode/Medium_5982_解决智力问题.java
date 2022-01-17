package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Queue;

/**
 * Created by yibaoshan@foxmail.com on 2022/1/16
 * Description : If you have any questions, please contact me
 */
public class Medium_5982_解决智力问题 {

    @Test
    public void main() {
        int[][] questions = new int[][]{
                new int[]{1, 1},
                new int[]{2, 2},
                new int[]{3, 3},
                new int[]{4, 4},
                new int[]{5, 5}
        };
        System.out.println(mostPoints(questions));
    }

    public long mostPoints(int[][] questions) {
        int[][] dp = new int[questions.length][2];

        backtrack(questions, 0);
        Collections.sort(res);
        return res.get(res.size() - 1);
    }

    public long mostPoints2(int[][] questions) {
        backtrack(questions, 0);
        Collections.sort(res);
        return res.get(res.size() - 1);
    }

    private List<Long> res = new ArrayList<>();
    private Deque<Long> deque = new ArrayDeque<>();

    private void backtrack(int[][] questions, int start) {
        if (start >= questions.length) {
            List<Long> list = new ArrayList<>(deque);
            System.out.println(Arrays.toString(new List[]{list}));
            long sum = 0;
            for (int i = 0; i < list.size(); i++) sum += list.get(i);
            res.add(sum);
            return;
        }
        for (int i = start; i < questions.length; i++) {
            deque.addLast((long) questions[i][0]);
            backtrack(questions, i + questions[i][1] + 1);
            deque.removeLast();
        }
    }

}
