package com.sjtu.naivegator

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.sjtu.naivegator.db.UserPreferenceDao

class SettingsFragment : Fragment() {
    private var weightDistance: Int = 50
    private var dislikes: Set<String> = setOf()
    private var sharedPref: SharedPreferences? = null
    private var prefDao: UserPreferenceDao? = null

    private var preferenceAdapter: PreferenceAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPref = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
        sharedPref?.let { weightDistance = it.getInt("weightDistance", 50) }
        sharedPref?.let { dislikes = it.getStringSet("dislikes", setOf()) as Set<String> }

        prefDao = (requireActivity() as MainActivity).prefDao
        preferenceAdapter = PreferenceAdapter(prefDao!!, requireActivity())
    }

    override fun onDestroy() {
        with(sharedPref?.edit()) {
            this?.putInt("weightDistance", weightDistance)
            this?.putStringSet("dislikes", dislikes)
            this?.apply()
            this?.clear()
        }
        super.onDestroy()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        val weightSeekBar = view.findViewById<SeekBar>(R.id.weight_seekbar)
        val weightText = view.findViewById<TextView>(R.id.weight_text)
        val recyclerView = view.findViewById<RecyclerView>(R.id.dislikes_list)
        val fab = view.findViewById<FloatingActionButton>(R.id.fab)


        weightText.text =
            "Weight between Distance and Traffic: ${weightDistance}/${100 - weightDistance}"
        weightSeekBar.progress = weightDistance
        weightSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                weightDistance = progress
                weightText.text =
                    "Weight between Distance and Traffic: ${weightDistance}/${100 - weightDistance}"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        recyclerView!!.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
        recyclerView.adapter = preferenceAdapter
        preferenceAdapter!!.refresh()

        fab.setOnClickListener {
            view.findViewById<FrameLayout>(R.id.content).setBackgroundColor(Color.WHITE)
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.content, AddPreferenceFragment())
                .commit()
        }
        return view
    }
}