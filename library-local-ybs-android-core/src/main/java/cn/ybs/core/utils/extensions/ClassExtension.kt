package cn.ybs.core.utils.extensions

import java.lang.reflect.ParameterizedType

@Suppress("UNCHECKED_CAST")
fun <T> getTClass(clazz: Class<*>, index: Int = 0): Class<T> {
    return when (val superType = clazz.genericSuperclass) {
        is ParameterizedType -> superType.actualTypeArguments[index] as Class<T>
        else -> getTClass(superType as Class<*>, index)
    }
}