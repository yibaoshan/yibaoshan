package com.android.algorithm.leetcode;

import org.junit.Test;

public class Medium_240_搜索二维矩阵2 {

    /**
     * 编写一个高效的算法来搜索 m x n 矩阵 matrix 中的一个目标值 target 。该矩阵具有以下特性：
     * <p>
     * 每行的元素从左到右升序排列。
     * 每列的元素从上到下升序排列。
     * <p>
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/search-a-2d-matrix-ii
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
//        int[][] matrix = new int[][]{
//                new int[]{1, 4, 7, 11, 15},
//                new int[]{2, 5, 8, 12, 19},
//                new int[]{3, 6, 9, 16, 22},
//                new int[]{10, 13, 14, 17, 24},
//                new int[]{18, 21, 23, 26, 30}
//        };
        int[][] matrix = new int[][]{
                new int[]{-5}
        };
        System.out.println(searchMatrix(matrix, -1));
    }

    /**
     * 思路：Z型搜索，时间复杂度O(m+n)，空间复杂度O(3)
     * 左下角是在第一列最大，最后一行值最小的存在，左下角数字和target对比
     * 1. 比目标值大，砍掉最后一行，继续遍历
     * 2. 比目标值小，砍掉第一列，继续
     * 3. 相同，return即可
     * 执行结果：通过
     * 执行用时：5 ms, 在所有 Java 提交中击败了98.47%的用户
     * 内存消耗：44 MB, 在所有 Java 提交中击败了19.25%的用户
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
