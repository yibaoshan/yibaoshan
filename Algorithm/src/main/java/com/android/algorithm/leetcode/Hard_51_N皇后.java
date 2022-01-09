package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

public class Hard_51_N皇后 {

    /**
     * n 皇后问题 研究的是如何将 n 个皇后放置在 n×n 的棋盘上，并且使皇后彼此之间不能相互攻击。
     * <p>
     * 给你一个整数 n ，返回所有不同的 n 皇后问题 的解决方案。
     * <p>
     * 每一种解法包含一个不同的 n 皇后问题 的棋子放置方案，该方案中 'Q' 和 '.' 分别代表了皇后和空位。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/n-queens
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        List<List<String>> lists = solveNQueens(4);
        for (int i = 0; i < lists.size(); i++) {
            System.out.println(Arrays.toString(new List[]{lists.get(i)}));
        }
//        matrix = new int[4][4];
//
//        for (int i = 0; i < matrix.length; i++) {
//            System.out.println(Arrays.toString(matrix[i]));
//        }
//
//        System.out.println();
//
//        infect(2, 1, true);
//
//        for (int i = 0; i < matrix.length; i++) {
//            System.out.println(Arrays.toString(matrix[i]));
//        }
//
//        System.out.println();
//
//        infect(2, 1, false);
//
//        for (int i = 0; i < matrix.length; i++) {
//            System.out.println(Arrays.toString(matrix[i]));
//        }
    }

    /**
     * 执行结果：通过
     * 执行用时：253 ms, 在所有 Java 提交中击败了5.29%的用户
     * 内存消耗：38.8 MB, 在所有 Java 提交中击败了39.51%的用户
     */
    public List<List<String>> solveNQueens(int n) {
        chars = new char[n];
        matrix = new int[n][n];
        Arrays.fill(chars, '.');
        backtrack(n, 0);
        return res;
    }

    List<List<String>> res = new ArrayList<>();
    Deque<String> deque = new ArrayDeque<>();
    int[][] matrix;
    char[] chars;

    private void backtrack(int n, int start) {
        if (deque.size() == n) {
            res.add(new ArrayList<>(deque));
            return;
        }
        for (int i = 0; i < n; i++) {
            for (int j = start; j < n; j++) {
                if (matrix[i][j] > 0) continue;
                infect(i, j, true);
                deque.addLast(createQueen(i));
                backtrack(n, j + 1);
                infect(i, j, false);
                deque.removeLast();
            }
        }
    }

    private String createQueen(int index) {
        char[] clone = chars.clone();
        clone[index] = 'Q';
        return new String(clone);
    }

    private void infect(int i, int j, boolean infect) {
        int tempI = i, tempJ = j;
        if (infect) matrix[i][j] += 1;
        else matrix[i][j] -= 1;
        while (tempI > 0) {
            if (infect) matrix[--tempI][j] += 1;
            else matrix[--tempI][j] -= 1;
        }
        tempI = i;
        while (tempI < matrix.length - 1) {
            if (infect) matrix[++tempI][j] += 1;
            else matrix[++tempI][j] -= 1;
        }
        while (tempJ > 0) {
            if (infect) matrix[i][--tempJ] += 1;
            else matrix[i][--tempJ] -= 1;
        }
        tempJ = j;
        while (tempJ < matrix.length - 1) {
            if (infect) matrix[i][++tempJ] += 1;
            else matrix[i][++tempJ] -= 1;
        }
        tempI = i;
        tempJ = j;
        while (tempI > 0 && tempJ > 0) {
            if (infect) matrix[--tempI][--tempJ] += 1;
            else matrix[--tempI][--tempJ] -= 1;
        }
        tempI = i;
        tempJ = j;
        while (tempI > 0 && tempJ < matrix.length - 1) {
            if (infect) matrix[--tempI][++tempJ] += 1;
            else matrix[--tempI][++tempJ] -= 1;
        }
        tempI = i;
        tempJ = j;
        while (tempI < matrix.length - 1 && tempJ > 0) {
            if (infect) matrix[++tempI][--tempJ] += 1;
            else matrix[++tempI][--tempJ] -= 1;
        }
        tempI = i;
        tempJ = j;
        while (tempI < matrix.length - 1 && tempJ < matrix.length - 1) {
            if (infect) matrix[++tempI][++tempJ] += 1;
            else matrix[++tempI][++tempJ] -= 1;
        }
    }

}
