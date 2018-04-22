package xyz.arnau.muvicat.data

interface SimpleCallback<T, V> {
    fun onSuccess(data: T)
    fun onFailure(error: V)
}