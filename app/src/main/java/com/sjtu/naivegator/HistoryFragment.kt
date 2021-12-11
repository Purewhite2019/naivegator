package com.sjtu.naivegator

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.animation.ArgbEvaluator
import android.app.Activity
import android.graphics.Color
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sjtu.naivegator.db.History
import com.sjtu.naivegator.db.HistoryDao
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

class HistoryViewHolder(itemView : View, private val historyDao : HistoryDao)
    : RecyclerView.ViewHolder(itemView){
    private var contentText : TextView = itemView.findViewById(R.id.text_content)
    private var dateText : TextView = itemView.findViewById(R.id.text_date)
    private var deleteButton : View = itemView.findViewById(R.id.button_delete)

//    private val colorMap = mapOf(0 to Color.WHITE, 1 to Color.GREEN, 2 to Color.RED)

    fun bind(history: History, historyAdapter: HistoryAdapter) {
        contentText.text = "${history.primaryKey}-${history.secondaryKey}: ${history.rating} Stars"
        dateText.text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(history.date)

        deleteButton.setOnClickListener {
            thread {
                historyDao.delete(history)
                historyAdapter.refresh()
            }
        }
        itemView.setBackgroundColor(interpolateColor(history.rating))
    }

    private fun interpolateColor(weight : Int) : Int {
        val argbEvaluator = ArgbEvaluator()
        return if (weight < 50) {
            argbEvaluator.evaluate((50-weight).toFloat() / 50.0F, Color.WHITE, Color.BLACK) as Int
        } else {
            argbEvaluator.evaluate((weight-50).toFloat() / 50.0F, Color.WHITE, Color.YELLOW) as Int
        }
    }
}

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

class HistoryFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

}