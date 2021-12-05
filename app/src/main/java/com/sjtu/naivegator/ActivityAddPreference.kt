package com.sjtu.naivegator

import android.animation.ArgbEvaluator
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.sjtu.naivegator.db.UserPreference
import com.sjtu.naivegator.db.UserPreferenceDao
import com.sjtu.naivegator.db.UserPreferenceDatabase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

class ActivityAddPreference : AppCompatActivity() {
    private var prefDB : UserPreferenceDatabase? = null
    private var prefDao : UserPreferenceDao? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_preference)
        title = "Add a Preference"

        prefDB = Room.databaseBuilder(
            applicationContext,
            UserPreferenceDatabase::class.java, "database-preference"
        ).build()
        prefDao = prefDB!!.userPreferenceDao()

        val editText = findViewById<EditText>(R.id.edit_text)
        val weightSeekbar = findViewById<SeekBar>(R.id.weight_seekbar)
        val addButton = findViewById<Button>(R.id.btn_add)
        val weightText = findViewById<TextView>(R.id.weight_text)

        editText.focusable = View.FOCUSABLE
        editText.requestFocus()
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        inputManager?.showSoftInput(editText, 0)

        addButton.setOnClickListener {
            val content = editText.text
            if (content.isEmpty()) {
                Toast.makeText(this, "No content to add", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            insert(content.toString(), weightSeekbar.progress)
            this.finish()
        }

        weightText.text = "Weight between Distance and Traffic: 50/50"
        weightSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                weightText.text = "Weight between Distance and Traffic: ${progress}/${100-progress}"
                weightText.setBackgroundColor(interpolateColor(progress))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

    }

    override fun onDestroy() {
        super.onDestroy()
        prefDB?.close()
    }

    private fun interpolateColor(weight : Int) : Int {
        val argbEvaluator = ArgbEvaluator()
        return if (weight < 50) {
            argbEvaluator.evaluate((50-weight).toFloat() / 50.0F, Color.WHITE, Color.RED) as Int
        } else {
            argbEvaluator.evaluate((weight-50).toFloat() / 50.0F, Color.WHITE, Color.GREEN) as Int
        }
    }

//    @PrimaryKey
//    val pid : Int,
//    @ColumnInfo(name = "target") val target : String,
//    @ColumnInfo(name = "weight") val weight : Int,
//    @ColumnInfo(name = "date") val date : String

    private fun insert(content: String, weight: Int) {
        val usePreference = UserPreference(
            pid = System.currentTimeMillis(),
            target = content,
            weight = weight,
            date = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.ENGLISH).format(Date(System.currentTimeMillis())).toString()
        )

        thread {
            prefDao!!.insert(usePreference)
        }

    }
}