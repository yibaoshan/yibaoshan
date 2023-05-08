package com.android.blackboard.ui.recyclerview;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class BitmapDigestUtils {

    final float MAX_ROW_LIMIT = 1024f;
    final float MAX_COLUMN_LIMIT = 1024f;

    public String bitmap2string(Bitmap bitmap) throws NoSuchAlgorithmException {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int offsetRow = width < MAX_ROW_LIMIT ? 1 : (int) Math.ceil(width / MAX_ROW_LIMIT);
        int offsetColumn = height < MAX_COLUMN_LIMIT ? 1 : (int) Math.ceil(height / MAX_COLUMN_LIMIT);

        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < width; i += offsetColumn) {
            StringBuilder sb = new StringBuilder();
            sb.append(i);
            for (int j = 0; j < height; j += offsetRow) {
                sb.append(bitmap.getPixel(i, j));
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

    /**
     * 计算Bitmap的MD5摘要值
     *
     * @param bitmap 要计算摘要的Bitmap对象
     * @return Bitmap的MD5摘要值，或者null表示计算失败
     */
    public static String getMD5Digest(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        // 将Bitmap转换为字节数组
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] bytes = outputStream.toByteArray();
        // 计算字节数组的MD5摘要值
        return getMD5Digest(bytes);
    }

    /**
     * 计算字节数组的MD5摘要值
     *
     * @param bytes 要计算摘要的字节数组
     * @return 字节数组的MD5摘要值，或者null表示计算失败
     */
    public static String getMD5Digest(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        try {
            // 创建MD5摘要算法对象
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            // 计算摘要值
            byte[] digest = md5.digest(bytes);
            // 将摘要值转换为字符串
            StringBuilder builder = new StringBuilder();
            for (byte b : digest) {
                builder.append(Integer.toHexString(b & 0xff));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
