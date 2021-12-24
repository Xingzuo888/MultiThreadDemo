package com.example.http.http

import org.json.JSONException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

/**
 *    Author : wxz
 *    Time   : 2021/12/17
 *    Desc   :
 */
object HandlerException {

    const val LOGIN_INVALID_EXCEPTION = "请重新登录"
    const val SERVER_EXCEPTION = "服务器开小差啦，请稍后再试"
    const val TIME_OUT_EXCEPTION = "网络请求超时，请稍后再试"
    const val NETWORK_EXCEPTION = "网络开小差啦，请稍后再试"
    const val JSON_EXCEPTION = "数据类型转换错误"

    const val LOGIN_INVALID_CODE = 401;//2001
    const val NETWORK_CODE = -900010
    const val JSON_CODE = -900011
    const val OTHER_CODE = -9000101

    fun handlerException(throwable: Throwable): Throwable {
        if (throwable is HttpException) {
            return if ((throwable as HttpException).code() == LOGIN_INVALID_CODE) {//登录异常
                ApiException(2001, LOGIN_INVALID_EXCEPTION)
            } else {
                ApiException((throwable as HttpException).code(), SERVER_EXCEPTION)
            }
        }
        if (throwable is IOException) {
            if (throwable is SocketTimeoutException) {
                return SocketTimeoutException(TIME_OUT_EXCEPTION)
            }
            return IOException(NETWORK_EXCEPTION)
        }
        if (throwable is JSONException) {
            return JSONException(JSON_EXCEPTION)
        }
        return throwable
    }
}