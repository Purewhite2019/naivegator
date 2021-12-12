package com.sjtu.naivegator

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.annotation.RequiresApi
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.sjtu.naivegator.databinding.ActivityNavigationBinding
import com.sjtu.naivegator.filter.FilterFragment
import com.sjtu.naivegator.StudyroomFragment
import com.sjtu.naivegator.db.HistoryDao
import com.sjtu.naivegator.db.HistoryDatabase
import com.sjtu.naivegator.api.bathroom.BathroomBean
import java.security.InvalidParameterException

class MainActivity : AppCompatActivity() {
    // Databases
    private var sharedPref: SharedPreferences? = null
    private var historyDB: HistoryDatabase? = null
    public var historyDao: HistoryDao? = null

    private lateinit var binding: ActivityNavigationBinding

    private val CanteenFragment = CanteenFragment()
    private val StudyroomFragment = StudyroomFragment()
    private val BathroomFragment = BathroomFragment()

    private var is_canteen_now: Boolean = true

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val canteenThread = CanteenThread()
        val studyroomThread = StudyroomThread()
        canteenThread.start()
        studyroomThread.start()

        historyDB = Room
            .databaseBuilder(applicationContext, HistoryDatabase::class.java, "database-history")
            .build()
        historyDao = historyDB!!.historyDao()
        sharedPref = applicationContext.getSharedPreferences("settings", Context.MODE_PRIVATE)

        binding = ActivityNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_navigation)

        navView.setOnNavigationItemReselectedListener {
            val transaction = supportFragmentManager.beginTransaction()
            when (it.itemId) {
                R.id.navigation_canteen -> {
                    transaction.replace(R.id.content, CanteenFragment)
                }
                R.id.navigation_studyroom -> {
                    transaction.replace(R.id.content, StudyroomFragment)
                }
                R.id.navigation_bathroom -> {
                    transaction.replace(R.id.content, BathroomFragment)
                }
                R.id.navigation_settings -> {
                    transaction.replace(R.id.content, SettingsFragment())
                }
                else -> {
                    throw InvalidParameterException("navView::Invalid item id: ${it.itemId}")
                }
            }
        }
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_canteen, R.id.navigation_studyroom, R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onDestroy() {
        historyDB?.close()

        super.onDestroy()
    }

    inner class CanteenThread : Thread() {
        override fun run() {
            super.run()
            updateMap()
        }

        private fun updateMap() {
            while (true) {
                Network.getCanteenData(0)
                for (i in 100..900 step 100) {
                    Network.getCanteenData(i)
                }
                sleep(10000)
//                for(canteen in canteenMap){
//                    println(canteen)
//                }
            }
        }
    }

    inner class StudyroomThread : Thread() {
        override fun run() {
            super.run()
            updateMap()
        }

        private fun updateMap() {
            val studyroomBuildId = listOf<String>(
                "126", "128", "127", "122", "564", "124"
            )
            while (true) {
                for (id in studyroomBuildId) {
                    Network.getStudyroomData(id)
                }
//                for (studyroom in studyroomMap) {
//                    println(studyroom)
//                }
                sleep(60000)
            }
        }
    }

    public fun is_canteen_now(): Boolean {
//        Log.e("is_canteen",CanteenFragment.isVisible.toString())
        return is_canteen_now
    }

    public fun hide_canteen() {
        is_canteen_now = false
    }

    public fun set_canteen() {
        is_canteen_now = true
    }


}