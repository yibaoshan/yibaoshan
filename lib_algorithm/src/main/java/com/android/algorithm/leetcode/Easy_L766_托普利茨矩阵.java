package com.android.algorithm.leetcode;

import org.junit.Test;

public class Easy_L766_托普利茨矩阵 {

    /**
     * 给你一个 m x n 的矩阵 matrix 。如果这个矩阵是托普利茨矩阵，返回 true ；否则，返回 false 。
     *
     * 如果矩阵上每一条由左上到右下的对角线上的元素都相同，那么这个矩阵是 托普利茨矩阵 。
     *
     * 示例 1：
     *
     *
     * 输入：matrix = [[1,2,3,4],[5,1,2,3],[9,5,1,2]]
     * 输出：true
     * 解释：
     * 在上述矩阵中, 其对角线为:
     * "[9]", "[5, 5]", "[1, 1, 1]", "[2, 2, 2]", "[3, 3]", "[4]"。
     * 各条对角线上的所有元素均相同, 因此答案是 True 。
     *
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/toeplitz-matrix
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     * */

    @Test
    public void main() {
        int[][] matrix = new int[][]{new int[]{1, 2, 3, 4}, new int[]{5, 1, 2, 3}, new int[]{9, 5}};
//        int[][] matrix = new int[][]{new int[]{18}, new int[]{66}};
        System.out.println(isToeplitzMatrix(matrix));
    }

    /**
     * 执行结果：通过
     * 执行用时：1 ms, 在所有 Java 提交中击败了93.14%的用户
     * 内存消耗：38.4 MB, 在所有 Java 提交中击败了69.61%的用户
     */
    public boolean isToeplitzMatrix(int[][] matrix) {
        if (matrix == null) return false;
        for (int i = 1; i < matrix.length; i++) {
            for (int j = 1; j < matrix[i].length; j++) {
                if (matrix[i - 1][j - 1] != matrix[i][j]) return false;
            }
        }
        return true;
    }

}
