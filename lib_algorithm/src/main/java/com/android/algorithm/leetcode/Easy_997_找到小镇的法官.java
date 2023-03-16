package com.android.algorithm.leetcode;

import org.junit.Test;

public class Easy_997_找到小镇的法官 {

    /**
     * 在一个小镇里，按从 1 到 n 为 n 个人进行编号。传言称，这些人中有一个是小镇上的秘密法官。
     * <p>
     * 如果小镇的法官真的存在，那么：
     * <p>
     * 小镇的法官不相信任何人。
     * 每个人（除了小镇法官外）都信任小镇的法官。
     * 只有一个人同时满足条件 1 和条件 2 。
     * 给定数组 trust，该数组由信任对 trust[i] = [a, b] 组成，表示编号为 a 的人信任编号为 b 的人。
     * <p>
     * 如果小镇存在秘密法官并且可以确定他的身份，请返回该法官的编号。否则，返回 -1。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/find-the-town-judge
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int[][] trust = new int[][]{new int[]{1, 2}};
        System.out.println(findJudge(2, trust));
    }

    /**
     * 执行结果：通过
     * 执行用时：0 ms, 在所有 Java 提交中击败了100.00%的用户
     * 内存消耗：39.4 MB, 在所有 Java 提交中击败了35.80%的用户
     */
    /**
     * 1. 小镇的法官不相信任何人。 （可理解为信任人数为0）
     * 2. 每个人（除了小镇法官外）都信任小镇的法官。 （可理解为获得n-1张选票）
     * 3. 只有一个人同时满足条件 1 和条件 2 。 (注意！！！这个条件可以忽略，满足1和2必然满足3)
     * 其中，n是小镇的人数
     * <p>
     * 反证：
     * 假设有2个人同时满足1和2
     * 那么2个人不相信任何人
     * 2个人最多都只能获得n-2票
     * 与条件2矛盾（条件2需获得n-1张选票）
     */
    public int findJudge(int n, int[][] trust) {
        //新建长度为n+1的数组，代表序号从1~n每个人起始被相信次数为0
        int[] res = new int[n + 1];
        //遍历数组
        for (int i = 0; i < trust.length; i++) {
            //如果有人相信，该序号处人被相信次数就+1
            res[trust[i][1]] = res[trust[i][1]] + 1;
            //如果有人相信了别人，就设置该序号处人被相信次数为-1，方便最后遍历结果作区分
            res[trust[i][0]] = -1;
        }
        //遍历最后结果
        for (int i = 1; i < res.length; i++) {
            //如果被相信数为总人数-1，则返回序号
            if (res[i] == n - 1) {
                return i;
            }
        }
        //到这一步，代表遍历完没有人次数到n-1，返回-1
        return -1;

    }

}
