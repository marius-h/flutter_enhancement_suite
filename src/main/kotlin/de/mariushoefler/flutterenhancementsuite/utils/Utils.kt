package de.mariushoefler.flutterenhancementsuite.utils

inline fun <T> buildList(builder: (CollectionBuilder<T>).() -> Unit): List<T> =
    buildCollection(mutableListOf(), builder) as List<T>

inline fun <T> buildCollection(
    result: MutableCollection<T>,
    builder: (CollectionBuilder<T>).() -> Unit
): MutableCollection<T> {
    object : CollectionBuilder<T> {
        override fun add(item: T) {
            result.add(item)
        }

        override fun addAll(items: Collection<T>) {
            result.addAll(items)
        }
    }.builder()
    return result
}

interface CollectionBuilder<in T> {
    fun add(item: T)
    fun addAll(items: Collection<T>)
}
