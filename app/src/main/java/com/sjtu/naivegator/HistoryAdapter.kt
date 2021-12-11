package com.sjtu.naivegator

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sjtu.naivegator.db.History
import com.sjtu.naivegator.db.HistoryDao
import kotlin.concurrent.thread

class HistoryAdapter(private val preferenceDao : HistoryDao, private val activity : Activity)
    : RecyclerView.Adapter<HistoryViewHolder>() {
    private var historyList : MutableList<History> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val preferenceView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(preferenceView, preferenceDao)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(historyList[position], this)
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    fun refresh() {
        thread {
            historyList.clear()
            val res = preferenceDao.getAll()
            historyList.addAll(res)
            historyList.sortWith { v1, v2 ->
                if (v1.date < v2.date) 1
                else -1
            }
            activity.runOnUiThread{
                notifyDataSetChanged()
            }
        }
    }
}