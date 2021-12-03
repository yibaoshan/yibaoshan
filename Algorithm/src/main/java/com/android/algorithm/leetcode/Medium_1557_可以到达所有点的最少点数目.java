package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Medium_1557_可以到达所有点的最少点数目 {

    /**
     * 给你一个 有向无环图 ， n 个节点编号为 0 到 n-1 ，以及一个边数组 edges ，其中 edges[i] = [fromi, toi] 表示一条从点  fromi 到点 toi 的有向边。
     * <p>
     * 找到最小的点集使得从这些点出发能到达图中所有点。题目保证解存在且唯一。
     * <p>
     * 你可以以任意顺序返回这些节点编号。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/minimum-number-of-vertices-to-reach-all-nodes
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {

    }

    /**
     * hash法求交集，简单粗暴
     * 执行结果：通过
     * 执行用时：53 ms, 在所有 Java 提交中击败了5.02%的用户
     * 内存消耗：89 MB, 在所有 Java 提交中击败了5.02%的用户
     */
    public List<Integer> findSmallestSetOfVertices(int n, List<List<Integer>> edges) {
        List<Integer> res = new ArrayList<>();
        HashSet<Integer> hashSet = new HashSet<>();
        HashSet<Integer> hash = new HashSet<>();
        for (int i = 0; i < edges.size(); i++) {
            hash.add(edges.get(i).get(0));
            hashSet.add(edges.get(i).get(1));
        }
        for (Integer a : hash) {
            if (!hashSet.contains(a)) {
                res.add(a);
            }
        }
        return res;
    }

}
