package com.android.java.relfection;

public class ReflectionTest {

    //私有静态常量字符串
    private static final String priStaticFinalStr = "priStaticFinalStr";
    //公开静态常量字符串
    public static final String pubStaticFinalStr = "pubStaticFinalStr";

    //私有静态字符串
    private static String priStaticStr = "priStaticStr";
    //公开静态字符串
    public static String pulStaticStr = "pulStaticStr";

    //私有的成员变量-常量字符串
    private final String priFinalStr = "priStaticStr";
    //公开的成员变量-常量字符串
    public final String pubFinalStr = "pulStaticStr";

    //私有的成员变量-字符串
    private String priStr = "priStaticStr";
    //公开的成员变量-字符串
    public String pubStr = "pulStaticStr";

    //无参构造方法
    public ReflectionTest() {
    }

    //有1个参数的构造方法
    public ReflectionTest(String priStr) {
        this.priStr = priStr;
    }

    //有2个参数的构造方法
    public ReflectionTest(String priStr, String pubStr) {
        this.priStr = priStr;
        this.pubStr = pubStr;
    }
}
