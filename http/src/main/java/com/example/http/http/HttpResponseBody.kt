package com.example.http.http

import android.util.Log
import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*

/**
 *    Author : wxz
 *    Time   : 2021/12/20
 *    Desc   :
 */
class HttpResponseBody(
    private val responseBody: ResponseBody,
    private val listener: DownloadListener? = null
) : ResponseBody() {
    private var bufferedSource: BufferedSource? = null
    override fun contentLength(): Long {
        return responseBody.contentLength()
    }

    override fun contentType(): MediaType? {
        return responseBody.contentType()
    }

    override fun source(): BufferedSource {
        if (bufferedSource == null) {
            bufferedSource = source(responseBody.source()).buffer()
        }
        return bufferedSource!!
    }

    private fun source(source: Source): Source {
        var totalBytesRead =
            if (listener != null && listener.isResume) listener.filePointer else 0L
        val totalSize =
            if (listener != null && listener.isResume) listener.fileTotalSize else responseBody.contentLength()
        return object : ForwardingSource(source) {
            @Throws(IOException::class)
            override fun read(sink: Buffer, byteCount: Long): Long {
                val bytesRead = super.read(sink, byteCount)
                totalBytesRead += if (bytesRead != -1L) bytesRead else 0
                Log.v(
                    "HttpDownload",
                    "read: ${(totalBytesRead * 100 / totalSize).toInt()}"
                )
                if (bytesRead != -1L) {
                    listener?.apply {
                        if (isPause || isCancel) return@apply
                        onProgress(
                            (totalBytesRead * 100 / totalSize).toInt(),
                            responseBody.contentLength()
                        )
                    }
                }
                return bytesRead
            }
        }
    }
}