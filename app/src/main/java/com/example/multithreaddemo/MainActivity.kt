package com.example.multithreaddemo

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.http.util.SPUtils
import com.example.multithreaddemo.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.FileNotFoundException
import java.util.concurrent.ThreadPoolExecutor

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private var isnot = false
    private lateinit var binding: ActivityMainBinding
    private val viewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(MyViewModel::class.java)
    }
    private var iAdapter: ItemAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.path = getExternalFilesDir("download")?.path.toString()
        viewModel.initData()
        initView()
        Log.d(
            TAG,
            "corePoolSize = ${(viewModel.coroutine.executor as ThreadPoolExecutor).corePoolSize}"
        )
        Log.d(
            TAG,
            "maximumPoolSize = ${(viewModel.coroutine.executor as ThreadPoolExecutor).maximumPoolSize}"
        )
        Log.d(TAG, "poolSize = ${(viewModel.coroutine.executor as ThreadPoolExecutor).poolSize}")
        Log.d(
            TAG,
            "activeCount = ${(viewModel.coroutine.executor as ThreadPoolExecutor).activeCount}"
        )
        Log.d(
            TAG,
            "largestPoolSize = ${(viewModel.coroutine.executor as ThreadPoolExecutor).largestPoolSize}"
        )
    }

    fun initView() {

        iAdapter = ItemAdapter(this, viewModel.list) { bean, position, adapter ->
            Log.d(TAG, "bean = $bean")
            Log.d(TAG, "position = $position")
            Log.d(
                TAG,
                "filePath = ${SPUtils[App.context, viewModel.FILEPATH + bean.path, ""] as String}"
            )
            when (bean.downloadState) {
                0 -> {
                    if (bean.isResume) {
                        try {
                            val filePath =
                                SPUtils[App.context, viewModel.FILEPATH + bean.path, ""] as String


                        } catch (e: FileNotFoundException) {
                            e.printStackTrace()
                        } finally {
                            viewModel.jobList.add(GlobalScope.launch {
                                viewModel.load(bean, position, adapter)
                            })
                        }
                    } else {
                        viewModel.jobList.add(GlobalScope.launch {
                            viewModel.load(bean, position, adapter)
                        })
                    }
                }
                1 -> {
                    viewModel.pause(bean, position, adapter)
                }
                2 -> {
                    viewModel.keepOn(bean, position, adapter)
                }
            }

        }

        binding.recycler.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = iAdapter
        }



        findViewById<Button>(R.id.Stop).setOnClickListener {
            iAdapter?.let { viewModel.cancel(it) }
        }
        binding.fastDownload.setOnClickListener {
            iAdapter?.let {
                GlobalScope.launch {
                    for (i in 0..6) {
                        it.list[i].downloadState = 0
                        viewModel.jobList.add(launch {
                            viewModel.load(it.list[i], i, it)
                        })
                        launch(Dispatchers.Main) {
                            it.notifyItemChanged(i)
                        }
                        delay(500)
                    }
                }

            }
        }

        binding.text.setOnClickListener {
//            viewModel.threadNum = 1
//            (viewModel.coroutine.executor as ThreadPoolExecutor).corePoolSize = viewModel.threadNum
            Log.d(
                TAG,
                "corePoolSize = ${(viewModel.coroutine.executor as ThreadPoolExecutor).corePoolSize}"
            )
            Log.d(
                TAG,
                "maximumPoolSize = ${(viewModel.coroutine.executor as ThreadPoolExecutor).maximumPoolSize}"
            )
            Log.d(
                TAG,
                "poolSize = ${(viewModel.coroutine.executor as ThreadPoolExecutor).poolSize}"
            )
            Log.d(
                TAG,
                "activeCount = ${(viewModel.coroutine.executor as ThreadPoolExecutor).activeCount}"
            )
            Log.d(
                TAG,
                "largestPoolSize = ${(viewModel.coroutine.executor as ThreadPoolExecutor).largestPoolSize}"
            )
        }
    }


}