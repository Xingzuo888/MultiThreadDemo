package com.example.multithreaddemo

import android.util.Log
import com.example.http.http.DownloadListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody


/**
 *    Author : wxz
 *    Time   : 2021/12/16
 *    Desc   :
 */
object Utils {

    fun download(url: String, listener: DownloadListener): Flow<ResponseBody> = flow {
        delay(500)
        HListener = listener
        Log.d("********", "HListener = ${HListener}")
        listener.onStartDownload()
        val download = HApi.download(url)
        emit(download)
    }


}

suspend fun <T> Flow<T>.next(bloc: suspend T.(T) -> Unit): Unit = collect { bloc(it, it) }




