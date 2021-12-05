package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;

public class Medium_973_最接近原点的K个点 {

    /**
     * 我们有一个由平面上的点组成的列表 points。需要从中找出 K 个距离原点 (0, 0) 最近的点。
     * <p>
     * （这里，平面上两点之间的距离是欧几里德距离。）
     * <p>
     * 你可以按任何顺序返回答案。除了点坐标的顺序之外，答案确保是唯一的。
     * <p>
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/k-closest-points-to-origin
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {

    }

    public int[][] kClosest(int[][] points, int k) {
        sort(points,k,0,points.length-1);
        return Arrays.copyOf(points,k);
    }

    private void sort(int[][] a,int k,int l,int r){
        if(l>r) return;
        int piv=partition(a,l,r);
        if(piv<k) sort(a,k,piv+1,r);
        else if(piv>k) sort(a,k,l,piv-1);
    }

    private int partition(int[][] a,int l,int r){
        int slow=l;
        for(int fast=l;fast<r;fast++){
            if(isCloser(a[fast],a[r])){
                swap(a,slow++,fast);
            }
        }
        swap(a,slow,r);
        return slow;
    }

    private void swap(int[][] a,int l,int r){
        int[] t=a[l];
        a[l]=a[r];
        a[r]=t;
    }

    private boolean isCloser(int[] l,int[] r){
        return l[0]*l[0]+l[1]*l[1]<r[0]*r[0]+r[1]*r[1];
    }

}
