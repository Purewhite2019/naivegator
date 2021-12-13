package com.sjtu.naivegator.filter


import android.content.Context
import android.location.Location
import android.location.Location.distanceBetween
import android.util.Log
import com.sjtu.naivegator.R


fun filterLog(info: String) {
    Log.e("[Filter]", info)
}


fun getDistanceHelper(start: Location, dest: Array<Double>): Float {
//    filterLog("calculate distance...")
    val results = FloatArray(1)
    //add all distances
//    filterLog(start.latitude.toString()+" lat")
//    filterLog(start.longitude.toString()+" lon")
    distanceBetween(
        start.latitude, start.longitude,
        dest[0],
        dest[1],
        results
    )
//    filterLog(results[0].toString())
    return results[0]
}


