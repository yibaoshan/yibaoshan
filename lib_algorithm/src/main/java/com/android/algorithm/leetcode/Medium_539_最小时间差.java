package com.android.algorithm.leetcode;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Medium_539_最小时间差 {

    /**
     * 给定一个 24 小时制（小时:分钟 "HH:MM"）的时间列表，找出列表中任意两个时间的最小时间差并以分钟数表示。
     * <p>
     * 示例 1：
     * <p>
     * 输入：timePoints = ["23:59","00:00"]
     * 输出：1
     * 示例 2：
     * <p>
     * 输入：timePoints = ["00:00","23:59","00:00"]
     * 输出：0
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/minimum-time-difference
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() throws ParseException {
        List<String> list = new ArrayList<>();
        list.add("12:12");
        list.add("00:13");
        System.out.println(findMinDifference(list));
    }

    public int findMinDifference(List<String> timePoints) {
        int[] minutes = new int[timePoints.size()];
        for (int i = 0; i < timePoints.size(); i++) {
            int minute = Integer.parseInt(timePoints.get(i).substring(0, 2)) * 60 + Integer.parseInt(timePoints.get(i).substring(3));
            minutes[i] = minute;
        }
        Arrays.sort(minutes);
        int min = Integer.MAX_VALUE;
        for (int i = 1; i < minutes.length; i++) {
            min = Math.min(min, minutes[i] - minutes[i - 1]);
        }
        return min;
//        return Math.min(min, minutes[0] + 1440 - minutes[minutes.length - 1]);
    }

    private int getMinDiff(String time1, String time2) {
        if (time1.equals(time2)) return 0;
        int index1 = time1.indexOf(":");
        int index2 = time2.indexOf(":");
        if (time1.substring(0, index1).equals("00")) time1 = "24" + time1.substring(index1);
        if (time2.substring(0, index2).equals("00")) time2 = "24" + time2.substring(index2);
        int hour1 = Integer.parseInt(time1.substring(0, index1));
        int hour2 = Integer.parseInt(time2.substring(0, index2));
        int minute1 = Integer.parseInt(time1.substring(index1 + 1));
        int minute2 = Integer.parseInt(time2.substring(index2 + 1));

        int hour = Math.min(Math.abs(hour1 - 1 - hour2), Math.abs(hour2 - 1 - hour1));
        int minute = 60 - minute1 - minute2;
        return hour > 0 ? hour * 60 + minute : minute;
    }

    private int getMinDiff2(String time1, String time2) {
        if (time1.equals(time2)) return 0;
        if (time1.substring(0, time1.indexOf(":")).equals("00")) time1 = "24" + time1.substring(time1.indexOf(":"));
        if (time2.substring(0, time2.indexOf(":")).equals("00")) time2 = "24" + time2.substring(time2.indexOf(":"));
        SimpleDateFormat dfs = new SimpleDateFormat("HH:mm");
        long between = 0;
        try {
            Date begin = dfs.parse(time1);
            Date end = dfs.parse(time2);
            long diff1 = end.getTime() - begin.getTime();
            long diff2 = begin.getTime() - end.getTime();
            System.out.println(diff1 + "," + diff2);
            between = Math.min(end.getTime() - begin.getTime(), begin.getTime() - end.getTime()) / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (int) Math.abs(between / 60);
    }

}
