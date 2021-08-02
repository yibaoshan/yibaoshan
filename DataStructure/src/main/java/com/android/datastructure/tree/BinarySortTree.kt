package com.android.datastructure.tree


/**
 * 二叉排序树，又称二叉搜索树，英文简称BST(Binary Search/Sort Tree)
 * 特点：左子树必定比父节点小，右节点必定比父节点大
 * */
class BinarySortTree<T : Comparable<T>> {

    private var root: Node<T>? = null

    fun print() {
        print(root)
    }

    private fun print(node: Node<T>?) {
        node ?: return
        print(node.left)
        print("[${node.value}]")
        print(node.right)
    }

    /**
     * 添加数据分为两步走：
     * 1. 根节点不为空的情况下，new新节点赋值给root
     * 2. 根节点不为空
     *      1). 目标值大于根节点的值，取根节点右子树向下遍历
     *      2). 目标值小于根节点，去左子树向下遍历
     *      3). 有相同值，不执行任何操作
     *
     * 注意：实现Comparable接口后在kotlin语法中，可以直接使用大于(>)小于(<)来比较
     *      Java中，还是需要按照Comparable的比较规则，调用compareTo方法，大于0目标值比自己大，小于0则目标值比自己小，等于0位相等值
     *
     * @param value 待添加数据
     * @return null为添加成功 不为空则代表有相同值，不执行插入
     *
     * */
    fun insert(value: T): T? {
        //步骤1 根节点为空
        if (root == null) {
            root = Node(value, null, null)
        } else {
            var temp = root
            while (temp != null) {
                //步骤2-1 目标值大于当前节点，取当前节点右子树继续向下遍历。若右子树为空，new新节点怼上去
                if (value > temp.value) {
                    if (temp.right == null) {
                        temp.right = Node(value, null, null)
                        break
                    } else temp = temp.right
                    continue
                }
                //步骤2-2 目标值小于当前节点，取当前节点左子树继续向下遍历。若左子树为空，new新节点怼上去
                if (value < temp.value) {
                    if (temp.left == null) {
                        temp.left = Node(value, null, null)
                        break
                    } else temp = temp.left
                    continue
                }
                //步骤2-3 不执行任何操作
                return value
            }
        }
        return null
    }

    /**
     * 删除时需考虑4种情况：
     * 1. 待删除的节点没有子节点
     *      1). 待删除节点为根节点，将根节点置空
     *      2). 待删除节点是其父节点的左子树，将其父节点左子树置空
     *      3). 待删除节点是其父节点的右子树，将其父节点右子树置空
     * 2. 待删除节点只有左子树
     *      1). 待删除节点为根节点，将root节点的左子树赋值为待删除节点的左子树
     *      2). 待删除节点是其父节点的左子树，将其父节点的左子树赋值为待删除节点的左子树
     *      3). 待删除节点是其父节点的右子树，将其父节点的右子树赋值为待删除节点的左子树
     * 3. 待删除节点只有右子树
     *      1). 参考步骤2，将后半句中的'左子树'替换为'右子树'即可
     * 4. 待删除节点既有左子树又有右子树，可以让左子树最右侧的节点代替自己，或者让右子树最左侧的节点代替自己，改示例实现的是前者
     *      1). 查询代替待删除节点的节点（示例中使用的是让左子树最右侧的节点代替自己的方案）
     *          1}. 将替代节点的右子树赋值为待删除节点的右子树
     *          2}. 若替代节点不是待删除节点的左子树，还需将替代节点的左子树复制为待删除节点的左子树
     *      2). 参考步骤3，，将后半句中的'左子树'替换为'替换节点'即可
     * */
    fun delete(value: T) {
        root ?: return

        //查询待删除节点，查询不到则返回
        //@tempParent为待删除节点的父节点，更改节点操作时使用
        //@isParentLeftChild用于标识待删除节点为其父节点的左子树还是右子树，更改节点/删除节点时方便定位
        var temp = root
        var tempParent: Node<T>? = null
        var isParentLeftChild = true
        while (temp != null) {
            temp = if (value > temp.value) {
                tempParent = temp
                isParentLeftChild = false
                temp.right
            } else if (value < temp.value) {
                tempParent = temp
                isParentLeftChild = true
                temp.left
            } else break
        }

        temp ?: return

        if (temp.left == null && temp.right == null) {
            if (temp == root) root = null//步骤1.1 待删除节点为根节点，将根节点置空
            else if (isParentLeftChild) tempParent?.left = null//步骤1.2 父节点左子树置空
            else tempParent?.right = null//步骤1.3 父节点右子树置空
        } else if (temp.right == null) {//步骤2
            if (temp == root) root = root?.left
            else if (isParentLeftChild) tempParent?.left = temp.left
            else tempParent?.right = temp.left
        } else if (temp.left == null) {//步骤3
            if (temp == root) root = root?.right
            else if (isParentLeftChild) tempParent?.left = temp.right
            else tempParent?.right = temp.right
        } else {

            //查询替换节点heir
            var replace = temp.left
            var replaceParent = temp
            while (replace?.right != null) {
                replaceParent = replace
                replace = replace.right
            }

            //步骤4.1.1 江山易主，待删除的右子树归替换节点所有
            replace?.right = temp.right
            //步骤4.1.2 若替换节点不是待删除节点的左子树，证明它是最最最底层上来的，还需要把待删除节点的左子树也交给替换节点
            if (replace != temp.left) replace?.left = temp.left

            //步骤4.2
            if (temp == root) root = replace
            else if (isParentLeftChild) tempParent?.left = replace
            else tempParent?.right = replace

            //最后，清空替换节点之前父节点对其的引用
            if (replace?.value == replaceParent?.left?.value) replaceParent?.left = null
            else replaceParent?.right = null
        }

    }

    private class Node<T : Comparable<T>>(var value: T, var left: Node<T>?, var right: Node<T>?)

}