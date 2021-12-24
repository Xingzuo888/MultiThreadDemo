package com.example.multithreaddemo

import com.example.http.http.DownloadListener

/**
 *    Author : wxz
 *    Time   : 2021/12/21
 *    Desc   :
 */
data class DataBean(
    var url: String,
    var path: String,
    var downloadListener: DownloadListener
)