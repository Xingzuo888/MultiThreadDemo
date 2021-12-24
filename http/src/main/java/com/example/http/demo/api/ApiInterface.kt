package com.example.http.demo.api

import com.example.http.http.baseUrl
import com.example.http.http.retrofit
import retrofit2.http.GET

/**
 *    Author : wxz
 *    Time   : 2021/12/17
 *    Desc   :
 */

val Api:ApiInterface by lazy {
    /*create retrofit 之前可以设置baseURL 、
    readTimeOut、writeTimeOut、connectTimeOut、
    readTimeUnit、writeTimeUnit、connectTimeUnit
    */
    baseUrl = ""
    retrofit.create(ApiInterface::class.java)
}

/**
 * 进行网络请求接口
 */
interface ApiInterface{

    @GET("/xxx/xxx")
    suspend fun getXXX():BaseResponse<Int>
}