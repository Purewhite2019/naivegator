package com.sjtu.naivegator

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.sjtu.naivegator.databinding.ActivityNavigationBinding
import com.sjtu.naivegator.db.UserPreferenceDao
import com.sjtu.naivegator.db.UserPreferenceDatabase
import com.sjtu.naivegator.filter.FilterFragment
import java.security.InvalidParameterException

class MainActivity : AppCompatActivity() {
    // Databases
    private var sharedPref: SharedPreferences? = null
    private var prefDB: UserPreferenceDatabase? = null
    public var prefDao: UserPreferenceDao? = null

    private lateinit var binding: ActivityNavigationBinding
    private val filterFragment = FilterFragment()
    private var fab : FloatingActionButton?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPref = applicationContext.getSharedPreferences("settings", Context.MODE_PRIVATE)
        prefDB = Room.databaseBuilder(
            applicationContext,
            UserPreferenceDatabase::class.java, "database-preference"
        ).build()
        prefDao = prefDB!!.userPreferenceDao()


        binding = ActivityNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_navigation)

        navView.setOnNavigationItemReselectedListener {
            val transaction = supportFragmentManager.beginTransaction()
            when (it.itemId) {
                R.id.navigation_canteen -> {
                    transaction.replace(R.id.content, CanteenFragment())
                }
                R.id.navigation_studyroom -> {
                    transaction.replace(R.id.content, CanteenFragment())
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

        val filterFab = findViewById<FloatingActionButton>(R.id.fab)
        filterFab.setOnClickListener {
            if (filterFragment.isVisible) {
                supportFragmentManager.beginTransaction().remove(filterFragment).commit();
                filterFab.setImageResource(R.drawable.ic_baseline_filter_alt_24)
            } else {
                supportFragmentManager.beginTransaction().add(R.id.content, filterFragment).commit()
                filterFab.setImageResource(R.drawable.ic_baseline_close_24)
            }
        }

    }

    override fun onDestroy() {
        prefDB?.close()

        super.onDestroy()
    }
}