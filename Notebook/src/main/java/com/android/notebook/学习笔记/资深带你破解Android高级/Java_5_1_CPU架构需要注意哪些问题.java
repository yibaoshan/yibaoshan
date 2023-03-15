package com.android.notebook.学习笔记.资深带你破解Android高级;

/**
 * author :Bob.
 * date : 2020/11/2
 * desc :
 */
class Java_5_1_CPU架构需要注意哪些问题 {

    /*
     *
     * Android支持的ABIS
     * mips, mips64, X86, X86–64, arm64-v8a, armeabi, armeabi-v7a
     *
     * 主流的：arm64-v8a, armeabi, armeabi-v7a
     *
     * 其中，armeabi兼容性最佳
     *
     *优化：
     * 1.构建时分发
     * 2.使用过滤器
     * defaultConfig {
        ndk {
            abiFilters "armeabi-v7a"
        }
    }
     *
     */
    public static void main(String[] args) {

    }

}
