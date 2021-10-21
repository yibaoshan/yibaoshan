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
     * 执行结果：通过
     * 执行用时：0 ms, 在所有 Java 提交中击败了100.00%的用户
     * 内存消耗：36.4 MB, 在所有 Java 提交中击败了22.44%的用户
     */
    public List<List<Integer>> generate(int numRows) {
        if (numRows < 1) return null;
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> oneIntegers = new ArrayList<>();
        List<Integer> last = new ArrayList<>();
        oneIntegers.add(1);
        result.add(oneIntegers);
        int count = 2;
        while (count <= numRows) {
            List<Integer> once = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                if (i == 0) once.add(1);
                else if (i + 1 == count) once.add(1);
                else {
                    once.add(last.get(i - 1) + last.get(i));
                }
            }
            last = once;
            result.add(once);
            count++;
        }
        return result;
    }

}
