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
        filter_log("enter filter fragment")
        var rootView = inflater.inflate(R.layout.fragment_filter, container, false)

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

       filter_log("comping set item")
       rootView.alpha=0.75f //set alpha for fragment view

       //test set a item
       set_item(rootView,1,"三餐湖畔餐厅二楼",1024, 9.4F)




        return rootView
    }

    fun update_wights(){
        //use distances and personal infos to filter
    }

    fun set_item(v:View,idx:Int,name:String,distance:Int,score:Float){
        val item_str = "choice$idx"+"_"
        val _dis = distance.toString()+"m"
        val _sco = "%.1f".format(score)
        var _title_size = 6*24/name.length
        val res: Resources = resources
        v.findViewById<com.hanks.htextview.rainbow.RainbowTextView>(
            res.getIdentifier(item_str+"title", "id", context?.packageName)).text=name

        v.findViewById<com.hanks.htextview.rainbow.RainbowTextView>(
            res.getIdentifier(item_str+"title", "id", context?.packageName)).textSize= _title_size.toFloat()

        filter_log(item_str+"distance")
        v.findViewById<TextView>(
            res.getIdentifier(item_str+"distance", "id", context?.packageName)).text=_dis
        v.findViewById<TextView>(
            res.getIdentifier(item_str+"score", "id", context?.packageName)).text=_sco
    }






    companion object {

        // TODO: Rename and change types and number of parameters
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