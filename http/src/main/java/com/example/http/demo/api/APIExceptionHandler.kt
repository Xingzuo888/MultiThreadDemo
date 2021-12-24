package com.example.http.demo.api

import androidx.appcompat.app.AppCompatActivity
import com.example.http.http.ApiException

/**
 *    Author : wxz
 *    Time   : 2021/12/17
 *    Desc   :
 */
fun APIExceptionHandler(activity: AppCompatActivity, e: ApiException): Boolean {
    val code: Int? = e.code
    val message: String? = e.message
    when (code) {
        2001 -> {
            if (activity != null) {

            }
            return true
        }
    }
    return false
}