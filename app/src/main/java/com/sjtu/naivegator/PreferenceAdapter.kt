package com.sjtu.naivegator

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sjtu.naivegator.db.UserPreference
import com.sjtu.naivegator.db.UserPreferenceDao
import kotlin.concurrent.thread

class PreferenceAdapter(private val preferenceDao : UserPreferenceDao, private val activity : Activity)
    : RecyclerView.Adapter<PreferenceViewHolder>() {
    private var preferenceList : MutableList<UserPreference> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreferenceViewHolder {
        val preferenceView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_preference, parent, false)
        return PreferenceViewHolder(preferenceView, preferenceDao)
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder, position: Int) {
        holder.bind(preferenceList[position], this)
    }

    override fun getItemCount(): Int {
        return preferenceList.size
    }

    fun refresh() {
        thread {
            preferenceList.clear()
            val res = preferenceDao.getAll()
            preferenceList.addAll(res)
            preferenceList.sortWith { v1, v2 ->
                if ((v1.date < v2.date) or ((v1.date == v2.date) and (v1.weight > v2.weight))) 1
                else -1
            }
            activity.runOnUiThread{
                notifyDataSetChanged()
            }
        }
    }

}