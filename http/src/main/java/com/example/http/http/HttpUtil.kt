package com.example.http.http

import com.example.http.BuildConfig
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 *    Author : wxz
 *    Time   : 2021/12/17
 *    Desc   :
 */
var baseUrl: String = ""

/**
 * 读取超时时间
 */
var readTimeOut: Long = 30

/**
 * 写入超时时间
 */
var writeTimeOut: Long = 30

/**
 * 连接超时时间
 */
var connectTimeOut: Long = 30

/**
 * * 读取超时时间单位，默认 [TimeUnit.SECONDS} 秒
 */
var readTimeUnit = TimeUnit.SECONDS

/**
 * * 写入超时时间单位，默认 [TimeUnit.SECONDS} 秒
 */
var writeTimeUnit = TimeUnit.SECONDS

/**
 * * 连接超时时间单位，默认 [TimeUnit.SECONDS} 秒
 */
var connectTimeUnit = TimeUnit.SECONDS

/**
 * 下载监听器
 */
var downloadListener: DownloadListener? = null

/**
 * 获取Retrofit对象
 */
val retrofit: Retrofit
    get() {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
//            .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
            .client(getOKHttpClient())
            .build()
    }

/**
 * 获取OKHttpClient对象
 */
private fun getOKHttpClient(): OkHttpClient {
    val builder: OkHttpClient.Builder = OkHttpClient.Builder()
        .readTimeout(readTimeOut, readTimeUnit)
        .writeTimeout(writeTimeOut, writeTimeUnit)
        .connectTimeout(connectTimeOut, connectTimeUnit)
    if (BuildConfig.DEBUG) {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        builder.addInterceptor(httpLoggingInterceptor.apply {
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
        })
    }
    if (downloadListener != null) {
        builder.addInterceptor(DownloadInterceptor(downloadListener!!))
    }

    return builder.build()
}