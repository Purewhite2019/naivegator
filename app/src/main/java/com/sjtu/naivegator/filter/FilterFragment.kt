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
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.sjtu.naivegator.CanteenInfo.Companion.canteenMap
import com.sjtu.naivegator.R
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
//        rootView.alpha=0.75f //set alpha for fragment view


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




//========initial filter items===============
       // get calculates from weights


       // show items

//       update_items(rootVIew)

//========initial filter items finished===========
       currLocation = locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)



       update_canteen_wights(rootView)

        return rootView
    }


    fun update_canteen_wights(v : View){
        //use distances and personal infos to filter
        val sharedPref = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
        var weightDistance = 50
        val canteenPreference : MutableMap<String, Int> = mutableMapOf()
        sharedPref?.let {
            weightDistance = it.getInt("weightDistance", 50)
            Log.d("sharedPref Load", "weightDistance : $weightDistance")
        }
        sharedPref?.let {
            for ((main, sublist) in com.sjtu.naivegator.canteenMap) {
                canteenPreference[main] = it.getInt(main, 50)
                for (sub in sublist) {
                    canteenPreference["$main $sub"] = it.getInt("$main $sub", 50)
                    Log.d("sharedPref Load", "$main $sub: ${canteenPreference["$main $sub"]}")
                }
            }
        }

        val comparator = kotlin.Comparator { key1: Float, key2: Float -> key2.compareTo(key1) }
        val weight_map= sortedMapOf<Float, String>(comparator)
        val info_map = mutableMapOf<String,Pair<Float,Int>>()
        var i = 0
        for ((key, value) in canteenMap) {
            if(key >= 100){
                continue
            }

            //value: Triple(Pair("闵行第一餐厅", "1F 餐厅") ,0, 0),
            var name = Pair2name(value.first)
//            filter_log(name)
            var current = value.third
            var total = value.second

            if(total==0){
                total=1
            }

//            filter_log("$current $total")
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


    fun set_item(v:View, idx:Int, name:String, distance:Int, people: Int?){
        val item_str = "choice$idx"+"_"
        val _dis = distance.toString()+"m"

        var _title_size : Float = 6F*24/name.length
        if(_title_size>30F){
            _title_size=30F
        }

        val res: Resources = resources
        filter_log(item_str+"title")
        v.findViewById<com.hanks.htextview.rainbow.RainbowTextView>(
            res.getIdentifier(item_str+"title", "id", context?.packageName)).text=name

        v.findViewById<com.hanks.htextview.rainbow.RainbowTextView>(
            res.getIdentifier(item_str+"title", "id", context?.packageName)).textSize= _title_size

        filter_log(item_str+"distance")
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


