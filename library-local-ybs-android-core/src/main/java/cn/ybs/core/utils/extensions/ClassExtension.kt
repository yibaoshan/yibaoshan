package cn.ybs.core.utils.extensions

import java.lang.reflect.ParameterizedType

@Suppress("UNCHECKED_CAST")
fun <T> getTClass(clazz: Class<*>, index: Int = 0): Class<T> {
    return (clazz.genericSuperclass as ParameterizedType).actualTypeArguments[index] as Class<T>
}