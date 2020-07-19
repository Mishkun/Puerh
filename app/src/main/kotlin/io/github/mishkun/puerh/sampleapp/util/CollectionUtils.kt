package io.github.mishkun.puerh.sampleapp.util

fun <T> setOfNotNull(vararg values: T): Set<T> = HashSet<T>().apply {
    values.filterNotTo(this) { it != null }
}
