package com.android.algorithm.leetcode;

import java.util.Stack;

public class Easy_415_字符串相加 {

    public static void main(String[] args) {
        String num1 = "123456789" ;
        String num2 = "987654321";
        System.out.println(addStrings(num1,num2));
        System.out.println(addStrings2(num1,num2));
    }

    public static String addStrings2(String num1, String num2) {
        char[] chars1 = num1.toCharArray();//9 9
        char[] chars2 = num2.toCharArray();//9 2 3
        int n1 = chars1.length-1;
        int n2 = chars2.length-1;
        StringBuilder sb = new StringBuilder();
        int index=0;
        long sum=0;
        while(n1>=0||n2>=0){
            long once;
            if(n1<0)once = Character.getNumericValue(chars2[n2]);
            else if(n2<0)once = Character.getNumericValue(chars1[n1]);
            else once = Character.getNumericValue(chars1[n1])+Character.getNumericValue(chars2[n2]);
         once = (long) (once *Math.pow(10,index));
         sum+=once;
            n1--;
            n2--;
            index++;
        }
        return sum+"";
    }

    public static String addStrings(String num1, String num2) {
        char[] chars1 = num1.toCharArray();//9 9
        char[] chars2 = num2.toCharArray();//9 2 3
        int n1 = chars1.length-1;
        int n2 = chars2.length-1;
        StringBuilder sb = new StringBuilder();
        Stack<Integer> stack = new Stack<>();
        int isAdd = 0;
        while(n1>=0||n2>=0){
            int once;
            if(n1<0)once = Character.getNumericValue(chars2[n2]);
            else if(n2<0)once = Character.getNumericValue(chars1[n1]);
            else once = Character.getNumericValue(chars1[n1])+Character.getNumericValue(chars2[n2]);
            if (isAdd>0){
                once++;
                isAdd--;
            }
            if (once>=10){
                isAdd++;
                stack.push(once%10);
            }else stack.push(once);
            n1--;
            n2--;
        }
        if (isAdd>0)stack.push(1);
        while (!stack.empty())sb.append(stack.pop());
        return sb.toString();
    }



}
