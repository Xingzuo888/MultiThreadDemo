package com.example.http.util

import android.util.Log
import com.example.http.http.DownloadListener
import kotlinx.coroutines.delay
import java.io.*
import java.net.URL

/**
 *    Author : wxz
 *    Time   : 2021/12/20
 *    Desc   :
 */

var byteSize: Int = 1024


/**
 * 写入文件
 * @param inputStream 输入流
 * @param url 下载地址，用户获取文件名
 * @param path 保存文件路径
 * @param isCover 是否覆盖已存在的文件，默认为true 覆盖
 * @param listener 监听下载进度
 */
suspend fun writeFile(
    inputStream: InputStream,
    url: String,
    path: String,
    isCover: Boolean = true,
    listener: DownloadListener? = null
) {
    val mUrl = URL(url)
    val fileName = File(mUrl.file).name
    var file = File(path, fileName)
    var num = 1
    if (!isCover) {
        val path1 = file.path
        while (file.exists()) {
            val prefix = path1.substring(0, path1.lastIndexOf("."))
            file = File(prefix + "(${num++})" + path1.substring(path1.lastIndexOf(".")))
        }
    }
    Log.e("writeFile", "file.path = ${file.path}")
    var fos: FileOutputStream? = null
    try {
        fos = FileOutputStream(file, listener != null && listener.isResume)
        val b = ByteArray(byteSize)
        var len = 0
        while (inputStream.read(b).also { len = it } != -1) {
            Log.e("writeFile", "write $len")
            fos.write(b, 0, len)
            listener?.run {
                filePointer += len
                if (isCancel) {
                    onCancel(file.path)
                    return
                }
                if (isPause) {
                    onPause(file.path)
                }
                while (isPause) {
                    delay(100)
                }
            }
        }
        listener?.onFinishDownload()
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
        listener?.onFail("FileNotFoundException")
    } catch (e: IOException) {
        e.printStackTrace()
        listener?.onFail("IOException")
    } finally {
        fos?.close()
        inputStream.close()
    }
}
