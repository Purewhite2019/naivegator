package com.sjtu.naivegator

import android.animation.ArgbEvaluator
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.sjtu.naivegator.db.History
import kotlin.concurrent.thread

class SettingsFragment : Fragment() {
    private var weightDistance: Int = 50
    private val canteenPreference : MutableMap<String, Int> = mutableMapOf()

    private var sharedPref: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPref = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
        sharedPref?.let {
            weightDistance = it.getInt("weightDistance", 50)
        }
        sharedPref?.let {
            for ((main, sublist) in canteenNameMap) {
                canteenPreference[main] = it.getInt(main, 50)
                for (sub in sublist) {
                    canteenPreference["$main $sub"] = it.getInt("$main $sub", 50)
                }
            }
        }
    }

    override fun onDestroy() {
        with(sharedPref?.edit()) {
            this?.putInt("weightDistance", weightDistance)
            for ((key, value) in canteenPreference) {
                this?.putInt(key, value)
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
            R.id.canteen_9)
        val canteenRadioList = listOf(
            view.findViewById<RadioButton>(R.id.canteen_1),
            view.findViewById<RadioButton>(R.id.canteen_2),
            view.findViewById<RadioButton>(R.id.canteen_3),
            view.findViewById<RadioButton>(R.id.canteen_4),
            view.findViewById<RadioButton>(R.id.canteen_5),
            view.findViewById<RadioButton>(R.id.canteen_6),
            view.findViewById<RadioButton>(R.id.canteen_7),
            view.findViewById<RadioButton>(R.id.canteen_8),
            view.findViewById<RadioButton>(R.id.canteen_9)
        )

        val subcanteenGroup = view.findViewById<RadioGroup>(R.id.canteen_sublist)
        val subcanteenRadioIdMap = mapOf(
            R.id.subcanteen_1 to 0,
            R.id.subcanteen_2 to 1,
            R.id.subcanteen_3 to 2,
            R.id.subcanteen_4 to 3,
            R.id.subcanteen_5 to 4
        )
        val subcanteenRadioList = listOf(
            view.findViewById<RadioButton>(R.id.subcanteen_1),
            view.findViewById<RadioButton>(R.id.subcanteen_2),
            view.findViewById<RadioButton>(R.id.subcanteen_3),
            view.findViewById<RadioButton>(R.id.subcanteen_4),
            view.findViewById<RadioButton>(R.id.subcanteen_5)
        )

        fun getChosenCanteen(): String{
            return if (canteenRadioGroup.checkedRadioButtonId in canteenRadioIdList) {
                val mainCanteen = view.findViewById<RadioButton>(canteenRadioGroup.checkedRadioButtonId).text.toString()
                subcanteenRadioIdMap[subcanteenGroup.checkedRadioButtonId]?.let {
                    if (it < canteenNameMap[mainCanteen]!!.size)
                        return "$mainCanteen ${canteenNameMap[mainCanteen]!![it]}"
                }
                return mainCanteen
            } else ""
        }
//        return if (canteenRadioGroup.checkedRadioButtonId in canteenRadioIdList) {
//            val mainCanteen = view.findViewById<RadioButton>(canteenRadioGroup.checkedRadioButtonId).text.toString()
//            subcanteenRadioIdMap[subcanteenGroup.checkedRadioButtonId]?.let {
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
            var secondaryKey = ""
            if (primaryKey != "") {
                subcanteenRadioIdMap[subcanteenGroup.checkedRadioButtonId]?.let {
                    if (it < canteenNameMap[primaryKey]!!.size)
                        secondaryKey = canteenNameMap[primaryKey]!![it]
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
            subcanteenGroup.clearCheck()
            val main = view.findViewById<RadioButton>(checkedId).text.toString()
            for (radio in subcanteenRadioList) {
                radio.visibility = GONE
                radio.focusable = NOT_FOCUSABLE
            }
            canteenNameMap[main]!!.forEachIndexed { i, item ->
                subcanteenRadioList[i].text = item
                subcanteenRadioList[i].visibility = VISIBLE
                subcanteenRadioList[i].focusable = FOCUSABLE
            }
            canteenPreference[getChosenCanteen()]?.let {
                preferenceSeekBar.progress = it
                preferenceText.text =
                    "Preference for this canteen: ${it}/${100}"
                preferenceText.setBackgroundColor(interpolateColor(it))
            }
        }

        subcanteenGroup.setOnCheckedChangeListener { _, _ ->
            preferenceSeekBar.progress = canteenPreference[getChosenCanteen()]!!
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
                canteenPreference[getChosenCanteen()] = seekBar!!.progress
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
