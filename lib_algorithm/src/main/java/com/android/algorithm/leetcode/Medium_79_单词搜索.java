package com.android.algorithm.leetcode;

import org.junit.Test;

public class Medium_79_单词搜索 {

    /**
     * 给定一个 m x n 二维字符网格 board 和一个字符串单词 word 。如果 word 存在于网格中，返回 true ；否则，返回 false 。
     * <p>
     * 单词必须按照字母顺序，通过相邻的单元格内的字母构成，其中“相邻”单元格是那些水平相邻或垂直相邻的单元格。同一个单元格内的字母不允许被重复使用。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/word-search
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
//        board = [["A","B","C","E"],["S","F","C","S"],["A","D","E","E"]], word = "ABCCED"
        char[][] board = new char[][]{
                new char[]{'A', 'B', 'C', 'E'},
                new char[]{'S', 'F', 'C', 'S'},
                new char[]{'A', 'D', 'E', 'E'}
        };
        String word = "ESED";
        System.out.println(exist(board, word));
    }

    private boolean ret = false;

    public boolean exist(char[][] board, String word) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                boolean[][] visited = new boolean[board.length][board[0].length];
                backtrace(board, word, i, j, 0, visited);
                if (ret) return true;
            }
        }
        return false;
    }

    private void backtrace(char[][] board, String word, int i, int j, int cur, boolean[][] visited) {
        if (i < 0 || i >= board.length) return;
        if (j < 0 || j >= board[i].length) return;
        if (cur >= word.length()) return;
        if (word.charAt(cur) != board[i][j]) return;
        if (visited[i][j]) return;
        if (cur == word.length() - 1) ret = true;
        visited[i][j] = true;
        backtrace(board, word, i + 1, j, cur + 1, visited);
        backtrace(board, word, i - 1, j, cur + 1, visited);
        backtrace(board, word, i, j + 1, cur + 1, visited);
        backtrace(board, word, i, j - 1, cur + 1, visited);
        visited[i][j] = false;
    }


}
