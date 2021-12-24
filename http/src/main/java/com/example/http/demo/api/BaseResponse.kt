package com.example.http.demo.api

import com.example.http.http.ApiException

/**
 *    Author : wxz
 *    Time   : 2021/12/17
 *    Desc   :
 */
class BaseResponse<T> {
    val data: T? = null
    val code: Int? = null
    val errorMsg = ""

    fun isValid(): Boolean {
        return code == 0
    }

    @Throws(ApiException::class)
    fun throwAPIException() {
        if (!isValid()) {
            throw ApiException(code, errorMsg)
        }
    }
}