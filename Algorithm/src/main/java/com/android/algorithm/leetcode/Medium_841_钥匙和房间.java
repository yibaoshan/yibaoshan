package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Medium_841_钥匙和房间 {

    /**
     * 有 n 个房间，房间按从 0 到 n - 1 编号。最初，除 0 号房间外的其余所有房间都被锁住。你的目标是进入所有的房间。然而，你不能在没有获得钥匙的时候进入锁住的房间。
     * <p>
     * 当你进入一个房间，你可能会在里面找到一套不同的钥匙，每把钥匙上都有对应的房间号，即表示钥匙可以打开的房间。你可以拿上所有钥匙取解锁其他房间。
     * <p>
     * 给你一个数组 rooms 其中 rooms[i] 是你进入 i 号房间可以获得的钥匙集合。如果能进入 所有 房间返回 true，否则返回 false。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/keys-and-rooms
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        //[[4],[3],[],[2,5,7],[1],[],[8,9],[],[],[6]]
        List<List<Integer>> rooms = new ArrayList<>();

        add(rooms, new int[]{4});
        add(rooms, new int[]{3});
        add(rooms, new int[]{});
        add(rooms, new int[]{2, 5, 7});
        add(rooms, new int[]{1});
        add(rooms, new int[]{});
        add(rooms, new int[]{8, 9});
        add(rooms, new int[]{});
        add(rooms, new int[]{});
        add(rooms, new int[]{6});

        System.out.println(canVisitAllRooms(rooms));
    }

    private void add(List<List<Integer>> rooms, int[] add) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < add.length; i++) {
            list.add(add[i]);
        }
        rooms.add(list);
    }

    /**
     * 利用队列来记录将来访问的房间
     * 执行结果：通过
     * 执行用时：2 ms, 在所有 Java 提交中击败了38.03%的用户
     * 内存消耗：38.5 MB, 在所有 Java 提交中击败了21.71%的用户
     */
    public boolean canVisitAllRooms(List<List<Integer>> rooms) {
        if (rooms == null || rooms.size() < 2) return false;
        int n = rooms.size();
        boolean[] res = new boolean[n];
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < rooms.get(0).size(); i++) {
            queue.offer(rooms.get(0).get(i));
        }
        while (!queue.isEmpty()) {
            int count = queue.size();
            while (count > 0) {
                Integer integer = queue.poll();
                if (integer != null && !res[integer]) {
                    res[integer] = true;
                    for (int i = 0; i < rooms.get(integer).size(); i++) {
                        queue.offer(rooms.get(integer).get(i));
                    }
                }
                count--;
            }
        }
        for (int i = 1; i < res.length; i++) if (!res[i]) return false;
        return true;
    }

}
