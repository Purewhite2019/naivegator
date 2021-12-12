package com.sjtu.naivegator

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
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
    private val filterPermissionsArrays = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private val REQUEST_PERMISSION = 123
    private val lockPermissionRequest = Object()

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

        checkPermission()
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

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED ||
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(
                this,
                filterPermissionsArrays,
                REQUEST_PERMISSION
            )
            lockPermissionRequest.wait()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var permissionResult = 0
        if (requestCode == REQUEST_PERMISSION) {
            permissionResult = 1
            for (i in permissions.indices) {
                if (grantResults.size > i &&
                    grantResults[i] == PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(this, "已经授权" + permissions[i], Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "拒绝授权" + permissions[i], Toast.LENGTH_LONG).show()
                    permissionResult = 2
                }
            }
        }
        when (permissionResult) {
            1 -> {
                synchronized(lockPermissionRequest) {
                    try {
                        lockPermissionRequest.notifyAll()
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
                Toast.makeText(this, "权限获取成功", Toast.LENGTH_LONG).show()
            }
            2 -> {
                synchronized(lockPermissionRequest) {
                    try {
                        lockPermissionRequest.notifyAll()
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
                Toast.makeText(this, "权限获取失败，筛选器无法使用", Toast.LENGTH_LONG).show()
            }
        }
    }
}