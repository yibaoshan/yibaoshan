package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

public class Hard_1036_逃离大迷宫 {

    /**
     * 在一个 106 x 106 的网格中，每个网格上方格的坐标为 (x, y) 。
     * <p>
     * 现在从源方格 source = [sx, sy] 开始出发，意图赶往目标方格 target = [tx, ty] 。数组 blocked 是封锁的方格列表，其中每个 blocked[i] = [xi, yi] 表示坐标为 (xi, yi) 的方格是禁止通行的。
     * <p>
     * 每次移动，都可以走到网格中在四个方向上相邻的方格，只要该方格 不 在给出的封锁列表 blocked 上。同时，不允许走出网格。
     * <p>
     * 只有在可以通过一系列的移动从源方格 source 到达目标方格 target 时才返回 true。否则，返回 false。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/escape-a-large-maze
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */
    @Test
    public void main() {

    }

    int EDGE = (int) 1e6, MAX = (int) 1e5;
    long BASE = 131L;
    Set<Long> set = new HashSet<>();
    int[][] dir = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    /**
     * 评论区答案
     *
     * 作者：AC_OIer
     * 链接：https://leetcode-cn.com/problems/escape-a-large-maze/solution/gong-shui-san-xie-bfs-gei-ding-zhang-ai-8w63o/
     * 来源：力扣（LeetCode）
     * 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
     */
    public boolean isEscapePossible(int[][] blocked, int[] source, int[] target) {
        for (int[] p : blocked) set.add(p[0] * BASE + p[1]);
        int n = blocked.length;
        MAX = n * (n - 1) / 2; // 可直接使用 1e5
        return check(source, target) && check(target, source);
    }

    boolean check(int[] a, int[] b) {
        Set<Long> vis = new HashSet<>();
        Deque<int[]> d = new ArrayDeque<>();
        d.addLast(a);
        vis.add(a[0] * BASE + a[1]);
        while (!d.isEmpty() && vis.size() <= MAX) {
            int[] poll = d.pollFirst();
            int x = poll[0], y = poll[1];
            if (x == b[0] && y == b[1]) return true;
            for (int[] di : dir) {
                int nx = x + di[0], ny = y + di[1];
                if (nx < 0 || nx >= EDGE || ny < 0 || ny >= EDGE) continue;
                long hash = nx * BASE + ny;
                if (set.contains(hash)) continue;
                if (vis.contains(hash)) continue;
                d.addLast(new int[]{nx, ny});
                vis.add(hash);
            }
        }
        return vis.size() > MAX;
    }

}
