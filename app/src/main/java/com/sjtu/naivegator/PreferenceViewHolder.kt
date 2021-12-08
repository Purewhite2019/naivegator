package com.sjtu.naivegator

import android.animation.ArgbEvaluator
import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sjtu.naivegator.db.UserPreference
import com.sjtu.naivegator.db.UserPreferenceDao
import kotlin.concurrent.thread
import kotlin.math.abs

class PreferenceViewHolder(itemView : View, private val preferenceDao : UserPreferenceDao)
    : RecyclerView.ViewHolder(itemView){
        private var contentText : TextView = itemView.findViewById(R.id.text_content)
        private var dateText : TextView = itemView.findViewById(R.id.text_date)
        private var deleteButton : View = itemView.findViewById(R.id.button_delete)

        private val colorMap = mapOf(0 to Color.WHITE, 1 to Color.GREEN, 2 to Color.RED)

    fun bind(userPreference: UserPreference, PreferenceAdapter: PreferenceAdapter) {
        contentText.text = "${userPreference.target}, ${userPreference.weight}"
        dateText.text = userPreference.date

        deleteButton.setOnClickListener {
            thread {
                preferenceDao.delete(userPreference)
                PreferenceAdapter.refresh()
            }
        }
        itemView.setBackgroundColor(interpolateColor(userPreference.weight))

    }

    private fun interpolateColor(weight : Int) : Int {
        val argbEvaluator = ArgbEvaluator()
        return if (weight < 50) {
            argbEvaluator.evaluate((50-weight).toFloat() / 50.0F, Color.WHITE, Color.RED) as Int
        } else {
            argbEvaluator.evaluate((weight-50).toFloat() / 50.0F, Color.WHITE, Color.GREEN) as Int
        }
    }
}