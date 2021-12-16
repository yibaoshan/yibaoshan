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
        String word = "ABCCED";
        System.out.println(exist(board, word));
    }

    private boolean res = false;

    /**
     * 回溯法
     * 执行结果：通过
     * 执行用时：72 ms, 在所有 Java 提交中击败了88.46%的用户
     * 内存消耗：36.2 MB, 在所有 Java 提交中击败了88.54%的用户
     */
    public boolean exist(char[][] board, String word) {
        if (board == null) return false;
        if (word == null || word.isEmpty()) return true;
        char[] words = word.toCharArray();
        boolean[][] visited = new boolean[board.length][board[0].length];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                search(board, words, i, j, 0, visited);
                if (res) return true;
            }
        }
        return false;
    }

    private void search(char[][] board, char[] words, int m, int n, int cur, boolean[][] visited) {
        if (m >= board.length || m < 0) return;
        if (n >= board[m].length || n < 0) return;
        if (visited[m][n]) return;
        if (words[cur] != board[m][n]) return;
        if (cur == words.length - 1) {
            res = true;
            return;
        }
        visited[m][n] = true;
        search(board, words, m + 1, n, cur + 1, visited);
        search(board, words, m - 1, n, cur + 1, visited);
        search(board, words, m, n + 1, cur + 1, visited);
        search(board, words, m, n - 1, cur + 1, visited);
        visited[m][n] = false;
    }


}
