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
import okhttp3.internal.wait


class FilterFragment : Fragment() {
    private var locationManager: LocationManager? = null
    private var currLocation: Location? = null
    private var normalizationRatio : Float =15F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
            filterLog("request permission failed")
            Toast.makeText(context, "定位权限获取失败，无法使用筛选器", Toast.LENGTH_SHORT)
        }
        filterLog("check finished")
        locationManager?.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            0L,
            0f,
            locationListener
        )
//=======check permission for location finished===============


        currLocation = locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)


        if ((requireActivity() as MainActivity).isCanteenNow()) {
            updateCanteenWights(rootView)
        } else {
            updateStudyroomWeights(rootView)
        }


        //=============test part ==================================
        //---------this part can be deleted at anytime-------------


        //===========================================================
        return rootView
    }

    private var weightDistance = 50
    private val preferenceMap: MutableMap<String, Int> = mutableMapOf()
    private var sharedPref: SharedPreferences? = null
    fun loadDistanceWeight() {
        sharedPref?.let {
            weightDistance = it.getInt("weightDistance", 50)
            Log.d("sharedPref Load", "weightDistance : $weightDistance")
        }
    }

    fun loadCanteenPreference() {
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

    fun loadStudyroomPreference() {
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


    fun updateCanteenWights(v: View) {
        //Load data from database
        resetItem(v)
        sharedPref = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
        loadDistanceWeight()
        loadCanteenPreference()
        //use distances and personal infos to filter
        val comparator = kotlin.Comparator { key1: Float, key2: Float -> key2.compareTo(key1) }
        val weightMap = sortedMapOf<Float, String>(comparator)
        val infoMap = mutableMapOf<String, Triple<Float, Int, Int>>()//距离，当前人数，座位数
        var i = 0
        for ((key, value) in canteenMap) {
            if (key >= 100) {
                continue
            }

            //value: Triple(Pair("闵行第一餐厅", "1F 餐厅") ,0, 0),
            var name = canteenPair2name(value.first)
//            filterLog(name)
            var current = value.third
            var total = value.second

            if (total == 0) {
                total = 1
            }


            var crowdedWeight = (100 - weightDistance) * (1 - (current.toFloat() / total))
            crowdedWeight *= normalizationRatio //Normalization
//            filterLog("$name,人数加权: $crowdedWeight")
            val distance = getDistanceFromCanteen(currLocation!!, name2canteen(name))
            val posWeight = weightDistance * (1000F / distance)
//            filterLog("$name,位置加权: $posWeight")
            val weight = (preferenceMap["${value.first} ${value.second}"]
                ?: 50) * (posWeight + crowdedWeight)
//            filterLog("$name,总权重: $weight")
            weightMap.put(weight, name)
            infoMap.put(name, Triple(distance, current, total))
            i += 1
        }
        i = 0
        for ((key, value) in weightMap!!) {
            if (i == 5) {
                break
            }
            var item = infoMap.getValue(value)
            setItem(
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

    fun updateStudyroomWeights(v: View) {
//        var studyroomMap = mutableMapOf<Pair<String,String>, Triple<String, Int, Pair<String,Boolean>>>()
        //Load data from database
        resetItem(v)
        sharedPref = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
        loadDistanceWeight()
        loadStudyroomPreference()
        filterLog("中院 115" + preferenceMap["中院 115"].toString())

        //use distances and personal infos to filter
//        Toast.makeText(v.context,"In studyroom now ",Toast.LENGTH_SHORT).show()
        val comparator = kotlin.Comparator { key1: Float, key2: Float -> key2.compareTo(key1) }
        val weightMap = sortedMapOf<Float, String>(comparator)
        val infoMap =
            mutableMapOf<String, Triple<Float, Pair<Int, Int>, String>>() //名称, Triple(距离,(当前人数,座位数),温度)
        var i = 0

        //get current time
        val currentTime = filterByTime()



        for ((key, value) in studyroomMap) {

            //key : Pair<String, String>  (教学楼名，教室名)
            //value : Triple<String, int, Pair<String,Boolean>> （总座位，实际人数，(温度,是否有课))）、
            if (!isAccessibleNow(currentTime, key)) {//exclude inaccessible study room
                continue
            }
            if (isAudioStudyroom(key)) {//exclude audio room
                continue
            }
            if (value.third.second) { //exclude study room having class now
                continue
            }
            var currentPeople = value.second
            var totalPeople = value.first.toInt()
            var attendanceRatio = 0F
            attendanceRatio = if (totalPeople == 0) {//Prevent division by zero errors
                (1 - (currentPeople.toFloat() / 1))
            } else {
                (1 - (currentPeople.toFloat() / totalPeople))
            }
            attendanceRatio*=normalizationRatio//Normalization
            var name = studuroomPair2name(key)
            val distance = getDistanceFromStudyroom(currLocation!!, key.first)
            var weight =
                weightDistance * (1000F / distance) + (100 - weightDistance) * attendanceRatio

            filterLog("$name preference[\"${key.first} ${key.second}\"] : ${(preferenceMap["${key.first} ${key.second}"])}")
            weight *= (preferenceMap["${key.first} ${key.second}"] ?: 50)
            filterLog("after $weight")

            if (weightMap.containsKey(weight)) {
                weight = (weight - 0.01F)
            }
            weightMap[weight] = name
            infoMap[name] = Triple(distance, Pair(currentPeople, totalPeople), value.third.first)
        }
        filterLog(weightMap.size.toString())
        if (weightMap.size < 5) {
            hideLastItem(v, 5 - weightMap.size)
        }

        i = 0
        for ((key, value) in weightMap!!) {
            if (i == 5) {
                break
            }
            val item = infoMap.getValue(value)
            var people = "${item.second.first}/${item.second.second}"
            if (currentTime.isMidnight() && item.second.first == 0) {
                people = "无数据"
            }
            setItem(v, i + 1, value, item.first.toInt(), people, true, item.third)
            i += 1
        }
    }


    fun hideLastItem(v: View, num: Int) {
        var localnum = num
        if (num > 5) { // hide at most 4 items
            localnum = 5
        }
        var idx = 5
        val res: Resources = resources
        while (localnum > 0) {
            val itemStr = "filter_item$idx"
            v.findViewById<FrameLayout>(
                res.getIdentifier(
                    itemStr,
                    "id",
                    context?.packageName
                )
            ).visibility = FrameLayout.GONE
            idx -= 1
            localnum -= 1
        }
    }

    fun resetItem(v: View) {
        v.findViewById<FrameLayout>(R.id.filter_item1).visibility = FrameLayout.VISIBLE
        v.findViewById<FrameLayout>(R.id.filter_item2).visibility = FrameLayout.VISIBLE
        v.findViewById<FrameLayout>(R.id.filter_item3).visibility = FrameLayout.VISIBLE
        v.findViewById<FrameLayout>(R.id.filter_item4).visibility = FrameLayout.VISIBLE
        v.findViewById<FrameLayout>(R.id.filter_item5).visibility = FrameLayout.VISIBLE
    }

    fun setItem(
        v: View,
        idx: Int,
        name: String,
        distance: Int,
        people: String,
        hasTemp: Boolean,
        temp: String
    ) {
        val itemStr = "choice$idx" + "_"
        val dis = "距离:" + distance.toString() + "m"

        var titleSize: Float = 6F * 22 / name.length
        if (titleSize > 30F) {
            titleSize = 30F
        }
        var people = "当前:${people}"
        val res: Resources = resources
//        filterLog(itemStr+"title")
        v.findViewById<com.hanks.htextview.rainbow.RainbowTextView>(
            res.getIdentifier(itemStr + "title", "id", context?.packageName)
        ).text = name

        v.findViewById<com.hanks.htextview.rainbow.RainbowTextView>(
            res.getIdentifier(itemStr + "title", "id", context?.packageName)
        ).textSize = titleSize

//        filterLog(itemStr+"distance")
        v.findViewById<TextView>(
            res.getIdentifier(itemStr + "distance", "id", context?.packageName)
        ).text = dis
        v.findViewById<TextView>(
            res.getIdentifier(itemStr + "score", "id", context?.packageName)
        ).text = people
        if (hasTemp) {
            var temp = "温度:${temp}℃"
            v.findViewById<TextView>(
                res.getIdentifier(itemStr + "temp", "id", context?.packageName)
            ).text = temp
        } else {
            v.findViewById<TextView>(
                res.getIdentifier(itemStr + "temp", "id", context?.packageName)
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

}


