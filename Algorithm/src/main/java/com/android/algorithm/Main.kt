package com.android.algorithm

class Main {

    /**
     * 算法思想汇总
     *
     * 五大基本/常用算法
     * 分治法
     *
     * 动态规划：
     * @see com.android.algorithm.leetcode.Easy_121_买卖股票的最佳时机
     * @see com.android.algorithm.leetcode.Easy_118_杨辉三角
     * @see com.android.algorithm.leetcode.Easy_1137_第N个泰波那契数
     * @see com.android.algorithm.leetcode.Easy_509_斐波那契数
     *
     * 贪心算法：
     * @see com.android.algorithm.leetcode.Medium_435_无重叠区间
     *
     * 回溯
     * 分支界定
     *
     * 滑动窗口：
     * @see com.android.algorithm.leetcode.Medium_L3_无重复字符的最长子串
     * */

    /****力扣begin****/

    /**
     * 数据结构
     * 数组：
     * @see com.android.algorithm.leetcode.Easy_217_存在重复元素
     * @see com.android.algorithm.leetcode.Easy_53_最大子序和
     * @see com.android.algorithm.leetcode.Easy_L1_两数之和
     * @see com.android.algorithm.leetcode.Easy_88_合并两个有序数组
     * @see com.android.algorithm.leetcode.Easy_350_两个数组的交集2
     * @see com.android.algorithm.leetcode.Easy_121_买卖股票的最佳时机
     * @see com.android.algorithm.leetcode.Easy_566_重塑矩阵
     * @see com.android.algorithm.leetcode.Easy_118_杨辉三角
     * @see com.android.algorithm.leetcode.Medium_36_有效的数独
     * @see com.android.algorithm.leetcode.Medium_73_矩阵置零
     * @see com.android.algorithm.leetcode.Easy_136_只出现一次的数字
     * @see com.android.algorithm.leetcode.Easy_169_多数元素
     * @see com.android.algorithm.leetcode.Medium_15_三数之和
     * @see com.android.algorithm.leetcode.Medium_75_颜色分类
     * @see com.android.algorithm.leetcode.Medium_56_合并区间
     * @see com.android.algorithm.leetcode.Easy_706_设计哈希映射
     * @see com.android.algorithm.leetcode.Easy_119_杨辉三角2
     * @see com.android.algorithm.leetcode.Medium_48_旋转图像
     * @see com.android.algorithm.leetcode.Medium_59_螺旋矩阵2
     * @see com.android.algorithm.leetcode.Medium_240_搜索二维矩阵2
     * @see com.android.algorithm.leetcode.Medium_435_无重叠区间
     * @see com.android.algorithm.leetcode.Medium_334_递增的三元子序列
     * @see com.android.algorithm.leetcode.Medium_238_除自身以外数组的乘积
     * @see com.android.algorithm.leetcode.Medium_560_和为K的子数组
     *
     * 字符串：
     * @see com.android.algorithm.leetcode.Easy_387_字符串中的第一个唯一字符
     * @see com.android.algorithm.leetcode.Easy_383_赎金信
     * @see com.android.algorithm.leetcode.Easy_242_有效的字母异位词
     * @see com.android.algorithm.leetcode.Easy_409_最长回文串
     * @see com.android.algorithm.leetcode.Easy_415_字符串相加
     * @see com.android.algorithm.leetcode.Medium_763_划分字母区间
     * @see com.android.algorithm.leetcode.Easy_290_单词规律
     * @see com.android.algorithm.leetcode.Medium_49_字母异位词分组
     * @see com.android.algorithm.leetcode.Medium_43_字符串相乘
     * @see com.android.algorithm.leetcode.Medium_5_最长回文子串
     * @see com.android.algorithm.leetcode.Medium_187_重复的DNA序列
     *
     * 链表：
     * @see com.android.algorithm.leetcode.Easy_141_环形链表
     * @see com.android.algorithm.leetcode.Easy_21_合并两个有序链表
     * @see com.android.algorithm.leetcode.Easy_203_移除链表元素
     * @see com.android.algorithm.leetcode.Easy_206_反转链表
     * @see com.android.algorithm.leetcode.Easy_83_删除排序链表中的重复元素
     * @see com.android.algorithm.leetcode.Medium_L2_两数相加
     * @see com.android.algorithm.leetcode.Medium_142_环形链表2
     * @see com.android.algorithm.leetcode.Easy_160_相交链表
     * @see com.android.algorithm.leetcode.Medium_82_删除排序链表中的重复元素2
     * @see com.android.algorithm.leetcode.Medium_24_两两交换链表中的节点
     * @see com.android.algorithm.leetcode.Hard_25_K个一组翻转链表
     * @see com.android.algorithm.leetcode.Medium_143_重排链表
     *
     * 栈/队列：
     * @see com.android.algorithm.leetcode.Easy_20_有效的括号
     * @see com.android.algorithm.leetcode.Easy_232_用栈实现队列
     * @see com.android.algorithm.leetcode.Easy_155_最小栈
     * @see com.android.algorithm.leetcode.Medium_1249_移除无效的括号
     * @see com.android.algorithm.leetcode.Medium_1823_找出游戏的获胜者
     *
     * 树：
     * @see com.android.algorithm.leetcode.Easy_144_二叉树的前序遍历
     * @see com.android.algorithm.leetcode.Easy_94_二叉树的中序遍历
     * @see com.android.algorithm.leetcode.Easy_145_二叉树的后序遍历
     * @see com.android.algorithm.leetcode.Easy_104_二叉树的最大深度
     * @see com.android.algorithm.leetcode.Easy_101_对称二叉树
     * @see com.android.algorithm.leetcode.Medium_102_二叉树的层序遍历
     * @see com.android.algorithm.leetcode.Easy_700二叉搜索树中的搜索
     * @see com.android.algorithm.leetcode.Easy_701_二叉搜索树中的插入操作
     * @see com.android.algorithm.leetcode.Medium_98_验证二叉搜索树
     * @see com.android.algorithm.leetcode.Easy_653_两数之和IV输入BST
     * @see com.android.algorithm.leetcode.Easy_235_二叉搜索树的最近公共祖先
     * @see com.android.algorithm.leetcode.Easy_108_将有序数组转换为二叉搜索树
     * @see com.android.algorithm.leetcode.Medium_105_从前序与中序遍历序列构造二叉树
     * @see com.android.algorithm.leetcode.Medium_103_二叉树的锯齿形层序遍历
     * @see com.android.algorithm.leetcode.Medium_199_二叉树的右视图
     * @see com.android.algorithm.leetcode.Medium_113_路径总和2
     * @see com.android.algorithm.leetcode.Medium_450_删除二叉搜索树中的节点
     *
     * */

    /**
     * 算法入门14天计划
     *
     * 二分查找：
     * @see com.android.algorithm.leetcode.Easy_704_二分查找
     * @see com.android.algorithm.leetcode.Easy_278_第一个错误的版本
     * @see com.android.algorithm.leetcode.Easy_35_搜索插入位置
     *
     * 双指针：
     * @see com.android.algorithm.leetcode.Easy_977_有序数组的平方
     * @see com.android.algorithm.leetcode.Medium_189_旋转数组
     * @see com.android.algorithm.leetcode.Easy_283_移动零
     * @see com.android.algorithm.leetcode.Easy_167_两数之和2输入有序数组
     * @see com.android.algorithm.leetcode.Easy_344_反转字符串
     * @see com.android.algorithm.leetcode.Easy_557_反转字符串中的单词3
     * @see com.android.algorithm.leetcode.Easy_876_链表的中间结点
     * @see com.android.algorithm.leetcode.Medium_19_删除链表的倒数第N个结点
     *
     * 滑动窗口：
     * @see com.android.algorithm.leetcode.Medium_567_字符串的排列
     * @see com.android.algorithm.leetcode.Medium_L3_无重复字符的最长子串
     *
     * 广度优先搜索/深度优先搜索：
     * @see com.android.algorithm.leetcode.Easy_733_图像渲染
     * @see com.android.algorithm.leetcode.Medium_695_岛屿的最大面积
     * @see com.android.algorithm.leetcode.Easy_617_合并二叉树
     * @see com.android.algorithm.leetcode.Medium_116_填充每个节点的下一个右侧节点指针
     * @see com.android.algorithm.leetcode.Medium_542_01矩阵
     * @see com.android.algorithm.leetcode.Medium_994_腐烂的橘子
     *
     * 递归/回溯：
     * @see com.android.algorithm.leetcode.Easy_21_合并两个有序链表
     * @see com.android.algorithm.leetcode.Easy_206_反转链表
     * @see com.android.algorithm.leetcode.Medium_77_组合
     * @see com.android.algorithm.leetcode.Medium_46_全排列
     * @see com.android.algorithm.leetcode.Medium_784_字母大小写全排列
     *
     * 动态规划：
     * @see com.android.algorithm.leetcode.Easy_70_爬楼梯
     * @see com.android.algorithm.leetcode.Medium_198_打家劫舍
     * @see com.android.algorithm.leetcode.Medium_120_三角形最小路径和
     *
     * 位运算：
     * @see com.android.algorithm.leetcode.Easy_231_2的幂
     * @see com.android.algorithm.leetcode.Easy_191_位1的个数
     * @see com.android.algorithm.leetcode.Easy_190_颠倒二进制位
     * @see com.android.algorithm.leetcode.Easy_136_只出现一次的数字
     *
     * */

    /**
     * 力扣动态规划21天计划
     * @see com.android.algorithm.leetcode.Easy_70_爬楼梯
     * */

    /****力扣end****/

}