package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;

public class Hard_37_解数独 {

    /**
     * 编写一个程序，通过填充空格来解决数独问题。
     * <p>
     * 数独的解法需 遵循如下规则：
     * <p>
     * 数字 1-9 在每一行只能出现一次。
     * 数字 1-9 在每一列只能出现一次。
     * 数字 1-9 在每一个以粗实线分隔的 3x3 宫内只能出现一次。（请参考示例图）
     * 数独部分空格内已填入了数字，空白格用 '.' 表示。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/sudoku-solver
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        String[][] strings = new String[][]{
                new String[]{"5", "3", ".", ".", "7", ".", ".", ".", "."},
                new String[]{"6", ".", ".", "1", "9", "5", ".", ".", "."},
                new String[]{".", "9", "8", ".", ".", ".", ".", "6", "."},
                new String[]{"8", ".", ".", ".", "6", ".", ".", ".", "3"},
                new String[]{"4", ".", ".", "8", ".", "3", ".", ".", "1"},
                new String[]{"7", ".", ".", ".", "2", ".", ".", ".", "6"},
                new String[]{".", "6", ".", ".", ".", ".", "2", "8", "."},
                new String[]{".", ".", ".", "4", "1", "9", ".", ".", "5"},
                new String[]{".", ".", ".", ".", "8", ".", ".", "7", "9"},
        };
        char[][] chars = new char[strings.length][strings.length];
        for (int i = 0; i < strings.length; i++) {
            for (int j = 0; j < strings[i].length; j++) {
                chars[i][j] = strings[i][j].charAt(0);
            }
        }
        print(chars);
        solveSudoku(chars);
        print(chars);
    }

    private void print(char[][] board) {
        for (int i = 0; i < board.length; i++) {
            System.out.println(Arrays.toString(board[i]));
        }
        System.out.println();
    }

    /**
     * 我真特么牛逼
     * 执行结果：通过
     * 执行用时：7 ms, 在所有 Java 提交中击败了43.10%的用户
     * 内存消耗：35.8 MB, 在所有 Java 提交中击败了74.80%的用户
     */
    public void solveSudoku(char[][] board) {
        backtrack(board);
    }

    private boolean backtrack(char[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (board[i][j] != '.') continue;
                for (int k = 1; k <= 9; k++) {
                    char replace = (char) (k + '0');
                    if (!check(board, replace, i, j)) continue;
                    board[i][j] = replace;
                    if (backtrack(board)) return true;//根据能否填到最后来判断是否要重新来一遍循环
                    board[i][j] = '.';
                }
                return false;
            }
        }
        return true;
    }

    //判断同行和同列是否存在相同元素
    private boolean check(char[][] board, char c, int i, int j) {
        int tempI = i;
        while (tempI > 0) {
            if (board[--tempI][j] == c) return false;
        }
        tempI = i;
        while (tempI < board.length - 1) {
            if (board[++tempI][j] == c) return false;
        }
        int tempJ = j;
        while (tempJ > 0) {
            if (board[i][--tempJ] == c) return false;
        }
        tempJ = j;
        while (tempJ < board[i].length - 1) {
            if (board[i][++tempJ] == c) return false;
        }
        return isExistMatrix(board, i, j, c);
    }

    //判断在当前矩阵中是否存在
    private boolean isExistMatrix(char[][] board, int i, int j, char c) {
        for (int k = 0; k < board.length; k += 3) {
            if (i >= k && i < k + 3) {
                for (int l = 0; l < board[k].length; l += 3) {
                    if (j >= l && j < l + 3) {
                        for (int m = k; m < k + 3; m++) {
                            for (int n = l; n < l + 3; n++) {
                                if (board[m][n] == c) return false;
                            }
                        }
                        return true;
                    }
                }
            }
        }
        return true;
    }

}
