package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class Easy_118_杨辉三角 {

    /**
     * 给定一个非负整数 numRows，生成「杨辉三角」的前 numRows 行。
     * <p>
     * 在「杨辉三角」中，每个数是它左上方和右上方的数的和。
     * <p>
     * <p>
     * 示例 1:
     * <p>
     * 输入: numRows = 5
     * 输出: [[1],[1,1],[1,2,1],[1,3,3,1],[1,4,6,4,1]]
     * 示例 2:
     * <p>
     * 输入: numRows = 1
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/pascals-triangle
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        List<List<Integer>> lists = generate(5);
        for (int i = 0; i < lists.size(); i++) {
            for (int j = 0; j < lists.get(i).size(); j++) {
                System.out.print(lists.get(i).get(j));
            }
            System.out.println();
        }
    }

    /**
     * 执行用时分布1ms击败98.35%
     * 消耗内存分布41.32MB击败31.85%
     */
    public List<List<Integer>> generate(int numRows) {
        if (numRows < 1) return null;
        List<List<Integer>> ret = new ArrayList<>();
        List<Integer> tmp = new ArrayList<>();
        tmp.add(1);
        ret.add(tmp);
        for (int i = 1; i < numRows; i++) {
            tmp = new ArrayList<>();
            tmp.add(1);
            for (int j = 1; j < i; j++) {
                tmp.add(ret.get(i - 1).get(j - 1) + ret.get(i - 1).get(j));
            }
            tmp.add(1);
            ret.add(tmp);
        }
        return ret;
    }

}
