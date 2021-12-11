package com.sjtu.naivegator

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi

private const val ARG_tmpId = "tmpId"
private const val ARG_canteenName = "canteenName"


class DetailFragment : Fragment() {
    private var tmpId: Int? = null
    private var canteenName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            tmpId = it.getInt(ARG_tmpId)
            canteenName = it.getString(ARG_canteenName)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_detail, container, false)
        tmpId?.let { view.findViewById<ImageView>(R.id.detail_img).setImageResource(it) }
        view.findViewById<TextView>(R.id.detail_intro).text =
                    "canteen_name: $canteenName\n\n"+
                    "Here are some details about the canteen.\n\n"
        view.findViewById<Button>(R.id.btn_exit).setOnClickListener {
//            val cur = requireActivity().supportFragmentManager.fragments.last()
//            requireActivity().supportFragmentManager.beginTransaction()
//                .remove(cur)
//                .commit()

            requireActivity().supportFragmentManager.popBackStack()
            val frags = requireActivity().supportFragmentManager.fragments

        }
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(tmpId: Int, canteenName: String) =
            DetailFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_tmpId, tmpId)
                    putString(ARG_canteenName, canteenName)
                }
            }
    }

}