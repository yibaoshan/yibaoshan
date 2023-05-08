package com.android.algorithm;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

/**
 * author : xiaobao
 * e-mail : yibaoshan@foxmail.com
 * time   : 2023/03/28
 * desc   :
 */
public class Test {

    @org.junit.Test
    public void main() throws NoSuchAlgorithmException, InterruptedException {

        HashMap<String, String> hashMap = new HashMap<>();

        while (true) {
            byte[][] array = new byte[6][6];
            for (int i = 0; i < array.length; i++) {
                for (int j = 0; j < array[i].length; j++) {
                    array[i][j] = create();
                }
            }
            String k = print(array);
            String v = matrix2string(array);

            if (hashMap.containsKey(k)) {
                System.err.println("重复数组：" + k + " , 原先摘要 ：" + hashMap.get(k) + " , 新摘要：" + v);
            } else {
                hashMap.put(k, v);
            }

//            System.out.println(k + "  " + v);
//            System.out.println("");
            Thread.sleep(1);
        }
    }

    private String print(byte[][] array) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            stringBuilder.append(Arrays.toString(array[i]));
        }
        return stringBuilder.toString();
    }

    private byte create() {
        if (new Random().nextInt() % 2 == 0) return 1;
        return 0;
    }

    public String matrix2string(byte[][] array) throws NoSuchAlgorithmException {
        int column = array.length < 1024 ? 1 : (int) Math.ceil(array.length / 1024.0);
        int row = array[0].length < 1024 ? 1 : (int) Math.ceil(array[0].length / 1024.0);
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < array.length; i += column) {
            StringBuilder sb = new StringBuilder();
            sb.append(i);
            for (int j = 0; j < array[i].length; j += row) {
                sb.append(array[i][j]);
            }
            ret.append(generateMD5(sb.toString()));
        }
        return generateMD5(ret.toString());
    }

    public static String generateMD5(String input) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] byteArr = md5.digest(input.getBytes(StandardCharsets.UTF_8));
        return byte2hex(byteArr);
    }

    public static String byte2hex(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp = "";
        for (byte value : b) {
            //为了保证二进制机器数不变，这里需要& 0XFF
            stmp = (Integer.toHexString(value & 0XFF));
            //如果只有一位，需要在前面补上0凑足两位
            if (stmp.length() == 1) {
                hs.append("0").append(stmp);
            } else {
                hs.append(stmp);
            }
        }
        return hs.toString();
    }

    private static final long MOD = 1_000_000_007;

    public static void main(String[] args) throws NoSuchAlgorithmException {
        int[][] array = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};

        long[] rowSums = new long[array.length];
        int[] rowSumsInt = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            long rowSum = 0;
            for (int j = 0; j < array[i].length; j++) {
                rowSum += array[i][j];
            }
            if (rowSum > Long.MAX_VALUE) {
                rowSums[i] = Long.MAX_VALUE;
                rowSumsInt[i] = (int) (rowSum % Integer.MAX_VALUE);
            } else {
                rowSums[i] = rowSum;
                rowSumsInt[i] = 0;
            }
        }

        long h1 = 0, h2 = 0;
        for (int i = 0; i < rowSums.length; i++) {
            h1 ^= rowSums[i];
            h2 ^= rowSums[i] * (i + 1) + rowSumsInt[i] * (i + 2);
        }

        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putLong(h1);
        buffer.putLong(h2);
        byte[] bytes = buffer.array();

        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digestBytes = md.digest(bytes);
//        String digestStr = DatatypeConverter.printHexBinary(digestBytes);
//        System.out.println("Array digest: " + digestStr);
    }

}
