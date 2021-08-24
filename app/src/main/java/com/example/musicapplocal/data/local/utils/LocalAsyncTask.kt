package com.example.musicapplocal.data.local.preferences.utils

import android.os.AsyncTask
import com.example.musicapplocal.data.local.utils.OnDataLocalCallback

class LocalAsyncTask<V, T>(
    private val callback: OnDataLocalCallback<T>,
    private val handler: (V) -> T,
) : AsyncTask<V, Unit, T>() {

    private var exception: Exception? = null

    override fun onPostExecute(result: T) {
        super.onPostExecute(result)
        result?.let {
            callback.onSucceed(result)
        } ?: callback.onFailed(exception)
    }

    override fun doInBackground(vararg params: V): T? =
        try {
            handler(params.first())
        } catch (e: Exception) {
            exception = e
            null
        }
}
