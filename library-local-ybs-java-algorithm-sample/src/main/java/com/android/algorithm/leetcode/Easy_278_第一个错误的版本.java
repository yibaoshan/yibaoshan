package com.android.algorithm.leetcode;

import org.junit.Test;

public class Easy_278_第一个错误的版本 {

    /**
     * 你是产品经理，目前正在带领一个团队开发新的产品。不幸的是，你的产品的最新版本没有通过质量检测。由于每个版本都是基于之前的版本开发的，所以错误的版本之后的所有版本都是错的。
     * <p>
     * 假设你有 n 个版本 [1, 2, ..., n]，你想找出导致之后所有版本出错的第一个错误的版本。
     * <p>
     * 你可以通过调用 bool isBadVersion(version) 接口来判断版本号 version 是否在单元测试中出错。实现一个函数来查找第一个错误的版本。你应该尽量减少对调用 API 的次数。
     * <p>
     *  
     * 示例 1：
     * <p>
     * 输入：n = 5, bad = 4
     * 输出：4
     * 解释：
     * 调用 isBadVersion(3) -> false
     * 调用 isBadVersion(5) -> true
     * 调用 isBadVersion(4) -> true
     * 所以，4 是第一个错误的版本。
     * 示例 2：
     * <p>
     * 输入：n = 1, bad = 1
     * 输出：1
     *  
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/first-bad-version
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        System.out.println(firstBadVersion(2126753390));
    }

    /**
     * 执行结果：通过
     * 执行用时：16 ms, 在所有 Java 提交中击败了19.63%的用户
     * 内存消耗：35 MB, 在所有 Java 提交中击败了78.76%的用户
     */
    public int firstBadVersion(int n) {
        int left = 1, right = n, mid = right / 2;
        while (left < right) {
            if (isBadVersion(mid)) {
                right = mid;
            } else {
                left = mid + 1;
            }
            mid = left + (right - left) / 2;
        }
        return left;
    }

    public boolean isBadVersion(int version) {
        if (version >= 1702766719) return true;
        return false;
    }

}
