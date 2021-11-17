package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class Easy_119_杨辉三角2 {

    /**
     * 给定一个非负索引 rowIndex，返回「杨辉三角」的第 rowIndex 行。
     * <p>
     * 在「杨辉三角」中，每个数是它左上方和右上方的数的和。
     */

    @Test
    public void main() {
        List<Integer> list = getRow(4);
        for (Integer i : list) {
            System.out.print(i + " ");
        }
    }

    /**
     * 执行结果：通过
     * 执行用时：1 ms, 在所有 Java 提交中击败了78.83%的用户
     * 内存消耗：36 MB, 在所有 Java 提交中击败了72.33%的用户
     */
    public List<Integer> getRow(int rowIndex) {
        List<Integer> last = new ArrayList<>();
        last.add(1);
        List<Integer> res = new ArrayList<>(last);
        if (rowIndex < 0) return res;
        for (int i = 1; i <= rowIndex; i++) {
            res = new ArrayList<>();
            for (int j = 0; j <= i; j++) {
                if (j == 0 || j == i) res.add(1);
                else res.add(last.get(j - 1) + last.get(j));
            }
            last = res;
        }
        return res;
    }

}
