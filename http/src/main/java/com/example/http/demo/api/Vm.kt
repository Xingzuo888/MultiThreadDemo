package com.example.http.demo.api

import com.example.http.http.ApiException
import com.example.http.http.HandlerException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import java.lang.IllegalArgumentException


/**
 *    Author : wxz
 *    Time   : 2021/12/17
 *    Desc   :
 */

suspend fun <T> flowRequest(request:suspend ApiInterface.()->BaseResponse<T>?): Flow<BaseResponse<T>> {
    return flow {
        val response = request(Api) ?: throw IllegalArgumentException("数据非法，获取响应数据为空")
        response.throwAPIException()
        emit(response)
    }.flowOn(Dispatchers.IO)
        .onCompletion { cause ->
            run {
                cause?.let { throw catchException(it) }// 这里再重新把捕获的异常再次抛出，调用的时候如果有必要可以再次catch 获取异常
            }
        }
}

fun onApiError(exception: ApiException) {
//    if (handlerApiException(null, exception)) {
//
//    }
}
fun catchException(e:Throwable):Throwable{
    e.printStackTrace()
    val exception = HandlerException.handlerException(e)
    exception?.let {
        if (e is ApiException) {
//            onApiError(it)
        }else{

        }
    }

    return exception
}

fun <T> Flow<T>.catchError(bloc:Throwable.()->Unit)=catch { cause -> bloc(cause) }
suspend fun <T> Flow<T>.next(bloc:suspend T.()->Unit):Unit = catch {  }.collect{ bloc(it) }