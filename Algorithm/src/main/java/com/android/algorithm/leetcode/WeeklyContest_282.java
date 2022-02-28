package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class WeeklyContest_282 {

    @Test
    public void test1() {
        String[] strings = new String[]{
                "pay", "attention", "practice", "attend"
        };
        String pre = "at";
        System.out.println(prefixCount(strings, pre));
    }

    public int prefixCount(String[] words, String pref) {
        if (words == null || pref == null || words.length == 0 || pref.length() == 0) return 0;
        int cnt = 0;
        for (int i = 0; i < words.length; i++) {
            if (words[i].indexOf(pref) == 0) cnt++;
        }
        return cnt;
    }

    @Test
    public void test2() {
//        String s = "leetcode";
        String s = "night";
//        String t = "coats";
        String t = "thing";
        System.out.println(minSteps(s, t));
    }

    public int minSteps(String s, String t) {
        if (s == null && t == null) return 0;
        if (s == null || s.length() == 0) return t.length();
        if (t == null || t.length() == 0) return s.length();
        HashMap<Character, Integer> hashMapS = new HashMap<>();
        HashMap<Character, Integer> hashMapT = new HashMap<>();
        for (int i = 0; i < s.length(); i++) {
            if (hashMapS.containsKey(s.charAt(i))) {
                hashMapS.put(s.charAt(i), hashMapS.get(s.charAt(i)) + 1);
            } else hashMapS.put(s.charAt(i), 1);
        }
        for (int i = 0; i < t.length(); i++) {
            if (hashMapT.containsKey(t.charAt(i))) {
                hashMapT.put(t.charAt(i), hashMapT.get(t.charAt(i)) + 1);
            } else hashMapT.put(t.charAt(i), 1);
        }
        int cnt = 0;
        Iterator<Map.Entry<Character, Integer>> iterator = hashMapS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Character, Integer> cur = iterator.next();
            if (hashMapT.containsKey(cur.getKey())) {
                cnt += Math.abs(hashMapT.get(cur.getKey()) - cur.getValue());
            } else cnt += cur.getValue();
        }
        iterator = hashMapT.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Character, Integer> cur = iterator.next();
            if (!hashMapS.containsKey(cur.getKey())) cnt += cur.getValue();
        }
        return cnt;
    }

    @Test
    public void test3() {
        int[] time = new int[]{5, 10, 10};
        int total = 9;
        System.out.println(minimumTime(time, total));
        System.out.println(minimumTime3(time, total));
    }

    /**
     * 纯模拟，超时
     */
    public long minimumTime(int[] time, int totalTrips) {
        Arrays.sort(time);
        long cnt = time[0], sum = 0;
        while (sum < totalTrips) {
            sum = 0;
            for (int i = 0; i < time.length; i++) {
                if (time[i] > cnt) break;
                sum += cnt / time[i];
            }
            if (sum >= totalTrips) return cnt;
            cnt++;
        }
        return cnt;
    }

    public long minimumTime2(int[] time, int totalTrips) {
        Arrays.sort(time);
        long cnt = time[0], sum = 0;
        while (sum < totalTrips) {
            sum = 0;
            for (int i = 0; i < time.length; i++) {
                if (time[i] > cnt) break;
                sum += cnt / time[i];
            }
            if (sum >= totalTrips) return cnt;
            cnt++;
        }
        return cnt;
    }

    public long minimumTime3(int[] time, int totalTrips) {
        Arrays.sort(time);
        long left = 0;
        // 记录当前最大完成旅途的时间
        long right = 1L*  time[0] * totalTrips ;
        // 在最小时间和最大时间之间搜索符合条件的时间
        while (left < right ){
            long mid = left + (right - left) /2;
            // 记录当前完成旅途的车
            long trips = 0;
            // 遍历每个车次需要完成的时间
            for(int t : time){
                if(mid < t){
                    break;
                }
                // 记录当前时间能完成的趟数
                trips += mid / t;
            }
            // 如果当前完成的车次已经到达了完成的次数则缩小范围 搜索前面时间范围
            if(trips >= totalTrips){
                right = mid;
            } else {
                // 反之搜索后面时间范围
                left = mid + 1;
            }
        }
        return left;
    }

    @Test
    public void test4() {
    }

}
