package com.android.algorithm.contest;

import com.android.algorithm.leetcode.LeetCodeUtil;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class WeeklyContest_284 {

    @Test
    public void test1() {
        int[] nums = new int[]{222, 151, 842, 244, 103, 736, 219, 432, 565, 216, 36, 198, 10, 367, 778, 111, 307, 460, 92, 622, 750, 36, 559, 983, 782, 432, 312, 111, 676, 179, 44, 86, 766, 371, 746, 905, 850, 170, 892, 80, 449, 932, 295, 875, 259, 556, 730, 441, 153, 869};
        int key = 153;
        int k = 19;
        List<Integer> list = findKDistantIndices(nums, key, k);
        System.out.println(Arrays.toString(new List[]{list}));
        System.out.println("[29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49]");
    }

    public List<Integer> findKDistantIndices(int[] nums, int key, int k) {
        HashSet<Integer> hashSet = new HashSet<>();
        List<Integer> keyList = new ArrayList<>();
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == key) keyList.add(i);
        }
        for (int i = 0; i < nums.length; i++) {
            for (int j = 0; j < keyList.size(); j++) {
                if (Math.abs(i - keyList.get(j)) <= k && nums[keyList.get(j)] == key) hashSet.add(i);
            }
        }
        List<Integer> res = new ArrayList<>(hashSet);
        Collections.sort(res);
        return res;
    }

    @Test
    public void test2() {
        int n = 9;

//        int[][] artifacts = LeetCodeUtil.parseStringToIntTwoDArray("[[0,2,0,5],[0,1,1,1],[3,0,3,3],[4,4,4,4],[2,1,2,4]]");
        int[][] artifacts = LeetCodeUtil.parseStringToIntTwoDArray("[[4,5,5,5],[5,1,7,1],[1,8,3,8],[1,1,1,2],[0,5,3,5],[6,2,7,2],[7,5,7,6],[6,4,6,6],[2,7,5,7],[0,2,0,2],[7,7,8,8],[3,1,4,2],[2,3,3,3],[5,3,7,3],[8,4,8,4],[2,6,5,6],[8,1,8,2],[4,8,4,8],[1,0,4,0],[6,8,6,8],[1,3,1,4],[0,7,0,8],[0,3,0,4],[0,6,0,6]]");
//        int[][] dig = LeetCodeUtil.parseStringToIntTwoDArray("[[0,2],[0,3],[0,4],[2,0],[2,1],[2,2],[2,5],[3,0],[3,1],[3,3],[3,4],[4,0],[4,3],[4,5],[5,0],[5,1],[5,2],[5,4],[5,5]]");
        int[][] dig = LeetCodeUtil.parseStringToIntTwoDArray("[[0,3],[0,4],[8,5],[8,6],[8,7],[0,8],[8,8],[0,6],[1,1],[1,8],[2,0],[2,2],[2,3],[2,4],[2,5],[2,8],[3,2],[3,3],[3,4],[3,5],[3,6],[3,7],[4,0],[4,3],[4,4],[4,6],[4,7],[5,1],[5,2],[5,6],[5,8],[6,0],[6,2],[6,4],[6,5],[6,6],[7,0],[7,1],[7,4],[7,5],[7,7]]");
        System.out.println(digArtifacts(n, artifacts, dig));
        System.out.println(digArtifacts2(n, artifacts, dig));
    }

    /**
     * 评论区答案，双百通过
     * */
    public int digArtifacts3(int n, int[][] artifacts, int[][] dig) {
        int count = 0;
        HashMap<Integer, HashMap<Integer, Integer>> map = new HashMap<>();
        for (int i = 0; i < dig.length; i++) {
            if (map.containsKey(dig[i][0])) {
                map.get(dig[i][0]).put(dig[i][1], 0);
            } else {
                HashMap<Integer, Integer> temp = new HashMap<>();
                temp.put(dig[i][1], 0);
                map.put(dig[i][0], temp);
            }
        }
        for (int i = 0; i < artifacts.length; i++) {
            count += checkCoverage(artifacts, i, map);
        }
        return count;
    }

    public int checkCoverage(int[][] artifacts, int row, HashMap<Integer, HashMap<Integer, Integer>> map) {
        for (int i = artifacts[row][0]; i <= artifacts[row][2]; i++) {
            for (int j = artifacts[row][1]; j <= artifacts[row][3]; j++) {
                if (!map.containsKey(i)) {
                    return 0;
                } else {
                    if (!map.get(i).containsKey(j)) {
                        return 0;
                    }
                }
            }
        }
        return 1;
    }

    public int digArtifacts(int n, int[][] artifacts, int[][] dig) {
        System.err.println("工件矩阵：");
        for (int i = 0; i < artifacts.length; i++) {
            System.out.println(Arrays.toString(artifacts[i]));
        }
        System.out.println();
        System.err.println("工作矩阵：");
        for (int i = 0; i < dig.length; i++) {
            System.out.println(Arrays.toString(dig[i]));
        }
        System.out.println();
        Des[][] matrix = new Des[n][n];
        for (int i = 0; i < artifacts.length; i++) {
            int[] artifact = artifacts[i];
            if (artifact[0] == artifact[2] && artifact[1] == artifact[3]) {
                matrix[artifact[0]][artifact[1]] = new Des(i, 1);
            } else if (artifact[0] != artifact[2] && artifact[1] != artifact[3]) {
                matrix[artifact[0]][artifact[1]] = new Des(i, 0.25f);
                matrix[artifact[0]][artifact[1] + 1] = new Des(i, 0.25f);
                matrix[artifact[2]][artifact[3]] = new Des(i, 0.25f);
                matrix[artifact[2]][artifact[3] - 1] = new Des(i, 0.25f);
            } else {
                if (artifact[0] == artifact[2]) {
                    for (int j = artifact[1]; j <= artifact[3]; j++) {
                        int distance = artifact[3] - artifact[1];
                        float unit = 0.5f;
                        if (distance == 3) unit = 0.25f;
                        else if (distance == 2) unit = 0.34f;
                        matrix[artifact[0]][j] = new Des(i, unit);
                    }
                } else {
                    for (int j = artifact[0]; j <= artifact[2]; j++) {
                        int distance = artifact[2] - artifact[0];
                        float unit = 0.5f;
                        if (distance == 3) unit = 0.25f;
                        else if (distance == 2) unit = 0.34f;
                        matrix[j][artifact[1]] = new Des(i, unit);
                    }
                }
            }
        }
        for (int i = 0; i < matrix.length; i++) {
            System.out.println(Arrays.toString(matrix[i]));
        }
        HashMap<Integer, Float> hashMap = new HashMap<>();
        int res = 0;
        for (int i = 0; i < dig.length; i++) {
            int[] work = dig[i];
            Des des = matrix[work[0]][work[1]];
            if (des == null) continue;
            if (hashMap.containsKey(des.id)) {
                float val = hashMap.get(des.id);
                hashMap.put(des.id, val + des.unit);
                if (val + des.unit > 1.0) res++;
            } else hashMap.put(des.id, des.unit);
        }
        List<Float> values = new ArrayList<>(hashMap.values());
        for (float f : values) if (f >= 1.0f) res++;
        return res;
    }

    public int digArtifacts2(int n, int[][] artifacts, int[][] dig) {
        Des[][] matrix = new Des[n][n];
        for (int i = 0; i < artifacts.length; i++) {
            int[] artifact = artifacts[i];
            if (artifact[0] == artifact[2] && artifact[1] == artifact[3]) {
                matrix[artifact[0]][artifact[1]] = new Des(i, 1);
            } else if (artifact[0] != artifact[2] && artifact[1] != artifact[3]) {
                matrix[artifact[0]][artifact[1]] = new Des(i, 0.25f);
                matrix[artifact[0]][artifact[1] + 1] = new Des(i, 0.25f);
                matrix[artifact[2]][artifact[3]] = new Des(i, 0.25f);
                matrix[artifact[2]][artifact[3] - 1] = new Des(i, 0.25f);
            } else {
                if (artifact[0] == artifact[2]) {
                    for (int j = artifact[1]; j <= artifact[3]; j++) {
                        int distance = artifact[3] - artifact[1];
                        float unit = 0.5f;
                        if (distance == 3) unit = 0.25f;
                        else if (distance == 2) unit = 0.34f;
                        matrix[artifact[0]][j] = new Des(i, unit);
                    }
                } else {
                    for (int j = artifact[0]; j <= artifact[2]; j++) {
                        int distance = artifact[2] - artifact[0];
                        float unit = 0.5f;
                        if (distance == 3) unit = 0.25f;
                        else if (distance == 2) unit = 0.34f;
                        matrix[j][artifact[1]] = new Des(i, unit);
                    }
                }
            }
        }
        HashMap<Integer, Float> hashMap = new HashMap<>();
        int res = 0;
        for (int i = 0; i < dig.length; i++) {
            int[] work = dig[i];
            Des des = matrix[work[0]][work[1]];
            if (des == null) continue;
            if (hashMap.containsKey(des.id)) {
                float val = hashMap.get(des.id);
                hashMap.put(des.id, val + des.unit);
                if (val + des.unit > 1.0) res++;
            } else hashMap.put(des.id, des.unit);
        }
        List<Float> values = new ArrayList<>(hashMap.values());
        for (float f : values) if (f >= 1.0f) res++;
        return res;
    }

    private class Des {

        int id;
        float unit;

        public Des(int id, float unit) {
            this.id = id;
            this.unit = unit;
        }

        @Override
        public String toString() {
            return "{" +
                    "id='" + id + '\'' +
                    ", unit=" + unit +
                    '}';
        }
    }

    @Test
    public void test3() {
        int[] nums = new int[]{91, 98, 17, 79, 15, 55, 47, 86, 4, 5, 17, 79, 68, 60, 60, 31, 72, 85, 25, 77, 8, 78, 40, 96, 76, 69, 95, 2, 42, 87, 48, 72, 45, 25, 40, 60, 21, 91, 32, 79, 2, 87, 80, 97, 82, 94, 69, 43, 18, 19, 21, 36, 44, 81, 99};
        int k = 1;
//        [2,28,6,4,3]
//        1000000000
        System.out.println(maximumTop(nums, k));
    }

    public int maximumTop(int[] nums, int k) {
        if (nums == null || nums.length == 0) return -1;
        if (nums.length == 1 && k % 2 == 1) return -1;
        if (k == 1) return nums.length > 1 ? nums[1] : -1;
        if (k == 2 && nums.length > 2) return Math.max(nums[0], nums[2]);
        int[] range = Arrays.copyOfRange(nums, 0, Math.min(nums.length, k));
        Arrays.sort(range);
        if (k >= nums.length) return range[range.length - 1];
        return Math.max(range[range.length - 1], nums[range.length]);
    }

    @Test
    public void test4() {
    }

}
