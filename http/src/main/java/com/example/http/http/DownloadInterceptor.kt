package com.example.http.http

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import okio.IOException

/**
 *    Author : wxz
 *    Time   : 2021/12/20
 *    Desc   :
 */
class DownloadInterceptor(private val listener: DownloadListener) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        //        header("RANGE","bytes="+mAlreadyUpLength+"-"+mTotalLength)
        var response = if (listener.isResume) {
            val request = chain.request()
            val newBuilder = request.newBuilder()
            Log.d("DownloadInterceptor", "bytes=${listener.filePointer}-${listener.fileTotalSize}")
            newBuilder.addHeader("RANGE", "bytes=${listener.filePointer}-${listener.fileTotalSize}")
            chain.proceed(newBuilder.build())
        } else {
            chain.proceed(chain.request())
        }
        return response.newBuilder().body(
            HttpResponseBody(response.body!!, listener)
        ).build()
    }
}