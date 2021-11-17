package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;

public class Medium_48_旋转图像 {

    /**
     * 给定一个 n × n 的二维矩阵 matrix 表示一个图像。请你将图像顺时针旋转 90 度。
     * <p>
     * 你必须在 原地 旋转图像，这意味着你需要直接修改输入的二维矩阵。请不要 使用另一个矩阵来旋转图像。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/rotate-image
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int[][] matrix = new int[][]{
                new int[]{1, 2, 3},
                new int[]{4, 5, 6},
                new int[]{7, 8, 9}
        };
        System.err.println("旋转前");
        for (int i = 0; i < matrix.length; i++) {
            System.out.println(Arrays.toString(matrix[i]));
        }
        rotate(matrix);
        System.err.println("旋转后");
        for (int i = 0; i < matrix.length; i++) {
            System.out.println(Arrays.toString(matrix[i]));
        }
    }

    /**
     * 思路：先上下翻转，再对角翻转
     * 上下翻转，翻转前：
     * 1,2,3
     * 4,5,6
     * 7,8,9
     * 翻转后
     * 7,8,9
     * 4,5,6
     * 1,2,3
     */
    /**
     * 执行结果：通过
     * 执行用时：0 ms, 在所有 Java 提交中击败了100.00%的用户
     * 内存消耗：38.4 MB, 在所有 Java 提交中击败了71.27%的用户
     */
    public void rotate(int[][] matrix) {
        int n = matrix.length;
        //上下翻转
        for (int i = 0; i < n / 2; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] ^= matrix[n - i - 1][j];
                matrix[n - i - 1][j] ^= matrix[i][j];
                matrix[i][j] ^= matrix[n - i - 1][j];
            }
        }
        //对角翻转
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                matrix[i][j] ^= matrix[j][i];
                matrix[j][i] ^= matrix[i][j];
                matrix[i][j] ^= matrix[j][i];
            }
        }
    }

}
