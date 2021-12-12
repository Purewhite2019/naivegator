package com.sjtu.naivegator

import android.animation.ArgbEvaluator
import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sjtu.naivegator.db.History
import com.sjtu.naivegator.db.HistoryDao
import java.text.SimpleDateFormat
import kotlin.concurrent.thread

class HistoryViewHolder(itemView: View, private val historyDao: HistoryDao) :
    RecyclerView.ViewHolder(itemView) {
    private var contentText: TextView = itemView.findViewById(R.id.text_content)
    private var dateText: TextView = itemView.findViewById(R.id.text_date)
    private var deleteButton: View = itemView.findViewById(R.id.button_delete)

//    private val colorMap = mapOf(0 to Color.WHITE, 1 to Color.GREEN, 2 to Color.RED)

    fun bind(history: History, historyAdapter: HistoryAdapter) {
        if (history.secondaryKey == "")
            contentText.text = "${history.primaryKey}: ${history.rating / 20.0F} Stars"
        else
            contentText.text =
                "${history.primaryKey}-${history.secondaryKey}: ${history.rating / 20.0F} Stars"
        dateText.text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(history.date)

        deleteButton.setOnClickListener {
            thread {
                historyDao.delete(history)
                historyAdapter.refresh()
            }
        }
        itemView.setBackgroundColor(interpolateColor(history.rating))
        deleteButton.bringToFront()
    }

    private fun interpolateColor(rating: Int): Int {
        val argbEvaluator = ArgbEvaluator()
        return if (rating < 50) {
            argbEvaluator.evaluate((50 - rating).toFloat() / 50.0F, Color.WHITE, Color.BLACK) as Int
        } else {
            argbEvaluator.evaluate(
                (rating - 50).toFloat() / 50.0F,
                Color.WHITE,
                Color.YELLOW
            ) as Int
        }
    }
}