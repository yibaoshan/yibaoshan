package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Easy_350_两个数组的交集2 {

    /**
     * 给定两个数组，编写一个函数来计算它们的交集。
     * <p>
     *  
     * <p>
     * 示例 1：
     * <p>
     * 输入：nums1 = [1,2,2,1], nums2 = [2,2]
     * 输出：[2,2]
     * 示例 2:
     * <p>
     * 输入：nums1 = [4,9,5], nums2 = [9,4,9,8,4]
     * 输出：[4,9]
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/intersection-of-two-arrays-ii
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     * <p>
     * 注：不要求顺序、不要求连续
     *
     * 进阶：
     *
     * 1. 如果给定的数组已经排好序呢？你将如何优化你的算法？
     * 2. 如果 nums1 的大小比 nums2 小很多，哪种方法更优？
     * 3. 如果 nums2 的元素存储在磁盘上，内存是有限的，并且你不能一次加载所有的元素到内存中，你该怎么办？
     *
     * 解答进阶：
     * 1. 如果已经排好序，用官解2的双指针可以解决：
     * 两个指针都从起点开始，一轮比较后较小的数字指针向后移一位，相同则记录下来
     *
     * 2. 官解1的hash显然更合适
     *
     * 3. 这个问题值得讨论一下，官解建议使用1方法也就是加载短数组到内存，再遍历长数组，记录相同项即可
     * 精选答案中有指出，若内存不足以放入短数组，那就只能使用外部排序，使用归并排序写入磁盘，再把小文件合并文大文件，依次读出使用官解2的双指针即可
     */

    @Test
    public void main() {
        int[] nums1 = new int[]{4, 9, 5};
        int[] nums2 = new int[]{9, 4, 9, 8, 4};
        System.out.println(Arrays.toString(intersect(nums1, nums2)));
    }

    /**
     * 看官方解题思路然后自己实现的
     * 既然题目不要求连续和不要求顺序的话，用hash显然是比较简单的
     * 1. 遍历短数组放入到map中，相同数字引用+1
     * 2. 遍历长数组，相同则引用-1
     * 执行结果：通过
     * 执行用时：2 ms, 在所有 Java 提交中击败了77.49%的用户
     * 内存消耗：38.7 MB, 在所有 Java 提交中击败了17.98%的用户
     */
    public int[] intersect(int[] nums1, int[] nums2) {
        int[] shortArray = nums1;
        int[] longArray = nums2;
        if (nums1.length > nums2.length) {
            shortArray = nums2;
            longArray = nums1;
        }
        HashMap<Integer, Integer> statistics = new HashMap<>();
        for (int num : shortArray) {
            if (statistics.containsKey(num)) {
                int count = statistics.get(num);
                statistics.put(num, ++count);
            } else {
                statistics.put(num, 1);
            }
        }
        ArrayList<Integer> arrayList = new ArrayList<>(shortArray.length);
        for (int num : longArray) {
            if (statistics.containsKey(num)) {
                int count = statistics.get(num);
                if (count <= 0) continue;
                statistics.put(num, --count);
                arrayList.add(num);
            }
        }
        int[] result = new int[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            result[i] = arrayList.get(i);
        }
        return result;
    }

}
