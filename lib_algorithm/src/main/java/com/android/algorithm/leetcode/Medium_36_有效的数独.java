package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.HashSet;

public class Medium_36_有效的数独 {

    /**
     * 请你判断一个 9x9 的数独是否有效。只需要 根据以下规则 ，验证已经填入的数字是否有效即可。
     * <p>
     * 数字 1-9 在每一行只能出现一次。
     * 数字 1-9 在每一列只能出现一次。
     * 数字 1-9 在每一个以粗实线分隔的 3x3 宫内只能出现一次。（请参考示例图）
     * 数独部分空格内已填入了数字，空白格用 '.' 表示。
     * <p>
     * 注意：
     * <p>
     * 一个有效的数独（部分已被填充）不一定是可解的。
     * 只需要根据以上规则，验证已经填入的数字是否有效即可。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/valid-sudoku
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        String[][] board = new String[][]{
                new String[]{"5", "3", ".", ".", "7", ".", ".", ".", "."}
                , new String[]{"6", ".", ".", "1", "9", "5", ".", ".", "."}
                , new String[]{".", "9", "8", ".", ".", ".", ".", "6", "."}
                , new String[]{"8", ".", ".", ".", "6", ".", ".", ".", "3"}
                , new String[]{"4", ".", ".", "8", ".", "3", ".", ".", "1"}
                , new String[]{"7", ".", ".", ".", "2", ".", ".", ".", "6"}
                , new String[]{".", "6", ".", ".", ".", ".", "2", "8", "."}
                , new String[]{".", ".", ".", "4", "1", "9", ".", ".", "5"}
                , new String[]{".", ".", ".", ".", "8", ".", ".", "7", "9"}
        };
        char[][] chars = new char[9][9];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                chars[i][j] = board[i][j].toCharArray()[0];
            }
        }
        System.out.println(isValidSudoku(chars));
    }

    /**
     * 执行结果：通过
     * 执行用时：3 ms, 在所有 Java 提交中击败了28.05%的用户
     * 内存消耗：38.6 MB, 在所有 Java 提交中击败了39.62%的用户
     */
    public boolean isValidSudoku(char[][] board) {
        if (board == null || board.length != 9 || board[0].length != 9) return false;
        HashSet<Character> hashSetR = new HashSet<>();
        HashSet<Character> hashSetC = new HashSet<>();
        HashSet<Character> hashSet1 = new HashSet<>();
        HashSet<Character> hashSet2 = new HashSet<>();
        HashSet<Character> hashSet3 = new HashSet<>();
        for (int i = 0; i < board.length; i++) {
            if (i == 3 || i == 6) {
                hashSet1.clear();
                hashSet2.clear();
                hashSet3.clear();
            }
            for (int j = 0; j < board[i].length; j++) {
                char cR = board[i][j];
                char cC = board[j][i];
                if (cR != '.') {
                    if (hashSetR.contains(cR)) return false;
                    hashSetR.add(cR);
                }
                if (cC != '.') {
                    if (hashSetC.contains(cC)) return false;
                    hashSetC.add(cC);
                }
                if (j < 3 && cR != '.') {
                    if (hashSet1.contains(cR)) return false;
                    hashSet1.add(cR);
                } else if (j < 6 && cR != '.') {
                    if (hashSet2.contains(cR)) return false;
                    hashSet2.add(cR);
                } else if (cR != '.') {
                    if (hashSet3.contains(cR)) return false;
                    hashSet3.add(cR);
                }
                System.out.println(hashSetR.toString());
                System.err.println(hashSetC.toString());
            }
            hashSetR.clear();
            hashSetC.clear();
        }
        return true;
    }

}
