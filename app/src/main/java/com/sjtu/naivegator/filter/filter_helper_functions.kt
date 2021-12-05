package com.sjtu.naivegator.filter


import android.content.Context
import android.location.Location
import android.location.Location.distanceBetween
import android.util.Log
import com.sjtu.naivegator.R




fun filter_log(info: String){
    Log.e("[Filter]",info)
}
fun get_can():Int{
    //TODO get canteen from string name

    return 1
}
fun get_distance_from_canteen(start: Location,can:Int): Float{
    when (can) {
        1 -> return _get_distance(start, canteen1)
        2 -> return _get_distance(start, canteen2)
        3 ->  return _get_distance(start, canteen3)
        4 ->  return _get_distance(start, canteen4)
        5 ->  return _get_distance(start, canteen5)
        6 ->  return _get_distance(start, canteen6)
        7 ->  return _get_distance(start, canteen7)
        8 ->  return _get_distance(start, canteenHale) //哈乐餐厅
        9 -> return _get_distance(start, canteenYulan) //玉兰苑
        else -> {
            return 9999F  //something mistaken in this case
        }
    }
}

fun _get_distance(start: Location,dest:Array<Double>): Float {
    filter_log("calculate distance...")
    val results=FloatArray(1)
    //add all distances
    filter_log(start.latitude.toString()+" lat")
    filter_log(start.longitude.toString()+" lon")
    distanceBetween(start.latitude,start.longitude,
        dest[0],
        dest[1],
        results)
    filter_log(results[0].toString())
    return results[0]
}


