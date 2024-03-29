package com.example.http.http

/**
 *    Author : wxz
 *    Time   : 2021/12/17
 *    Desc   :
 */
class BaseResponse<T> {
    val data: T? = null
    val code: Int? = null
    val errorMsg: String? = null
    var exception: Throwable? = null
}

inline fun <T> BaseResponse<T>.next(bloc: BaseResponse<T>.() -> Unit): BaseResponse<T> {
    return if (exception == null) {//没有异常，则把正确结果bloc出去
        bloc()
        this
    } else {//出现异常（网络/服务器/自定义异常）执行这里 不用bloc
        this
    }
}

inline fun <T> BaseResponse<T>.catchException(bloc: Throwable.() -> Unit) {
    if (exception != null) {
        bloc(exception!!)
    }
}