package com.sjtu.naivegator

import android.animation.ArgbEvaluator
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.sjtu.naivegator.db.History
import java.security.InvalidParameterException
import kotlin.concurrent.thread

class SettingsFragment : Fragment() {
    private var weightDistance: Int = 50
    private val preferenceInfo : MutableMap<String, Int> = mutableMapOf()

    private var sharedPref: SharedPreferences? = null

    private val historyFragment = HistoryFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPref = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
        sharedPref?.let {
            weightDistance = it.getInt("weightDistance", 50)
        }
        sharedPref?.let {
            for ((main, sublist) in canteenNameMap) {
                preferenceInfo[main] = it.getInt(main, 50)
                for (sub in sublist) {
                    preferenceInfo["$main $sub"] = it.getInt("$main $sub", 50)
                    Log.d("onCreate", "$main $sub\", ${preferenceInfo["$main $sub"]}")
                }
            }
            for ((main, sublist) in studyroomNameMap) {
                preferenceInfo[main] = it.getInt(main, 50)
                for (sub in sublist) {
                    preferenceInfo["$main $sub"] = it.getInt("$main $sub", 50)
                    Log.d("onCreate", "$main $sub\", ${preferenceInfo["$main $sub"]}")
                }
            }
        }
    }

    override fun onDestroy() {
        with(sharedPref?.edit()) {
            this?.putInt("weightDistance", weightDistance)
            for ((key, value) in preferenceInfo) {
                this?.putInt(key, value)
                Log.d("onDestroy", "$key, $value")
            }
            this?.apply()
            this?.clear()
        }
        super.onDestroy()
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        val historyFab = view.findViewById<FloatingActionButton>(R.id.fab_history_view)
//        val addHistoryFab = view.findViewById<FloatingActionButton>(R.id.fab_history)
        val remarkEditText = view.findViewById<EditText>(R.id.remark_text)

        val ratingSeekBar = view.findViewById<SeekBar>(R.id.rating_seekbar)
        val ratingProgBar = view.findViewById<ProgressBar>(R.id.rating_progbar)
        val rateButton = view.findViewById<ImageButton>(R.id.btn_history_add)

        val weightSeekBar = view.findViewById<SeekBar>(R.id.weight_seekbar_pos)
        val weightText = view.findViewById<TextView>(R.id.weight_text_pos)

        val preferenceSeekBar = view.findViewById<SeekBar>(R.id.weight_seekbar_canteen)
        val preferenceText = view.findViewById<TextView>(R.id.weight_text_canteen)

        val canteenRadioGroup = view.findViewById<RadioGroup>(R.id.canteen_group)
        val canteenRadioIdList = listOf(
            R.id.canteen_1,
            R.id.canteen_2,
            R.id.canteen_3,
            R.id.canteen_4,
            R.id.canteen_5,
            R.id.canteen_6,
            R.id.canteen_7,
            R.id.canteen_8,
            R.id.canteen_9
        )
        val studyroomRadioIdList = listOf(
            R.id.studyroom_1,
            R.id.studyroom_2,
            R.id.studyroom_3,
            R.id.studyroom_4,
            R.id.studyroom_5,
            R.id.studyroom_6,
        )
        
        val subitemGroup = view.findViewById<RadioGroup>(R.id.canteen_sublist)
        val subitemRadioIdMap = mutableMapOf<Int, String>()    // R.id.xxx -> idx
//        val subitemRadioList = mutableListOf<RadioButton>() // RadioButton object

        historyFab.setOnClickListener {
            childFragmentManager.beginTransaction().remove(historyFragment).commit()
            if (historyFragment.isVisible) {
                childFragmentManager.beginTransaction().remove(historyFragment).commit()
                historyFab.setImageResource(R.drawable.history)
                weightSeekBar.isEnabled = true
                rateButton.isEnabled = true
            } else {
                childFragmentManager.beginTransaction().add(R.id.content, historyFragment).commit()
                historyFab.setImageResource(R.drawable.ic_baseline_close_24)
                weightSeekBar.isEnabled = false
                rateButton.isEnabled = false
            }
        }

//        historyFab.setOnClickListener {
//            requireActivity().supportFragmentManager.beginTransaction()
//                .addToBackStack(null)
//                .add(
//                    R.id.content,
//                    HistoryFragment()
//                )
//                .commit()
//        }

        fun getChosenCanteen(): String{
            return if (canteenRadioGroup.checkedRadioButtonId in canteenRadioIdList) {
                val mainCanteen = view.findViewById<RadioButton>(canteenRadioGroup.checkedRadioButtonId).text.toString()
                val subCanteen = subitemRadioIdMap[subitemGroup.checkedRadioButtonId]
                if (subCanteen == null)
                    return mainCanteen
                else
                    return "$mainCanteen $subCanteen"
            } else if (canteenRadioGroup.checkedRadioButtonId in studyroomRadioIdList) {
                val mainStudyroom = view.findViewById<RadioButton>(canteenRadioGroup.checkedRadioButtonId).text.toString()
                val subStudyroom = subitemRadioIdMap[subitemGroup.checkedRadioButtonId]
                if (subStudyroom == null)
                    return mainStudyroom
                else
                    return "$mainStudyroom $subStudyroom"
            } else ""
        }
//        return if (canteenRadioGroup.checkedRadioButtonId in canteenRadioIdList) {
//            val mainCanteen = view.findViewById<RadioButton>(canteenRadioGroup.checkedRadioButtonId).text.toString()
//            subitemRadioIdMap[subitemGroup.checkedRadioButtonId]?.let {
//                if (it < canteenNameMap[mainCanteen]!!.size)
//                    return "$mainCanteen ${canteenNameMap[mainCanteen]!![it]}"
//            }
//            return mainCanteen
//        } else ""
        rateButton.setOnClickListener{
            val primaryKey = if (canteenRadioGroup.checkedRadioButtonId in canteenRadioIdList)
                view.findViewById<RadioButton>(canteenRadioGroup.checkedRadioButtonId).text.toString()
            else
                ""
            if (primaryKey == "")
                return@setOnClickListener
            var secondaryKey = ""
            if (primaryKey != "") {
                subitemRadioIdMap[subitemGroup.checkedRadioButtonId]?.let {
                    secondaryKey = it
                }
            }
            val history = History(date = System.currentTimeMillis(), primaryKey = primaryKey, secondaryKey = secondaryKey, rating = ratingSeekBar.progress, remark = remarkEditText.text.toString())
            remarkEditText.text = null
            ratingSeekBar.progress = 0
            ratingProgBar.progress = 0
            val historyDao = (requireActivity() as MainActivity).historyDao
            thread {
                historyDao?.insert(history = history)
                requireActivity().runOnUiThread{
                    Toast.makeText(context, "History added Successfully!", Toast.LENGTH_LONG).show();
                }
            }
        }

        ratingSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                ratingProgBar.progress = progress
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        canteenRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            preferenceSeekBar.visibility = VISIBLE
            preferenceSeekBar.focusable = FOCUSABLE
            preferenceText.visibility = VISIBLE

            preferenceSeekBar.progress = 50
            preferenceText.text = "Preference for this canteen: 50/${100}"

            val main = view.findViewById<RadioButton>(checkedId).text.toString()
            subitemGroup.removeAllViews()
            subitemRadioIdMap.clear()

            if (main in canteenNameMap.keys) {
                canteenNameMap[main]!!.forEachIndexed { i, item ->
                    val radioButton = RadioButton(context)
                    radioButton.text = item
                    subitemGroup.addView(radioButton)
                    subitemRadioIdMap[radioButton.id] = item
                    Log.d("radioButton.id", "${radioButton.id}, ${item}")
                }
                subitemGroup.clearCheck()
                preferenceInfo[getChosenCanteen()]?.let {
                    preferenceSeekBar.progress = it
                    preferenceText.text =
                        "Preference for this canteen: ${it}/${100}"
                    preferenceText.setBackgroundColor(interpolateColor(it))
                }
            } else if (main in studyroomNameMap.keys) {
                studyroomNameMap[main]!!.forEachIndexed { i, item ->
                    val radioButton = RadioButton(context)
                    radioButton.text = item
                    subitemGroup.addView(radioButton)
                    subitemRadioIdMap[radioButton.id] = item
                    Log.d("radioButton.id", "${radioButton.id}, ${item}")
                }
                subitemGroup.clearCheck()
                preferenceInfo[getChosenCanteen()]?.let {
                    preferenceSeekBar.progress = it
                    preferenceText.text =
                        "Preference for this canteen: ${it}/${100}"
                    preferenceText.setBackgroundColor(interpolateColor(it))
                }
            } else {
                throw InvalidParameterException("Invalid primary key: ${main}")
            }

        }

        subitemGroup.setOnCheckedChangeListener { _, _ ->
            Log.d("getChosenCanteen() ", "${getChosenCanteen()}, ${subitemGroup.checkedRadioButtonId}")
            preferenceSeekBar.progress = preferenceInfo[getChosenCanteen()]?:50
            preferenceText.text =
                "Preference for this canteen: ${preferenceSeekBar.progress}/${100}"
            preferenceText.setBackgroundColor(interpolateColor(preferenceSeekBar.progress))
        }

        weightText.text =
            "Weight between Distance and Traffic: ${weightDistance}/${100 - weightDistance}"
        weightSeekBar.progress = weightDistance
        weightSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                weightText.text =
                    "Weight between Distance and Traffic: ${progress}/${100 - progress}"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                weightDistance = seekBar!!.progress
            }
        })

        preferenceSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                preferenceText.text =
                    "Preference for this canteen: ${progress}/${100}"
                preferenceText.setBackgroundColor(interpolateColor(progress))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                preferenceInfo[getChosenCanteen()] = seekBar!!.progress
            }
        })

//        addHistoryFab.setOnClickListener {
//            ;
//        }

        return view
    }

    private fun interpolateColor(weight: Int): Int {
        val argbEvaluator = ArgbEvaluator()
        return if (weight < 50) {
            argbEvaluator.evaluate((50 - weight).toFloat() / 50.0F, Color.WHITE, Color.RED) as Int
        } else {
            argbEvaluator.evaluate((weight - 50).toFloat() / 50.0F, Color.WHITE, Color.GREEN) as Int
        }
    }
}
