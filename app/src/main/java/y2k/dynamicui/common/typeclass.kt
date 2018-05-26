package y2k.dynamicui.common

import java.util.concurrent.ConcurrentHashMap

val globalInstances = ConcurrentHashMap<Pair<Class<*>, Class<*>>, Any>()

inline fun <reified TC : Any, reified T> registerTypeClass(instance: TC) {
    globalInstances[T::class.java to TC::class.java] = instance
}

inline fun <reified TC> instance(value: Any?): TC {
    val valueClass = value!!.javaClass
    val tcClass = TC::class.java
    return globalInstances[valueClass to tcClass] as TC
}

inline fun <reified TC> instance(x: Any?, y: Any?): TC {
    TODO()
}
