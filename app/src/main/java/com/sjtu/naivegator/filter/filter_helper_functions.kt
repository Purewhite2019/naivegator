package com.sjtu.naivegator.filter


import android.content.Context
import android.location.Location
import android.location.Location.distanceBetween
import android.util.Log
import com.sjtu.naivegator.R


fun filter_log(info: String) {
    Log.e("[Filter]", info)
}


fun _get_distance(start: Location, dest: Array<Double>): Float {
//    filter_log("calculate distance...")
    val results = FloatArray(1)
    //add all distances
//    filter_log(start.latitude.toString()+" lat")
//    filter_log(start.longitude.toString()+" lon")
    distanceBetween(
        start.latitude, start.longitude,
        dest[0],
        dest[1],
        results
    )
//    filter_log(results[0].toString())
    return results[0]
}


