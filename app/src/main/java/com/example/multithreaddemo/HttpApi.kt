package com.example.multithreaddemo

import com.example.http.http.DownloadListener
import com.example.http.http.baseUrl
import com.example.http.http.downloadListener
import com.example.http.http.retrofit
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

/**
 *    Author : wxz
 *    Time   : 2021/12/20
 *    Desc   :
 */

var HListener: DownloadListener? = null
val HApi: HttpApi
    get() {
        downloadListener = HListener
        baseUrl = "http://static.file.plusplustu.com/"
        return retrofit.create(HttpApi::class.java)
    }

interface HttpApi {
    @Streaming
    @GET
    suspend fun download(@Url url: String): ResponseBody
}