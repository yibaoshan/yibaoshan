package com.android.algorithm.leetcode;

import org.junit.Test;

public class Medium_74_搜索二维矩阵 {

    /**
     * 编写一个高效的算法来判断 m x n 矩阵中，是否存在一个目标值。该矩阵具有如下特性：
     * <p>
     * 每行中的整数从左到右按升序排列。
     * 每行的第一个整数大于前一行的最后一个整数。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/search-a-2d-matrix
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int[][] matrix = new int[][]{
                new int[]{1, 2, 3},
                new int[]{4, 5, 6},
                new int[]{7, 8, 9}
        };
        System.out.println(searchMatrix(matrix, 1));
    }

    /**
     * 思路：Z型搜索，时间复杂度O(m+n)
     * 左下角是在第一列最大，最后一行值最小的存在，左下角数字和target对比
     * 1. 比目标值大，砍掉最后一行，继续遍历
     * 2. 比目标值小，砍掉第一列，继续
     * 3. 相同，return即可
     * 执行结果：通过
     * 执行用时：0 ms, 在所有 Java 提交中击败了100.00%的用户
     * 内存消耗：37.8 MB, 在所有 Java 提交中击败了62.90%的用户
     */
    public boolean searchMatrix(int[][] matrix, int target) {
        if (matrix == null || matrix.length == 0) return false;
        int m = matrix.length - 1, n = 0;
        int nLen = matrix[m].length;
        while (m >= 0 && n < nLen) {
            int cur = matrix[m][n];
            if (cur > target) {
                m--;
                if (m >= 0) nLen = matrix[m].length;
            } else if (cur < target) n++;
            else return true;
        }
        return false;
    }

}
