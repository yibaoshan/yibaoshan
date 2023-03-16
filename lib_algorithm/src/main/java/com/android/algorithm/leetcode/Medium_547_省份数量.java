package com.android.algorithm.leetcode;

import org.junit.Test;

public class Medium_547_省份数量 {

    /**
     * 有 n 个城市，其中一些彼此相连，另一些没有相连。如果城市 a 与城市 b 直接相连，且城市 b 与城市 c 直接相连，那么城市 a 与城市 c 间接相连。
     * <p>
     * 省份 是一组直接或间接相连的城市，组内不含其他没有相连的城市。
     * <p>
     * 给你一个 n x n 的矩阵 isConnected ，其中 isConnected[i][j] = 1 表示第 i 个城市和第 j 个城市直接相连，而 isConnected[i][j] = 0 表示二者不直接相连。
     * <p>
     * 返回矩阵中 省份 的数量。
     * <p>
     * <p>
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/number-of-provinces
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
//        [],[],[0,0,1]
        int[][] isConnected = new int[][]{new int[]{1, 1, 0}, new int[]{1, 1, 0}, new int[]{0, 0, 1}};
        System.out.println(findCircleNum(isConnected));
    }

    /**
     * 广度优先遍历
     * 执行结果：通过
     * 执行用时：1 ms, 在所有 Java 提交中击败了94.81%的用户
     * 内存消耗：39.2 MB, 在所有 Java 提交中击败了79.12%的用户
     */
    public int findCircleNum(int[][] isConnected) {
        boolean[] visited = new boolean[isConnected.length];
        int ret = 0;
        for (int i = 0; i < isConnected.length; ++i) {
            if (!visited[i]) {
                dfs(isConnected, visited, i);
                ret++;
            }
        }
        return ret;
    }

    private void dfs(int[][] m, boolean[] visited, int i) {
        for (int j = 0; j < m.length; ++j) {
            if (m[i][j] == 1 && !visited[j]) {
                visited[j] = true;
                dfs(m, visited, j);
            }
        }
    }

}
