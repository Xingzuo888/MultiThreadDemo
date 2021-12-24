package com.example.http.http

/**
 *    Author : wxz
 *    Time   : 2020/11/06
 *    Desc   :
 */
interface DownloadListener {
    /**
     * 用于判断是否暂停
     */
    var isPause: Boolean

    /**
     * 用于判断是否取消
     */
    var isCancel: Boolean

    /**
     * 用于判断是否恢复下载
     */
    var isResume: Boolean

    /**
     * 用于恢复下载的起始位置
     */
    var filePointer:Long

    /**
     * 用于恢复下载的文件总长度
     */
    var fileTotalSize:Long

    fun onStartDownload()

    fun onProgress(progress: Int,totalLength: Long)

    fun onFinishDownload()

    fun onFail(errorInfo: String?)

    /**
     * 继续下载
     */
    fun onKeepOn()

    fun onPause(path: String)
    fun onCancel(path: String)
}