package com.sjtu.naivegator

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_canteen.*

class CanteenFragment : Fragment() {
    private val imgFiles: Array<String> = arrayOf(
        "ic_canteen",
        "ic_canteen2",
        "ic_canteen3",
        "ic_canteen4",
        "ic_canteen5",
        "ic_canteen6",
        "ic_canteen7",
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
        "玉兰苑"
    )
    private val canteenIntros: Array<String> = arrayOf(
        "一餐还不错",
        "二餐绿源yyds，新疆餐厅好吃不贵",
        "交大最拉跨食堂没有之一",
        "交大第一食堂不解释",
        "东湖的面不错，无田有果+吉姆丽德",
        "没吃过",
        "寿喜锅还可以就是有点贵",
        "麻辣香锅nb"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_canteen, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.items)
        val contacts = Contact.createContactsList(8, imgFiles, canteenNames, canteenIntros)
        val adapter = ContactsAdapter(contacts, context)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        recyclerView.addOnItemTouchListener(
            RecyclerItemClickListener(
                context,
                recyclerView,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        val tmpId = activity!!.resources.getIdentifier(
                            imgFiles[position],
                            "drawable",
                            activity!!.packageName
                        )
                        activity!!.supportFragmentManager.beginTransaction()
                            .add(
                                R.id.content,
                                DetailFragment.newInstance(tmpId, canteenNames[position])
                            )
                            .commit()
                    }

                    override fun onLongItemClick(view: View?, position: Int) {
                        // do whatever
                        val tmpId = activity!!.resources.getIdentifier(
                            imgFiles[position],
                            "drawable",
                            activity!!.packageName
                        )
                        activity!!.supportFragmentManager.beginTransaction()
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
}

class Contact(
    val img: String, val name: String,
    val breed: String, val age: Int, val Intro: String
) {
    companion object {
        private var lastContactId = 0
        fun createContactsList(
            numContacts: Int,
            img_files: Array<String>, names: Array<String>, intros: Array<String>
        ): ArrayList<Contact> {
            val contacts = ArrayList<Contact>()
            for (i in 1..numContacts) {
                var name = img_files[i - 1].substring(3)
                contacts.add(
                    Contact(
                        img_files[i - 1], names[i - 1], name,
                        (1..3).random(), intros[i - 1]
                    )
                )
            }
            return contacts
        }
    }
}

class ContactsAdapter(private val mContacts: List<Contact>, val context: Context?) :
    RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {
    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        val nameTextView = itemView.findViewById<TextView>(R.id.name)
        val introTextView = itemView.findViewById<TextView>(R.id.intro)
        val PictureImageView = itemView.findViewById<ImageView>(R.id.picture)
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
        val image = viewHolder.PictureImageView
        val id = context!!.resources.getIdentifier(contact.img, "drawable", context.packageName)
        image.setImageResource(id)
    }

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return mContacts.size
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