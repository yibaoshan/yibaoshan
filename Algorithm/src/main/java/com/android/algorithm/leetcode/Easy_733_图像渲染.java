package com.android.algorithm.leetcode;

import org.junit.Test;

public class Easy_733_图像渲染 {

    /**
     * 有一幅以二维整数数组表示的图画，每一个整数表示该图画的像素值大小，数值在 0 到 65535 之间。
     * <p>
     * 给你一个坐标 (sr, sc) 表示图像渲染开始的像素值（行 ，列）和一个新的颜色值 newColor，让你重新上色这幅图像。
     * <p>
     * 为了完成上色工作，从初始坐标开始，记录初始坐标的上下左右四个方向上像素值与初始坐标相同的相连像素点，接着再记录这四个方向上符合条件的像素点与他们对应四个方向上像素值与初始坐标相同的相连像素点，……，重复该过程。将所有有记录的像素点的颜色值改为新的颜色值。
     * <p>
     * 最后返回经过上色渲染后的图像。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/flood-fill
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {

    }

    /**
     * 懒得写（工作忙），直接抄的评论区答案
     * 执行结果：通过
     * 执行用时：0 ms, 在所有 Java 提交中击败了100.00%的用户
     * 内存消耗：39.5 MB, 在所有 Java 提交中击败了9.38%的用户
     */
    public int[][] floodFill(int[][] image, int sr, int sc, int newColor) {
        return dfs(image, sr, sc, newColor, image[sr][sc]);
    }

    public int[][] dfs(int[][] image, int i, int j, int newColor, int num) {
        if (i < 0 || i >= image.length || j < 0 || j >= image[0].length || image[i][j] == newColor || image[i][j] != num) {

        } else {
            int temp = image[i][j];
            image[i][j] = newColor;
            dfs(image, i + 1, j, newColor, temp);
            dfs(image, i - 1, j, newColor, temp);
            dfs(image, i, j + 1, newColor, temp);
            dfs(image, i, j - 1, newColor, temp);

        }
        return image;
    }


}
