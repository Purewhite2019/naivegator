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
import kotlinx.android.synthetic.main.fragment_canteen.*
import com.sjtu.naivegator.filter.FilterFragment
import com.sjtu.naivegator.filter.filterLog

class CanteenFragment : Fragment() {
    private val filterFragment = FilterFragment()
    private val imgFiles: Array<String> = arrayOf(
        "ic_canteen",
        "ic_canteen2",
        "ic_canteen3",
        "ic_canteen4",
        "ic_canteen5",
        "ic_canteen6",
        "ic_canteen7",
        "ic_canteen8",
        "ic_canteen9"
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

        (requireActivity() as MainActivity).set_canteen()
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
            sleep(1000)
            // Wait for network
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

class Contact(
    val img: String, val name: String, val Intro: String, val progress: Int
) {
    companion object {
        fun createContactsList(
            numContacts: Int,
            img_files: Array<String>, names: Array<String>, intros: MutableList<Pair<Int, Int>>
        ): ArrayList<Contact> {
            val contacts = ArrayList<Contact>()
            for (i in 1..numContacts) {
//                println("now: ${intros[i-1].first}, total: ${intros[i-1].second}")
                if (intros[i - 1].second == 0) {
                    contacts.add(
                        Contact(
                            img_files[i - 1], names[i - 1],
                            "当前楼栋不开放自习",
                            0
                        )
                    )
                } else if (intros[i - 1].second == 1) {
                    contacts.add(
                        Contact(
                            img_files[i - 1], names[i - 1],
                            "正在获取数据中",
                            0
                        )
                    )
                } else {
                    contacts.add(
                        Contact(
                            img_files[i - 1], names[i - 1],
                            "上座率:" + intros[i - 1].first.toString() + "/" + intros[i - 1].second.toString(),
                            intros[i - 1].first * 100 / intros[i - 1].second
                        )
                    )
                }

            }
            return contacts
        }
    }
}

class ContactsAdapter(private val mContacts: MutableList<Contact>, val context: Context?) :
    RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {
    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        val nameTextView = itemView.findViewById<TextView>(R.id.name)
        val introTextView = itemView.findViewById<TextView>(R.id.intro)
        val PictureImageView = itemView.findViewById<ImageView>(R.id.picture)
        val progressBar = itemView.findViewById<ProgressBar>(R.id.tabular)
    }


    // ... constructor and member variables
    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ContactsAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.dynamic_lists, parent, false)
        // Return a new holder instance
        return ViewHolder(contactView)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        //Get the data model based on position
        val contact: Contact = mContacts[position]
        // Set item views based on your views and data model
        val nametextView = viewHolder.nameTextView
        nametextView.text = contact.name
        val introtextView = viewHolder.introTextView
        introtextView.text = contact.Intro
        val progressBar = viewHolder.progressBar
        progressBar.progress = contact.progress
        println(progressBar.progress)
        val image = viewHolder.PictureImageView
        val id = context!!.resources.getIdentifier(contact.img, "drawable", context.packageName)
        image.setImageResource(id)
    }

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return mContacts.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(items: List<Contact>?) {
        if (items != null && items.isNotEmpty()) {
            mContacts.clear()
            mContacts.addAll(items)
            notifyDataSetChanged()
        }
    }

}

class RecyclerItemClickListener(
    context: Context?,
    recyclerView: RecyclerView,
    private val mListener: OnItemClickListener?
) :
    RecyclerView.OnItemTouchListener {
    interface OnItemClickListener {
        fun onItemClick(view: View?, position: Int)
        fun onLongItemClick(view: View?, position: Int)
    }

    var mGestureDetector: GestureDetector =
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                return true
            }

            override fun onLongPress(e: MotionEvent) {
                val child = recyclerView.findChildViewUnder(e.x, e.y)
                if (child != null && mListener != null) {
                    mListener.onLongItemClick(
                        child,
                        recyclerView.getChildAdapterPosition(child)
                    )
                }
            }
        })

    override fun onInterceptTouchEvent(view: RecyclerView, e: MotionEvent): Boolean {
        val childView = view.findChildViewUnder(e.x, e.y)
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, view.getChildAdapterPosition(childView))
            return true
        }
        return false
    }

    override fun onTouchEvent(view: RecyclerView, motionEvent: MotionEvent) {}
    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
}