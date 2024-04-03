package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

public class Medium_93_复原IP地址 {

    /**
     * 有效 IP 地址 正好由四个整数（每个整数位于 0 到 255 之间组成，且不能含有前导 0），整数之间用 '.' 分隔。
     * <p>
     * 例如："0.1.2.201" 和 "192.168.1.1" 是 有效 IP 地址，但是 "0.011.255.245"、"192.168.1.312" 和 "192.168@1.1" 是 无效 IP 地址。
     * 给定一个只包含数字的字符串 s ，用以表示一个 IP 地址，返回所有可能的有效 IP 地址，这些地址可以通过在 s 中插入 '.' 来形成。你不能重新排序或删除 s 中的任何数字。你可以按 任何 顺序返回答案。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/restore-ip-addresses
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
//        String s = "25525511135";
//        String s = "0000";
        String s = "101023";
        List<String> list = restoreIpAddresses(s);
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
        }
    }

    /**
     * 回溯-分割法
     * 执行结果：通过
     * 执行用时：3 ms, 在所有 Java 提交中击败了51.67%的用户
     * 内存消耗：38.7 MB, 在所有 Java 提交中击败了26.47%的用户
     */
    public List<String> restoreIpAddresses(String s) {
        dfs(s, 0);
        return res;
    }

    private List<String> res = new ArrayList<>();
    private Deque<String> deque = new ArrayDeque<>();

    private void dfs(String s, int start) {
        if (deque.size() == 4 && start < s.length()) return;
        if (start >= s.length()) {
            if (deque.size() == 4) {
                StringBuilder stringBuilder = new StringBuilder();
                for (String value : deque) {
                    stringBuilder.append(value);
                    stringBuilder.append(".");
                }
                stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
                res.add(stringBuilder.toString());
            }
            return;
        }
        for (int i = start; i < s.length(); i++) {
            String substring = s.substring(start, i + 1);
            if (isIpAddress(substring)) {
                deque.addLast(substring);
            } else continue;
            dfs(s, i + 1);
            deque.removeLast();
        }
    }

    private boolean isIpAddress(String s) {
        try {
            int ip = Integer.parseInt(s);
            if (ip == 0 && s.length() > 1) return false;
            if (ip > 0 && s.length() > 1 && s.charAt(0) == '0') return false;
            if (ip >= 0 && ip <= 255) return true;
        } catch (Exception e) {
            return false;
        }
        return false;
    }


}
