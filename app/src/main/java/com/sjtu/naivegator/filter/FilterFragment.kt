package com.sjtu.naivegator.filter

import android.Manifest
import android.app.Activity
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
import kotlinx.android.synthetic.main.dynamic_lists.*
import kotlinx.android.synthetic.main.fragment_filter.*


class FilterFragment : Fragment() {
    private var locationManager: LocationManager? = null
    private var currLocation:Location ?=null


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
        locationManager = requireActivity().applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager?;
       val filterPermissionsArrays = arrayOf(
           Manifest.permission.ACCESS_FINE_LOCATION,
           Manifest.permission.ACCESS_COARSE_LOCATION)

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
            }else{
                Toast.makeText(context, "Request Location Permission!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        filter_log("check finished")
        locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener);
//=======check permission for location finished===============





       currLocation = locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)


        if((requireActivity() as MainActivity).is_canteen_now()){
            update_canteen_wights(rootView)
        }else{
            update_studyroom_wights(rootView)
        }




       //=============test part ==================================
       //---------this part can be deleted at anytime-------------
       test_time()

       //===========================================================
        return rootView
    }
    private var weightDistance = 50
    private val canteenPreference : MutableMap<String, Int> = mutableMapOf()
    private var sharedPref  :SharedPreferences?=null
    fun load_distance_weight(){
        sharedPref?.let {
            weightDistance = it.getInt("weightDistance", 50)
            Log.d("sharedPref Load", "weightDistance : $weightDistance")
        }
    }
    fun load_canteen_preference(){
        sharedPref?.let {
            for ((main, sublist) in com.sjtu.naivegator.canteenNameMap) {
                canteenPreference[main] = it.getInt(main, 50)
                for (sub in sublist) {
                    canteenPreference["$main $sub"] = it.getInt("$main $sub", 50)
                    Log.d("sharedPref Load", "$main $sub: ${canteenPreference["$main $sub"]}")
                }
            }
        }
    }
    fun load_studyroom_preference(){
        //[TODO] add other elements to filter studyroom
//        sharedPref?.let {
//            for ((main, sublist) in com.sjtu.naivegator.canteenNameMap) {
//                canteenPreference[main] = it.getInt(main, 50)
//                for (sub in sublist) {
//                    canteenPreference["$main $sub"] = it.getInt("$main $sub", 50)
//                    Log.d("sharedPref Load", "$main $sub: ${canteenPreference["$main $sub"]}")
//                }
//            }
//        }
    }


    fun update_canteen_wights(v : View){
        //Load data from database
        reset_item(v)
        sharedPref = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
        load_distance_weight()
        load_canteen_preference()
        //use distances and personal infos to filter
        val comparator = kotlin.Comparator { key1: Float, key2: Float -> key2.compareTo(key1) }
        val weight_map= sortedMapOf<Float, String>(comparator)
        val info_map = mutableMapOf<String,Pair<Float,Int>>()
        var i = 0
        for ((key, value) in canteenMap) {
            if(key >= 100){
                continue
            }

            //value: Triple(Pair("闵行第一餐厅", "1F 餐厅") ,0, 0),
            var name = canteen_Pair2name(value.first)
//            filter_log(name)
            var current = value.third
            var total = value.second

            if(total==0){
                total=1
            }


            var crowded_weight = (100-weightDistance)*(current.toFloat()/total)
//            filter_log("$name,人数加权: $crowded_weight")
            var distance = get_distance_from_canteen(currLocation!!, name2canteen(name))
            var pos_weight = weightDistance*(1000F/distance)
//            filter_log("$name,位置加权: $pos_weight")
            var weight = (canteenPreference["${value.first} ${value.second}"]?:50) * (pos_weight+crowded_weight)
//            filter_log("$name,总权重: $weight")
            weight_map.put(weight,name)
            info_map.put(name, Pair(distance,current))
            i+=1
        }

        i=0
       for((key,value) in weight_map!!){
           if (i==5){
               break
           }
           var item = info_map.getValue(value)
           set_item(v,i+1,value, item.first.toInt(), item.second )
           i+=1
       }

    }

    fun update_studyroom_wights(v : View){
        var studyroomMap = mutableMapOf<Pair<String,String>, Triple<String, Int, String>>()
        //Load data from database
        reset_item(v)
        sharedPref = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
        load_distance_weight()
        load_studyroom_preference()
        //use distances and personal infos to filter
//        Toast.makeText(v.context,"In studyroom now ",Toast.LENGTH_SHORT).show()
        val comparator = kotlin.Comparator { key1: Float, key2: Float -> key2.compareTo(key1) }
        val weight_map= sortedMapOf<Float, String>(comparator)
        val info_map = mutableMapOf<String,Pair<Float,Int>>()
        var i = 0

        //get current time
        val current_time = filter_by_time()

        var current_map = studyroomMap
        if (current_time.is_midnight()){
            //24:00 --- 7:30
            //可用: 中院114、115和东中院3-105、3-106
            hide_last_item(v,1)
            //only calculate the above 4 studyrooms
            var Late_night_study_room :List<Pair<String,String>> = listOf(
                Pair("中院","114"),
                Pair("中院","115"),
                Pair("东中院","3-105"),
                Pair("东中院","3-106")
            )
            current_map.clear()
            for(item  in Late_night_study_room){
                current_map.put(item, studyroomMap.get(item)!!)
            }
        }

        for ((key, value) in current_map) {

            //key : Pair<String, String>  (教学楼名，教室名)
            //value : Triple<String, int, String> （总座位，实际人数，温度）、
            if(!is_accessible_now(current_time,key)){
                continue
            }

        }

    }



    fun hide_last_item(v:View, num:Int){
        var localnum = num
        if(num>4){ // hide at most 4 items
            localnum=4
        }
        var idx = 5
        val res: Resources = resources
        while(localnum>0){
            val item_str = "filter_item$idx"
            v.findViewById<FrameLayout>(res.getIdentifier(item_str, "id", context?.packageName)).visibility=FrameLayout.GONE
            idx-=1
            localnum-=1
        }
    }

    fun reset_item(v:View) {
        v.findViewById<FrameLayout>(R.id.filter_item1).visibility=FrameLayout.VISIBLE
        v.findViewById<FrameLayout>(R.id.filter_item2).visibility=FrameLayout.VISIBLE
        v.findViewById<FrameLayout>(R.id.filter_item3).visibility=FrameLayout.VISIBLE
        v.findViewById<FrameLayout>(R.id.filter_item4).visibility=FrameLayout.VISIBLE
        v.findViewById<FrameLayout>(R.id.filter_item5).visibility=FrameLayout.VISIBLE
    }

    fun set_item(v:View, idx:Int, name:String, distance:Int, people: Int?){
        val item_str = "choice$idx"+"_"
        val _dis = distance.toString()+"m"

        var _title_size : Float = 6F*20/name.length
        if(_title_size>30F){
            _title_size=30F
        }

        val res: Resources = resources
//        filter_log(item_str+"title")
        v.findViewById<com.hanks.htextview.rainbow.RainbowTextView>(
            res.getIdentifier(item_str+"title", "id", context?.packageName)).text=name

        v.findViewById<com.hanks.htextview.rainbow.RainbowTextView>(
            res.getIdentifier(item_str+"title", "id", context?.packageName)).textSize= _title_size

//        filter_log(item_str+"distance")
        v.findViewById<TextView>(
            res.getIdentifier(item_str+"distance", "id", context?.packageName)).text=_dis
        v.findViewById<TextView>(
            res.getIdentifier(item_str+"score", "id", context?.packageName)).text=people.toString()
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



    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION) {
            for (i in permissions.indices) {
                if (grantResults.size > i &&
                    grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "已经授权" + permissions[i], Toast.LENGTH_LONG).show()
                }
            }
        }
    }




}


