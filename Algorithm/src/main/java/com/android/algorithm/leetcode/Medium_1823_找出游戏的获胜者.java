package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.LinkedList;
import java.util.Queue;

public class Medium_1823_找出游戏的获胜者 {

    /**
     * 共有 n 名小伙伴一起做游戏。小伙伴们围成一圈，按 顺时针顺序 从 1 到 n 编号。确切地说，从第 i 名小伙伴顺时针移动一位会到达第 (i+1) 名小伙伴的位置，其中 1 <= i < n ，从第 n 名小伙伴顺时针移动一位会回到第 1 名小伙伴的位置。
     * <p>
     * 游戏遵循如下规则：
     * <p>
     * 从第 1 名小伙伴所在位置 开始 。
     * 沿着顺时针方向数 k 名小伙伴，计数时需要 包含 起始时的那位小伙伴。逐个绕圈进行计数，一些小伙伴可能会被数过不止一次。
     * 你数到的最后一名小伙伴需要离开圈子，并视作输掉游戏。
     * 如果圈子中仍然有不止一名小伙伴，从刚刚输掉的小伙伴的 顺时针下一位 小伙伴 开始，回到步骤 2 继续执行。
     * 否则，圈子中最后一名小伙伴赢得游戏。
     * 给你参与游戏的小伙伴总数 n ，和一个整数 k ，返回游戏的获胜者。
     * <p>
     * <p>
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/find-the-winner-of-the-circular-game
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        Queue<Integer> queue = new LinkedList<>();
        queue.add(1);
        queue.add(2);
        queue.add(3);
        findTheWinner(6, 5);
    }

    public int findTheWinner(int n, int k) {
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 1; i <= n; i++) queue.add(i);
        for (int i = 1; i < n; i++) {
            for (int j = 1; j < k; j++) queue.add(queue.poll());//重新排队
            queue.poll();//干掉最后一个人，他输了
        }
        return queue.poll();
    }

    /**
     * 评论区答案，动态规划解法
     */
    public int findTheWinner2(int n, int k) {
        int dp = 1;
        for (int i = 0; i < n; i++) {
            dp = (dp + k - 1) % (i + 1) + 1;
        }
        return dp;
    }

    /**
     * 评论区答案，递归解法
     */
    int f(int n, int m) {
        if (n == 1) return n;
        return (f(n - 1, m) + m - 1) % n + 1;
    }

}
