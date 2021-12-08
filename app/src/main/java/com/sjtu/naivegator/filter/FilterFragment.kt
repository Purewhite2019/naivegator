package com.sjtu.naivegator.filter

import android.Manifest

import android.content.Context
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
        var rootView = inflater.inflate(R.layout.fragment_filter, container, false)
        rootView.alpha=0.65f //set alpha for fragment view


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

       //test

       currLocation = locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

       set_item(rootView,1,"一餐教工食堂", get_distance_from_canteen(currLocation!!,1).toInt(), 100)
       set_item(rootView,2,"哈乐", get_distance_from_canteen(currLocation!!,8).toInt(), 400)
       set_item(rootView,3,"四餐", get_distance_from_canteen(currLocation!!,4).toInt(), 400)
       set_item(rootView,4,"二餐", get_distance_from_canteen(currLocation!!,2).toInt(), 400)
       set_item(rootView,5,"玉兰苑", get_distance_from_canteen(currLocation!!,9).toInt(), 400)
        return rootView
    }


    fun update_items(v:View,names: Array<Pair<String,Int>>,people_nums:Array<Int>,
                     ){
        for (i in names.indices){
            set_item(v,i+1,names[i].first, get_distance_from_canteen(currLocation!!, names[i].second).toInt(),people_nums[i])
        }
    }

    fun update_wights(pos_weight:Float,like_weights:Array<Float>){
        //use distances and personal infos to filter
        var map: Map<Float, String>? = null


    }


    fun set_item(v:View,idx:Int,name:String,distance:Int,people:Int){
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