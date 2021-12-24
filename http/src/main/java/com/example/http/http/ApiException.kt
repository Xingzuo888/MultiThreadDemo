package com.example.http.http

/**
 *    Author : wxz
 *    Time   : 2021/12/17
 *    Desc   :
 */
class ApiException(val code: Int?, private val msg: String) : Exception(msg) {
}