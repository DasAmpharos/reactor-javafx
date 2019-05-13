package io.github.dylmeadows.reaktorfx.util

data class MapChange<K, V>(val key: K, val value: V, val changeType: ChangeType)