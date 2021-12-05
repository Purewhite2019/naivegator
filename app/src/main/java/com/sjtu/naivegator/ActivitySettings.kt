package com.sjtu.naivegator

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.sjtu.naivegator.db.UserPreferenceDao
import com.sjtu.naivegator.db.UserPreferenceDatabase

class ActivitySettings : AppCompatActivity() {
    private var weightDistance : Int = 50
    private var dislikes : Set<String> = setOf()

    private var sharedPref : SharedPreferences? = null
    private var prefDB : UserPreferenceDatabase? = null
    private var prefDao : UserPreferenceDao? = null

    private var recyclerView : RecyclerView? = null
    private var preferenceAdapter : PreferenceAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sharedPref = applicationContext.getSharedPreferences("settings", Context.MODE_PRIVATE)
        sharedPref?.let { weightDistance = it.getInt("weightDistance", 50) }
        sharedPref?.let { dislikes = it.getStringSet("dislikes", setOf()) as Set<String> }
        prefDB = Room.databaseBuilder(
            applicationContext,
            UserPreferenceDatabase::class.java, "database-preference"
        ).build()
        prefDao = prefDB!!.userPreferenceDao()
        preferenceAdapter = PreferenceAdapter(prefDao!!, this)

        val weightSeekBar = findViewById<SeekBar>(R.id.weight_seekbar)
        val weightText = findViewById<TextView>(R.id.weight_text)

        weightText.text = "Weight between Distance and Traffic: ${weightDistance}/${100-weightDistance}"
        weightSeekBar.progress = weightDistance
        weightSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                weightDistance = progress
                weightText.text = "Weight between Distance and Traffic: ${weightDistance}/${100-weightDistance}"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        recyclerView = findViewById(R.id.dislikes_list)
        recyclerView!!.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView!!.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recyclerView!!.adapter = preferenceAdapter
        preferenceAdapter!!.refresh()

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this, ActivityAddPreference::class.java)
            startActivity(intent)
        }
    }

    override fun onRestart() {
        super.onRestart()
        preferenceAdapter?.refresh()
    }

    override fun onDestroy() {
        with (sharedPref?.edit()) {
            this?.putInt("weightDistance", weightDistance)
            this?.putStringSet("dislikes", dislikes)
            this?.apply()
            this?.clear()
        }
        prefDB?.close()

        super.onDestroy()
    }
}