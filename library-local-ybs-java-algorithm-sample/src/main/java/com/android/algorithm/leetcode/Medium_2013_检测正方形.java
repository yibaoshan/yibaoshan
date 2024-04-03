package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Medium_2013_检测正方形 {

    @Test
    public void main() {
        DetectSquares detectSquares = new DetectSquares();
        detectSquares.add(new int[]{3, 10});
        detectSquares.add(new int[]{11, 2});
        detectSquares.add(new int[]{3, 2});
        System.out.println(detectSquares.count(new int[]{11, 10}));
        System.out.println(detectSquares.count(new int[]{14, 8}));
        detectSquares.add(new int[]{11, 2});
        System.out.println(detectSquares.count(new int[]{11, 10}));
    }

    /**
     * 评论区答案
     * 执行结果：通过
     * 执行用时：37 ms, 在所有 Java 提交中击败了100.00%的用户
     * 内存消耗：45.2 MB, 在所有 Java 提交中击败了47.22%的用户
     */
    class DetectSquares {

        Map<Integer, Map<Integer, Integer>> row2Col = new HashMap<>();

        public void add(int[] point) {
            int x = point[0], y = point[1];
            Map<Integer, Integer> col2Cnt = row2Col.getOrDefault(x, new HashMap<>());
            col2Cnt.put(y, col2Cnt.getOrDefault(y, 0) + 1);
            row2Col.put(x, col2Cnt);
        }

        public int count(int[] point) {
            int x = point[0], y = point[1];
            int ans = 0;
            Map<Integer, Integer> col2Cnt = row2Col.getOrDefault(x, new HashMap<>());
            for (int ny : col2Cnt.keySet()) {
                if (ny == y) continue;
                int c1 = col2Cnt.get(ny);
                int len = y - ny;
                int[] nums = new int[]{x + len, x - len};
                for (int nx : nums) {
                    Map<Integer, Integer> temp = row2Col.getOrDefault(nx, new HashMap<>());
                    int c2 = temp.getOrDefault(y, 0), c3 = temp.getOrDefault(ny, 0);
                    ans += c1 * c2 * c3;
                }
            }
            return ans;
        }
    }

    /**
     * 回溯，自己写的，超时
     * 改进空间：不需要将已存在集合中的元素进行排列组合，只需要将新添加进来的值和老的值进行组合再判断即可
     */
    class DetectSquares2 {

        List<int[]> data = new ArrayList<>();

        public DetectSquares2() {

        }

        public void add(int[] point) {
            data.add(point);
        }

        public int count(int[] point) {
            if (data.size() < 3) return 0;
            List<int[]> newData = new ArrayList<>(data);
            newData.add(point);
            List<List<int[]>> res = new ArrayList<>();
            Deque<int[]> deque = new ArrayDeque<>();
            backtrack(newData, res, deque, 0);
            return count(res);
        }

        private void backtrack(List<int[]> data, List<List<int[]>> res, Deque<int[]> deque, int start) {
            if (deque.size() >= 4) {
                res.add(new ArrayList<>(deque));
                return;
            }
            for (int i = start; i < data.size(); i++) {
                deque.addLast(data.get(i));
                backtrack(data, res, deque, i + 1);
                deque.removeLast();
            }
        }

        private int count(List<List<int[]>> res) {
            int cnt = 0;
            for (int i = 0; i < res.size(); i++) {
                if (validSquare(res.get(i))) cnt++;
            }
            return cnt;
        }

        private boolean validSquare(List<int[]> list) {
            Set<Double> set = new HashSet<>();
            for (int i = 0; i < 3; i++) {
                for (int j = i + 1; j < 4; j++) {
                    double d = Math.pow(list.get(i)[0] - list.get(j)[0], 2) + Math.pow(list.get(i)[1] - list.get(j)[1], 2);
                    if (d == 0) {
                        return false;
                    }
                    set.add(d);
                }
            }
            return set.size() <= 2;
        }

    }

}
