package com.sjtu.naivegator.filter

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.sjtu.naivegator.MainActivity
import com.sjtu.naivegator.R
import com.sjtu.naivegator.canteenMap
import com.sjtu.naivegator.studyroomMap


class FilterFragment : Fragment() {
    // Permission lock
    private val lockPermissionRequest = Object()

    private var locationManager: LocationManager? = null
    private var currLocation: Location? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        this.activity?.setContentView(R.layout.fragment_filter)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_filter, container, false)


//================check permission for location=====================
        locationManager =
            requireActivity().applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager?;
        val filterPermissionsArrays = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (ActivityCompat.checkSelfPermission(
                rootView.context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                rootView.context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            filter_log("request permission")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(filterPermissionsArrays, REQUEST_PERMISSION)
                lockPermissionRequest.wait()
            } else {
                Toast.makeText(context, "Request Location Permission!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        filter_log("check finished")
        locationManager?.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            0L,
            0f,
            locationListener
        )
//=======check permission for location finished===============


        currLocation = locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)


        if ((requireActivity() as MainActivity).is_canteen_now()) {
            update_canteen_wights(rootView)
        } else {
            update_studyroom_wights(rootView)
        }


        //=============test part ==================================
        //---------this part can be deleted at anytime-------------
        test_time()

        //===========================================================
        return rootView
    }

    private var weightDistance = 50
    private val preferenceMap: MutableMap<String, Int> = mutableMapOf()
    private var sharedPref: SharedPreferences? = null
    fun load_distance_weight() {
        sharedPref?.let {
            weightDistance = it.getInt("weightDistance", 50)
            Log.d("sharedPref Load", "weightDistance : $weightDistance")
        }
    }

    fun load_canteen_preference() {
        sharedPref?.let {
            for ((main, sublist) in com.sjtu.naivegator.canteenNameMap) {
                preferenceMap[main] = it.getInt(main, 50)
                for (sub in sublist) {
                    preferenceMap["$main $sub"] = it.getInt("$main $sub", preferenceMap[main]!!)
//                    Log.d("sharedPref Load", "$main $sub: ${preferenceMap["$main $sub"]}")
                }
            }
        }
    }

    fun load_studyroom_preference() {
        sharedPref?.let {
            for ((main, sublist) in com.sjtu.naivegator.studyroomNameMap) {
                preferenceMap[main] = it.getInt(main, 50)
                for (sub in sublist) {
                    preferenceMap["$main $sub"] = it.getInt("$main $sub", 50)
                    Log.e("sharedPref Load", "$main $sub: ${preferenceMap["$main $sub"]}")
                }
            }
        }
    }


    fun update_canteen_wights(v: View) {
        //Load data from database
        reset_item(v)
        sharedPref = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
        load_distance_weight()
        load_canteen_preference()
        //use distances and personal infos to filter
        val comparator = kotlin.Comparator { key1: Float, key2: Float -> key2.compareTo(key1) }
        val weight_map = sortedMapOf<Float, String>(comparator)
        val info_map = mutableMapOf<String, Triple<Float, Int, Int>>()//距离，当前人数，座位数
        var i = 0
        for ((key, value) in canteenMap) {
            if (key >= 100) {
                continue
            }

            //value: Triple(Pair("闵行第一餐厅", "1F 餐厅") ,0, 0),
            var name = canteen_Pair2name(value.first)
//            filter_log(name)
            var current = value.third
            var total = value.second

            if (total == 0) {
                total = 1
            }


            val crowded_weight = (100 - weightDistance) * (1 - (current.toFloat() / total))
//            filter_log("$name,人数加权: $crowded_weight")
            val distance = get_distance_from_canteen(currLocation!!, name2canteen(name))
            val pos_weight = weightDistance * (1000F / distance)
//            filter_log("$name,位置加权: $pos_weight")
            val weight = (preferenceMap["${value.first} ${value.second}"]
                ?: 50) * (pos_weight + crowded_weight)
//            filter_log("$name,总权重: $weight")
            weight_map.put(weight, name)
            info_map.put(name, Triple(distance, current, total))
            i += 1
        }
        i = 0
        for ((key, value) in weight_map!!) {
            if (i == 5) {
                break
            }
            var item = info_map.getValue(value)
            set_item(
                v,
                i + 1,
                value,
                item.first.toInt(),
                item.second.toString(),
                false,
                "座位数:${item.third}"
            )
            i += 1
        }

    }

    fun update_studyroom_wights(v: View) {
//        var studyroomMap = mutableMapOf<Pair<String,String>, Triple<String, Int, Pair<String,Boolean>>>()
        //Load data from database
        reset_item(v)
        sharedPref = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
        load_distance_weight()
        load_studyroom_preference()
        filter_log("中院 115" + preferenceMap["中院 115"].toString())

        //use distances and personal infos to filter
//        Toast.makeText(v.context,"In studyroom now ",Toast.LENGTH_SHORT).show()
        val comparator = kotlin.Comparator { key1: Float, key2: Float -> key2.compareTo(key1) }
        val weight_map = sortedMapOf<Float, String>(comparator)
        val info_map =
            mutableMapOf<String, Triple<Float, Pair<Int, Int>, String>>() //名称, Triple(距离,(当前人数,座位数),温度)
        var i = 0

        //get current time
        val current_time = filter_by_time()



        for ((key, value) in studyroomMap) {

            //key : Pair<String, String>  (教学楼名，教室名)
            //value : Triple<String, int, Pair<String,Boolean>> （总座位，实际人数，(温度,是否有课))）、
            if (!is_accessible_now(current_time, key)) {//exclude inaccessible study room
                continue
            }
            if (is_audio_studyroom(key)) {//exclude audio room
                continue
            }
            if (value.third.second) { //exclude study room having class now
                continue
            }
            var current_people = value.second
            var total_people = value.first.toInt()
            var attendance_ratio = 0F
            attendance_ratio = if (total_people == 0) {//Prevent division by zero errors
                (1 - (current_people.toFloat() / 1))
            } else {
                (1 - (current_people.toFloat() / total_people))
            }
            var name = studuroom_Pair2name(key)
            val distance = get_distance_from_studyroom(currLocation!!, key.first)
            var weight =
                weightDistance * (1000F / distance) + (100 - weightDistance) * attendance_ratio

            filter_log("$name preference[\"${key.first} ${key.second}\"] : ${(preferenceMap["${key.first} ${key.second}"])}")
            weight *= (preferenceMap["${key.first} ${key.second}"] ?: 50)
            filter_log("after $weight")

            if (weight_map.containsKey(weight)) {
                weight = (weight - 0.01F)
            }
            weight_map[weight] = name
            info_map[name] = Triple(distance, Pair(current_people, total_people), value.third.first)
        }
        filter_log(weight_map.size.toString())
        if (weight_map.size < 5) {
            hide_last_item(v, 5 - weight_map.size)
        }

        i = 0
        for ((key, value) in weight_map!!) {
            if (i == 5) {
                break
            }
            var item = info_map.getValue(value)
            var _people = "${item.second.first}/${item.second.second}"
            if (current_time.is_midnight() && item.second.first == 0) {
                _people = "无数据"
            }
            set_item(v, i + 1, value, item.first.toInt(), _people, true, item.third)
            i += 1
        }
    }


    fun hide_last_item(v: View, num: Int) {
        var localnum = num
        if (num > 5) { // hide at most 4 items
            localnum = 5
        }
        var idx = 5
        val res: Resources = resources
        while (localnum > 0) {
            val item_str = "filter_item$idx"
            v.findViewById<FrameLayout>(
                res.getIdentifier(
                    item_str,
                    "id",
                    context?.packageName
                )
            ).visibility = FrameLayout.GONE
            idx -= 1
            localnum -= 1
        }
    }

    fun reset_item(v: View) {
        v.findViewById<FrameLayout>(R.id.filter_item1).visibility = FrameLayout.VISIBLE
        v.findViewById<FrameLayout>(R.id.filter_item2).visibility = FrameLayout.VISIBLE
        v.findViewById<FrameLayout>(R.id.filter_item3).visibility = FrameLayout.VISIBLE
        v.findViewById<FrameLayout>(R.id.filter_item4).visibility = FrameLayout.VISIBLE
        v.findViewById<FrameLayout>(R.id.filter_item5).visibility = FrameLayout.VISIBLE
    }

    fun set_item(
        v: View,
        idx: Int,
        name: String,
        distance: Int,
        people: String,
        has_temp: Boolean,
        temp: String
    ) {
        val item_str = "choice$idx" + "_"
        val _dis = "距离:" + distance.toString() + "m"

        var _title_size: Float = 6F * 22 / name.length
        if (_title_size > 30F) {
            _title_size = 30F
        }
        var _people = "当前:${people}"
        val res: Resources = resources
//        filter_log(item_str+"title")
        v.findViewById<com.hanks.htextview.rainbow.RainbowTextView>(
            res.getIdentifier(item_str + "title", "id", context?.packageName)
        ).text = name

        v.findViewById<com.hanks.htextview.rainbow.RainbowTextView>(
            res.getIdentifier(item_str + "title", "id", context?.packageName)
        ).textSize = _title_size

//        filter_log(item_str+"distance")
        v.findViewById<TextView>(
            res.getIdentifier(item_str + "distance", "id", context?.packageName)
        ).text = _dis
        v.findViewById<TextView>(
            res.getIdentifier(item_str + "score", "id", context?.packageName)
        ).text = _people
        if (has_temp) {
            var _temp = "温度:${temp}℃"
            v.findViewById<TextView>(
                res.getIdentifier(item_str + "temp", "id", context?.packageName)
            ).text = _temp
        } else {
            v.findViewById<TextView>(
                res.getIdentifier(item_str + "temp", "id", context?.packageName)
            ).text = temp
        }
    }


    companion object {


        @JvmStatic
        fun newInstance() =
            FilterFragment().apply {
                arguments = Bundle().apply {
                }
            }

        private const val REQUEST_PERMISSION = 123
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
//            yfqing_test_text.text = "" + location.longitude + ":" + location.latitude
            currLocation = location
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
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
                    Toast.makeText(context, "已经授权" + permissions[i], Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "拒绝授权" + permissions[i], Toast.LENGTH_LONG).show()
                    permissionResult = 2
                }
            }
        }
        when (permissionResult) {
            1 ->  {
                Toast.makeText(context, "权限获取失败，应用退出", Toast.LENGTH_LONG).show()
            }
            2 -> {
                Toast.makeText(context, "权限获取成功", Toast.LENGTH_LONG).show()
                lockPermissionRequest.notifyAll()
            }
        }
    }


}


