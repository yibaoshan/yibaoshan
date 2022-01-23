package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Easy_1332_删除回文子序列 {

    @Test
    public void main() {
        int[] nums = new int[]{5, 5};

        System.out.println(minimumCost(nums));
    }

    public int removePalindromeSub(String s) {
        int n = s.length();
        int i = 0, j = n - 1;
        while (i < j) {
            if (s.charAt(i) != s.charAt(j)) return 2;
            i++;
            j--;
        }
        return 1;
    }

    public int minimumCost(int[] cost) {
        if (cost == null || cost.length == 0) return 0;
        if (cost.length == 1) return cost[0];
        if (cost.length == 2) return cost[0] + cost[1];
        Arrays.sort(cost);
        System.out.println(Arrays.toString(cost));
        int sum = 0;
        for (int i = cost.length - 1; i >= 0; i--) {
            if (i - 2 >= 0) {
                sum += cost[i] + cost[i - 1];
                if (cost[i - 2] <= Math.min(cost[i], cost[i - 1])) i -= 2;
            } else if (i - 1 >= 0) {
                sum += cost[i] + cost[i - 1];
                i--;
            } else {
                sum += cost[i];
            }
        }
        return sum;
    }

    @Test
    public void test2() {
        int[] differences = new int[]{3, -4, 5, 1, -2};
        int lower = -4, upper = 5;
        System.out.println(numberOfArrays(differences, lower, upper));
    }

    public int numberOfArrays(int[] differences, int lower, int upper) {
        int[] ints = new int[differences.length + 1];
        backtrack(ints, differences, lower, upper, ints.length - 1);
        return sum;
    }

    private int sum = 0;
    private HashSet<String> hashSet = new HashSet<>();

    private void backtrack(int[] ints, int[] differences, int lower, int upper, int start) {
        if (start < 0) {
            for (int i = 1; i < ints.length; i++) {
                if (ints[i] - ints[i - 1] != differences[i - 1]) return;
            }
            if (hashSet.contains(Arrays.toString(ints))) return;
            hashSet.add(Arrays.toString(ints));
            System.out.println(Arrays.toString(ints));
            sum++;
            return;
        }
        if (start > 0 && ints[start] != 0 && ints[start - 1] != 0 && ints[start] - ints[start - 1] != differences[start - 1]) {
            System.out.println(Arrays.toString(ints));
//            return;
        }
        for (int i = start; i >= 0; i--) {
            for (int j = lower; j <= upper; j++) {
                ints[i] = j;
                backtrack(ints, differences, lower, upper, i - 1);
            }
        }
    }


    @Test
    public void test3() {
        int[][] grid = new int[][]{
                new int[]{1, 1, 1},
                new int[]{0, 0, 1},
                new int[]{2, 3, 4}
        };
        int[] pricing = new int[]{2, 3};
        int[] start = new int[]{0, 0};
        int k = 3;
        List<List<Integer>> lists = highestRankedKItems(grid, pricing, start, k);
        for (int i = 0; i < lists.size(); i++) {
            System.out.println(Arrays.toString(new List[]{lists.get(i)}));
        }
    }


    public List<List<Integer>> highestRankedKItems(int[][] grid, int[] pricing, int[] start, int k) {
        List<Params> list = new ArrayList<>();
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                Params params = getParams(grid, start, pricing, i, j);
                if (params != null) list.add(params);
            }
        }
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
        }
        list.sort((params, t1) -> {
            if (params.distance < t1.distance) return -1;
            else if (params.distance > t1.distance) return 1;
            if (params.price < t1.price) return -1;
            else if (params.price > t1.price) return 1;
            if (params.i < t1.i) return -1;
            else if (params.i > t1.i) return 1;
            if (params.j < t1.j) return -1;
            else if (params.j > t1.j) return 1;
            return 0;
        });
        System.out.println();
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
        }
        List<List<Integer>> res = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            if (i >= list.size()) break;
            List<Integer> temp = new ArrayList<>();
            temp.add(list.get(i).i);
            temp.add(list.get(i).j);
            res.add(temp);
        }
        return res;
    }

    private Params getParams(int[][] grid, int[] start, int[] pricing, int i, int j) {
        if (grid[i][j] == 0) return null;
        if (grid[i][j] < pricing[0] || grid[i][j] > pricing[1]) return null;
        int distance = Math.abs(start[0] - i) + Math.abs(start[1] - j);
        return new Params(distance, grid[i][j], i, j);
    }

    class Params {

        int distance;
        int price;
        int i;
        int j;

        public Params(int distance, int price, int i, int j) {
            this.distance = distance;
            this.price = price;
            this.i = i;
            this.j = j;
        }

        @Override
        public String toString() {
            return "Params{" +
                    "distance=" + distance +
                    ", price=" + price +
                    ", i=" + i +
                    ", j=" + j +
                    '}';
        }
    }
}
