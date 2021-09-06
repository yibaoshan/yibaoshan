package com.android.datastructure.tree

import kotlin.math.max

/**
 * 平衡二叉排序/搜索树，由BST演变而来，又称自动平衡二叉树
 * @see BinarySortTree
 * 由于BST在某些场景下会几乎退化成链表，查找时间复杂度从O(logN)变为O(N)
 * AVL树在此基础上增加规则，保证哪怕在最坏情况下查找、插入、删除的时间复杂度稳定在O(logN)
 * 增加和删除元素的操作则可能需要借由一次或多次树旋转，以实现树的重新平衡
 * 带有平衡因子1、0或 -1的节点被认为是平衡的。带有平衡因子 -2或2的节点被认为是不平衡的，并需要重新平衡这个树
 *
 * 特点：任何节点的左右子树高度差不得大于1
 *
 * 关键词：左旋、右旋、高度平衡树、平衡因子、最小不平衡子树
 *
 * 节点失衡的四种情况：
 * 1. 左左：左子树的左边节点失衡
 * 2. 右右：右子树的右边节点失衡
 * 3. 左右：左子树的右边节点失衡
 * 4. 右左：右子树的左边节点失衡
 *
 * 这里的失衡情况指的是：当按照二分查找树的规则添加一个新的节点时，发现树的高度差大于1
 * 那么必然是上述场景之一
 *
 */
class AVLTree<T : Comparable<T>> {

    private var root: Node<T>? = null

    /**
     * @see BinarySortTree#insert()
     * */
    fun insert(value: T) {
        if (root == null) {
            root = Node(value, null, null)
        }
    }

    private fun rotateLeftLeft(node: Node<T>?): Node<T>? {
        var top = node?.left
        node?.left = null
        top?.right = node
        updateNodeHeight(top)
        updateNodeHeight(node)
        return top
    }

    /*更新节点的高度信息，受Java引用传递的影响，修改当前形参内部的值*/
    private fun updateNodeHeight(node: Node<T>?) {
        node?.height = 1 + max(getHeight(node?.left), getHeight(node?.right))
    }

    private fun getHeight(node: Node<T>?): Int {
        return node?.height ?: -1
    }

    class Node<T : Comparable<T>>(var value: T, var left: Node<T>?, var right: Node<T>?, var height: Int = 0)

}