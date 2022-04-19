package com.android.chatbot.utils.apache;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class StringUtils {
    public StringUtils() {
    }

    public static boolean isBlank(String str){
        if (str==null||str.length()==0)return true;
        return false;
    }

    public static boolean isNotBlank(String str){
        return !isBlank(str);
    }

    private static byte[] getBytes(String string, Charset charset) {
        return string == null ? null : string.getBytes(charset);
    }

    private static ByteBuffer getByteBuffer(String string, Charset charset) {
        return string == null ? null : ByteBuffer.wrap(string.getBytes(charset));
    }

    public static ByteBuffer getByteBufferUtf8(String string) {
        return getByteBuffer(string, Charsets.UTF_8);
    }

    public static byte[] getBytesIso8859_1(String string) {
        return getBytes(string, Charsets.ISO_8859_1);
    }

    public static byte[] getBytesUnchecked(String string, String charsetName) {
        if (string == null) {
            return null;
        } else {
            try {
                return string.getBytes(charsetName);
            } catch (UnsupportedEncodingException var3) {
                throw newIllegalStateException(charsetName, var3);
            }
        }
    }

    public static byte[] getBytesUsAscii(String string) {
        return getBytes(string, Charsets.US_ASCII);
    }

    public static byte[] getBytesUtf16(String string) {
        return getBytes(string, Charsets.UTF_16);
    }

    public static byte[] getBytesUtf16Be(String string) {
        return getBytes(string, Charsets.UTF_16BE);
    }

    public static byte[] getBytesUtf16Le(String string) {
        return getBytes(string, Charsets.UTF_16LE);
    }

    public static byte[] getBytesUtf8(String string) {
        return getBytes(string, Charsets.UTF_8);
    }

    private static IllegalStateException newIllegalStateException(String charsetName, UnsupportedEncodingException e) {
        return new IllegalStateException(charsetName + ": " + e);
    }

    private static String newString(byte[] bytes, Charset charset) {
        return bytes == null ? null : new String(bytes, charset);
    }

    public static String newString(byte[] bytes, String charsetName) {
        if (bytes == null) {
            return null;
        } else {
            try {
                return new String(bytes, charsetName);
            } catch (UnsupportedEncodingException var3) {
                throw newIllegalStateException(charsetName, var3);
            }
        }
    }

    public static String newStringIso8859_1(byte[] bytes) {
        return newString(bytes, Charsets.ISO_8859_1);
    }

    public static String newStringUsAscii(byte[] bytes) {
        return newString(bytes, Charsets.US_ASCII);
    }

    public static String newStringUtf16(byte[] bytes) {
        return newString(bytes, Charsets.UTF_16);
    }

    public static String newStringUtf16Be(byte[] bytes) {
        return newString(bytes, Charsets.UTF_16BE);
    }

    public static String newStringUtf16Le(byte[] bytes) {
        return newString(bytes, Charsets.UTF_16LE);
    }

    public static String newStringUtf8(byte[] bytes) {
        return newString(bytes, Charsets.UTF_8);
    }
}
