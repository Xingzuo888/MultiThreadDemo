package com.example.multithreaddemo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.multithreaddemo.databinding.ItemBinding

/**
 *    Author : wxz
 *    Time   : 2021/12/20
 *    Desc   :
 */
class ItemAdapter(val context: Context, val list: MutableList<Bean>,val action: ((Bean,Int,ItemAdapter) -> Unit)? =null):
    RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View,val binding: ItemBinding) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBinding.inflate(LayoutInflater.from(context))
        return ViewHolder(binding.root,binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.name.text = "download ${position+1}"
        holder.binding.download.text = when (list[position].downloadState){
            -1->{"下载"}
            0->{"下载中"}
            1->{"继续下载"}
            2->{"下载中"}
            3->{"下载完成"}
            else->{"下载"}
        }
        holder.binding.progress.progress = list[position].progress
        holder.binding.index.text = "${list[position].progress}%"
        holder.binding.download.setOnClickListener {
                when (list[position].downloadState) {
                    -1->{list[position].downloadState = 0}
                    0 -> {list[position].downloadState = 1}
                    1->{list[position].downloadState = 2}
                    2->{list[position].downloadState = 1}
                    3 -> {
                        list[position].downloadState = 0
                    }
                }
            action?.let { it1 -> it1(list[position],position,this) }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}