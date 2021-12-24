package com.example.multithreaddemo

import android.util.Log
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import com.example.http.http.DownloadListener
import com.example.http.util.SPUtils
import com.example.http.util.writeFile
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import java.io.File

/**
 *    Author : wxz
 *    Time   : 2021/12/20
 *    Desc   :
 */
class MyViewModel : ViewModel(), LifecycleObserver {

    private val TAG = "MyViewModel"
    var threadNum = 3
    val list: MutableList<Bean> = mutableListOf()
    val coroutine = newFixedThreadPoolContext(threadNum, "DownloadContext")
    var jobList: MutableList<Job> = mutableListOf()
    var path = ""
    var listenerMap: MutableMap<String, DownloadListener> = mutableMapOf()
    val FILEPATH = "path_"
    val FILEPROGRESS = "progress_"
    val FILETOTAL = "total_"
    val FILEPOINTER = "filePointer_"

    fun initData() {
        list.clear()
        list.add(Bean(FileUrl.A))
        list.add(Bean(FileUrl.B))
        list.add(Bean(FileUrl.C))
        list.add(Bean(FileUrl.D))
        list.add(Bean(FileUrl.E))
        list.add(Bean(FileUrl.F))
        list.add(Bean(FileUrl.G))
        list.add(Bean(FileUrl.H))
        list.add(Bean(FileUrl.I))
        list.add(Bean(FileUrl.J))
        list.add(Bean(FileUrl.K))
        list.add(Bean(FileUrl.HH))
        for (item in list) {
            if (spContains(FILEPATH + item.path)) {
                item.progress = SPUtils[App.context, FILEPROGRESS + item.path, 0] as Int
                item.isResume = true
            }
        }
    }


    fun cancel(adapter: ItemAdapter) {
        for (job in jobList) {
            job.cancel()
        }
        jobList.clear()

        for (map in listenerMap) {
            map.value.isCancel = true
        }
        listenerMap.clear()
        for (item in adapter.list) {
            item.downloadState = -1
            item.progress = 0
        }
        adapter.notifyDataSetChanged()
    }

    suspend fun load(bean: Bean, position: Int, adapter: ItemAdapter) {
        withContext(coroutine) {
            val listener = object : DownloadListener {
                var indexProgress = 0
                var total = 0L
                var isPauseState = false
                var isCancelState = false
                var isResumeState = bean.isResume
                var filePointerLength = 0L
                var mFileTotalSize = 0L
                override var isPause: Boolean
                    get() = isPauseState
                    set(value) {
                        isPauseState = value
                    }
                override var isCancel: Boolean
                    get() = isCancelState
                    set(value) {
                        isCancelState = value
                    }
                override var isResume: Boolean
                    get() = isResumeState
                    set(value) {
                        isResumeState = value
                    }
                override var filePointer: Long
                    get() = filePointerLength
                    set(value) {
                        filePointerLength = value
                    }
                override var fileTotalSize: Long
                    get() = mFileTotalSize
                    set(value) {
                        mFileTotalSize = value
                    }

                override fun onStartDownload() {
                    filePointerLength = SPUtils[App.context, FILEPOINTER + bean.path, 0L] as Long
                    mFileTotalSize = SPUtils[App.context, FILETOTAL + bean.path, 0L] as Long
                }

                override fun onProgress(progress: Int, totalLength: Long) {
                    bean.progress = progress
                    indexProgress = progress
                    total = totalLength
                    GlobalScope.launch(Dispatchers.Main) {
                        adapter.notifyItemChanged(position)
                    }
                }

                override fun onFinishDownload() {
                    bean.downloadState = 3
                    GlobalScope.launch(Dispatchers.Main) {
                        adapter.notifyItemChanged(position)
                    }
                    bean.isResume = false

                    listenerMap.remove(bean.path)
                    remove(bean.path)
                }

                override fun onFail(errorInfo: String?) {
                    Log.e(TAG, errorInfo.toString())
                    //暂时全部取消,根据具体需求更改
                    cancel()
                    remove(bean.path)
                }

                override fun onKeepOn() {
                    isPauseState = false
                }

                override fun onPause(path: String) {
                    save(bean.path, path, indexProgress, total, filePointerLength)
                }

                override fun onCancel(path: String) {
                    remove(bean.path)
                    File(path).delete()
                }
            }
            Log.d(TAG, "listener = $listener")
            listenerMap.put(bean.path, listener)
            val flow = Utils.download(bean.path, listener)
            flow.flowOn(Dispatchers.IO).cancellable()
                .map {
                    Log.d("*******", "map-----------------------------")
                    it.byteStream()
                }.onCompletion { cause ->

                }.next { input ->
                    Log.d("*******", "collect+++++++++++++++++")
                    writeFile(input, bean.path, path, listener = listener)
                }
        }
    }

    fun pause(bean: Bean, position: Int, adapter: ItemAdapter) {
        listenerMap.getValue(bean.path).isPause = true
        GlobalScope.launch(Dispatchers.Main) {
            adapter.notifyItemChanged(position)
        }
    }

    fun keepOn(bean: Bean, position: Int, adapter: ItemAdapter) {
        listenerMap.getValue(bean.path).isPause = false
        GlobalScope.launch(Dispatchers.Main) {
            adapter.notifyItemChanged(position)
        }
    }

    fun save(url: String, path: String, progress: Int, total: Long, filePointer: Long) {
        SPUtils.putApply(App.context, FILEPATH + url, path)
        SPUtils.putApply(App.context, FILEPROGRESS + url, progress)
        SPUtils.putApply(App.context, FILETOTAL + url, total)
        SPUtils.putApply(App.context, FILEPOINTER + url, filePointer)
    }

    fun remove(url: String) {
        SPUtils.remove(App.context, FILEPATH + url)
        SPUtils.remove(App.context, FILEPROGRESS + url)
        SPUtils.remove(App.context, FILETOTAL + url)
        SPUtils.remove(App.context, FILEPOINTER + url)
    }

    fun spContains(url: String): Boolean {
        return SPUtils.contains(App.context, url)
    }

}