package com.sjtu.naivegator

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.*
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.sjtu.naivegator.DetailFragment
import com.sjtu.naivegator.R
import com.sjtu.naivegator.canteenMap
import kotlinx.android.synthetic.main.fragment_canteen.*
import com.sjtu.naivegator.filter.FilterFragment
import com.sjtu.naivegator.filter.filter_log

class StudyroomFragment : Fragment() {
    private val filterFragment = FilterFragment()
    private val imgFiles: Array<String> = arrayOf(
        "ic_up",
        "ic_mid",
        "ic_down",
        "ic_eastup",
        "ic_eastmid",
        "ic_eastdown",

    )
    private val canteenNames: Array<String> = arrayOf(
        "上院",
        "中院",
        "下院",
        "东上院",
        "东中院",
        "东下院"
    )

    private var canteenIntros: MutableList<Pair<Int, Int>> = ArrayList()
    var recyclerView: RecyclerView? = null
    var contacts: ArrayList<Contact>? = null
    var adapter: ContactsAdapter? = null

    fun updateIntros(canteenIntros: MutableList<Pair<Int, Int>>) {
        canteenIntros.clear()
        canteenIntros.addAll(
            arrayOf(
                Pair(canteenMap[100]!!.third, canteenMap[100]!!.second),
                Pair(canteenMap[200]!!.third, canteenMap[200]!!.second),
                Pair(canteenMap[300]!!.third, canteenMap[300]!!.second),
                Pair(canteenMap[400]!!.third, canteenMap[400]!!.second),
                Pair(canteenMap[500]!!.third, canteenMap[500]!!.second),
                Pair(canteenMap[600]!!.third, canteenMap[600]!!.second)
            )
        )
    }

    companion object {
        const val STATUS_FINISH_UPDATE = 0
        var RESULT = ArrayList<Contact>()
    }

    val handler: Handler = Handler(Looper.getMainLooper()) { msg ->
        when (msg.what) {
            STATUS_FINISH_UPDATE -> {
                (recyclerView?.adapter as ContactsAdapter).updateList(RESULT)
            }
        }
        true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun resume() {
        recyclerView?.focusable = View.FOCUSABLE
        recyclerView?.visibility = View.VISIBLE
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_canteen, container, false)
        recyclerView = view.findViewById<RecyclerView>(R.id.items)
        updateIntros(canteenIntros)
        contacts = Contact.createContactsList(6, imgFiles, canteenNames, canteenIntros)
        adapter = ContactsAdapter(contacts!!, context)
        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = LinearLayoutManager(context)

        val updateThread = UpdateThread()
        updateThread.start()

        recyclerView?.addOnItemTouchListener(
            RecyclerItemClickListener(
                context,
                recyclerView!!,
                object : RecyclerItemClickListener.OnItemClickListener {
                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun onItemClick(view: View?, position: Int) {
                        val tmpId = activity!!.resources.getIdentifier(
                            imgFiles[position],
                            "drawable",
                            activity!!.packageName
                        )
//                        recyclerView?.focusable = View.NOT_FOCUSABLE
//                        recyclerView?.visibility = View.INVISIBLE
                        activity!!.supportFragmentManager.beginTransaction()
                            .addToBackStack(null)
                            .add(
                                R.id.content,
                                DetailFragment.newInstance(tmpId, canteenNames[position])
                            )
                            .commit()
                    }

                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun onLongItemClick(view: View?, position: Int) {
                        val tmpId = activity!!.resources.getIdentifier(
                            imgFiles[position],
                            "drawable",
                            activity!!.packageName
                        )
//                        recyclerView?.focusable = View.NOT_FOCUSABLE
//                        recyclerView?.visibility = View.INVISIBLE
                        activity!!.supportFragmentManager.beginTransaction()
                            .addToBackStack(null)
                            .add(
                                R.id.content,
                                DetailFragment.newInstance(tmpId, canteenNames[position])
                            )
                            .commit()
                    }
                })
        )

        val filterFab = view.findViewById<FloatingActionButton>(R.id.canteen_fab)
        filterFab.setOnClickListener {
            childFragmentManager.beginTransaction().remove(filterFragment).commit()
            if (filterFragment.isVisible) {
                childFragmentManager.beginTransaction().remove(filterFragment).commit()
                filterFab.setImageResource(R.drawable.ic_baseline_filter_alt_24)
                recyclerView!!.visibility = RecyclerView.VISIBLE
            } else {

                childFragmentManager.beginTransaction().add(R.id.content, filterFragment).commit()

                filterFab.setImageResource(R.drawable.ic_baseline_close_24)
                recyclerView!!.visibility = RecyclerView.GONE
            }
        }

        return view
    }

    inner class UpdateThread() : Thread() {

        override fun run() {
            super.run()
            update()
        }

        fun update() {
            while (true) {
                updateIntros(canteenIntros)
                val contacts = Contact.createContactsList(6, imgFiles, canteenNames, canteenIntros)
                val msg = Message.obtain()
                msg.what = STATUS_FINISH_UPDATE
                msg.data = Bundle().apply {
                    RESULT = contacts
                }
                handler.sendMessage(msg)

                sleep(10000)
            }
        }
    }

}
