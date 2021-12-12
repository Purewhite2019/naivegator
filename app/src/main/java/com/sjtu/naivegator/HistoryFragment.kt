package com.sjtu.naivegator

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.animation.ArgbEvaluator
import android.app.Activity
import android.graphics.Color
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sjtu.naivegator.db.History
import com.sjtu.naivegator.db.HistoryDao
import java.text.SimpleDateFormat
import kotlin.concurrent.thread

class HistoryFragment : Fragment() {
    private var historyDao: HistoryDao? = null
    private var historyAdapter: HistoryAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        historyDao = (requireActivity() as MainActivity).historyDao
        historyAdapter = HistoryAdapter(historyDao!!, requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)
//        val exitBtn = view.findViewById<Button>(R.id.btn_exit)

        val recyclerView = view.findViewById<RecyclerView>(R.id.history_recview)

//        exitBtn.setOnClickListener {
//            val fragMgr = requireActivity().supportFragmentManager
//            val cur = fragMgr.fragments.last()
//            fragMgr.beginTransaction()
//                .remove(cur)
//                .commit()
//        }

        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
        recyclerView.adapter = historyAdapter
        historyAdapter!!.refresh()
        view.bringToFront()
        recyclerView.bringToFront()

        return view
    }

}