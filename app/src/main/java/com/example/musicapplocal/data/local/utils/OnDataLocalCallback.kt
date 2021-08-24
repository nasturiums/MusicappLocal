package com.example.musicapplocal.data.local.utils

interface OnDataLocalCallback<T> {
    fun onSucceed(data: T)
    fun onFailed(e: Exception?)
}
