package com.android.datastructure.tree

import kotlin.math.max

/**
 * 平衡二叉排序/搜索树，由BST演变而来，又称自动平衡二叉树
 * @see BinarySortTree
 * 由于BST在某些场景下会几乎退化成链表，查找时间复杂度从O(logN)变为O(N)
 * AVL树在此基础上增加规则，保证查找时间复杂度稳定在O(N)
 *
 * 特点：任何节点的左右子树高度差不得大于1
 *
 * 关键词：左旋、右旋、高度平衡树、平衡因子、最小不平衡子树
 *
 * 节点失衡的四种情况：
 * 1. 左左：左子树的左边节点
 */
class AVLTree<T : Comparable<T>> {

    private fun rotateLL(node: Node<T>?): Node<T>? {
        var top = node?.left
        node?.left = null
        top?.right = node
        updateHeight(top)
        updateHeight(node)
        return top
    }

    private fun updateHeight(node: Node<T>?) {
        node?.height = 1 + max(getHeight(node?.left), getHeight(node?.right))
    }

    private fun getHeight(node: Node<T>?): Int {
        return node?.height ?: -1
    }

    private class Node<T : Comparable<T>>(var value: T, var left: Node<T>?, var right: Node<T>?, var height: Int = 0)

}