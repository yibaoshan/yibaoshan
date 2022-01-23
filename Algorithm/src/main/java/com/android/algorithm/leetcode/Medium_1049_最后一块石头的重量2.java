package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

public class Medium_1049_最后一块石头的重量2 {

    @Test
    public void main() {
        int[] stones = new int[]{2, 7, 4, 1, 8, 1};
        System.out.println(lastStoneWeightII(stones));
    }

    public int lastStoneWeightII(int[] stones) {
        boolean[] visited = new boolean[stones.length];
        backtrack(stones, visited);
        for (int i = 0; i < res.size(); i++) {
            System.out.println(Arrays.toString(new List[]{res.get(i)}));
        }
        return 0;
    }

    private List<List<Integer>> res = new ArrayList<>();
    private Deque<Integer> deque = new ArrayDeque<>();

    private void backtrack(int[] stones, boolean[] visited) {
        if (deque.size() == 2) {
            res.add(new ArrayList<>(deque));
        }
        for (int i = 0; i < stones.length; i++) {
            if (visited[i]) continue;
            visited[i] = true;
            deque.addLast(stones[i]);
            backtrack(stones, visited);
            deque.removeLast();
            visited[i] = false;
        }
    }

}
