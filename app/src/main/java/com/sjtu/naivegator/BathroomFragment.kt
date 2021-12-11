package com.sjtu.naivegator

import android.os.*
import android.view.*
import androidx.fragment.app.Fragment
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class BathroomFragment : Fragment() {
    private val imgFiles: Array<String> = arrayOf(
        "ic_canteen",
        "ic_canteen2",
        "ic_canteen3",
        "ic_canteen4",
        "ic_canteen5",
        "ic_canteen6",
        "ic_canteen7",
        "ic_canteen8",
        "ic_canteen8"
    )
    private val canteenNames: Array<String> = arrayOf(
        "一餐",
        "二餐",
        "三餐",
        "四餐",
        "五餐",
        "六餐",
        "七餐",
        "哈乐",
        "玉兰苑"
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
                Pair(canteenMap[600]!!.third, canteenMap[600]!!.second),
                Pair(canteenMap[700]!!.third, canteenMap[700]!!.second),
                Pair(canteenMap[800]!!.third, canteenMap[800]!!.second),
                Pair(canteenMap[900]!!.third, canteenMap[900]!!.second)
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
        contacts = Contact.createContactsList(9, imgFiles, canteenNames, canteenIntros)
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
                val contacts = Contact.createContactsList(9, imgFiles, canteenNames, canteenIntros)
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
