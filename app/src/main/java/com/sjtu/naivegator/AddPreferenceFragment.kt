package com.sjtu.naivegator

import android.animation.ArgbEvaluator
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import com.sjtu.naivegator.db.UserPreference
import com.sjtu.naivegator.db.UserPreferenceDao
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

class AddPreferenceFragment : Fragment() {
    private var prefDao: UserPreferenceDao? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefDao = (requireActivity() as MainActivity).prefDao
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_preference, container, false)

        val editText = view.findViewById<EditText>(R.id.edit_text)
        val weightSeekbar = view.findViewById<SeekBar>(R.id.weight_seekbar)
        val addButton = view.findViewById<Button>(R.id.btn_add)
        val weightText = view.findViewById<TextView>(R.id.weight_text)

        editText.focusable = View.FOCUSABLE
        editText.requestFocus()
        val inputManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        inputManager?.showSoftInput(editText, 0)

        addButton.setOnClickListener {
            val content = editText.text
            if (content.isEmpty()) {
                Toast.makeText(context, "No content to add", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            insert(content.toString(), weightSeekbar.progress)
            view.findViewById<FrameLayout>(R.id.content).setBackgroundColor(Color.TRANSPARENT)
            requireActivity().supportFragmentManager.beginTransaction()
                .remove(this)
                .commit()
        }

        weightText.text = "Weight between Distance and Traffic: 50/50"
        weightSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                weightText.text =
                    "Weight between Distance and Traffic: ${progress}/${100 - progress}"
                weightText.setBackgroundColor(interpolateColor(progress))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

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

    private fun insert(content: String, weight: Int) {
        val usePreference = UserPreference(
            pid = System.currentTimeMillis(),
            target = content,
            weight = weight,
            date = SimpleDateFormat(
                "EEE, d MMM yyyy HH:mm:ss",
                Locale.ENGLISH
            ).format(Date(System.currentTimeMillis())).toString()
        )

        thread {
            prefDao!!.insert(usePreference)
        }
    }
}