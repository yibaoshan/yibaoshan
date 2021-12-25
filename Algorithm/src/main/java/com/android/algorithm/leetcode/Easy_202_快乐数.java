package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Easy_202_快乐数 {

    /**
     * 编写一个算法来判断一个数 n 是不是快乐数。
     * <p>
     * 「快乐数」定义为：
     * <p>
     * 对于一个正整数，每一次将该数替换为它每个位置上的数字的平方和。
     * 然后重复这个过程直到这个数变为 1，也可能是 无限循环 但始终变不到 1。
     * 如果 可以变为  1，那么这个数就是快乐数。
     * 如果 n 是快乐数就返回 true ；不是，则返回 false 。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/happy-number
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int n = 2;
        List<Integer> list = new ArrayList<>();
        list.add(0);
        list.add(0);
        list.add(1);
//        System.out.println(isHappy(list));
        System.out.println(isHappy(n));
    }

    /**
     * 官解答案
     * 执行结果：通过
     * 执行用时：83 ms, 在所有 Java 提交中击败了90.27%的用户
     * 内存消耗：35.5 MB, 在所有 Java 提交中击败了34.16%的用户
     */
    public boolean isHappy2(int n) {
        HashSet<Integer> seen = new HashSet<>();
        while (n != 1 && !seen.contains(n)) {
            seen.add(n);
            n = next(n);
        }
        return n == 1;
    }

    private int next(int n) {
        int totalSum = 0;
        while (n > 0) {
            int d = n % 10;
            n = n / 10;
            totalSum += d * d;
        }
        return totalSum;
    }

    public boolean isHappy(int n) {
        return isHappy(parse(n), n);
    }

    public boolean isHappy(List<Integer> list, int last) {
        int count = 0, sum = 0;
        for (int val : list) {
            if (val == 1 || val == 0) count++;
            sum += val * val;
        }
        if (count == 1) return true;
        if (sum == last || sum == 4) return false;
        return isHappy(parse(sum), sum);
    }

    public List<Integer> parse(int n) {
        List<Integer> list = new ArrayList<>();
        while (n >= 10) {
            list.add(n % 10);
            n /= 10;
        }
        list.add(n);
        return list;
    }

}
