package com.sjtu.naivegator.filter


import android.content.Context
import android.location.Location
import android.location.Location.distanceBetween
import android.util.Log
import com.sjtu.naivegator.R



fun filter_log(info: String){
    Log.e("[Filter]",info)
}

fun Pair2name(pair:Pair<String, String>): String {
    var res : String? = null
//    filter_log(pair.first)
    when{
        pair.first.contains("第")-> res = pair.first.substring(3,5)
        pair.first.contains("哈")-> res = pair.first.substring(2,4)
        pair.first.contains("玉")-> res = pair.first.substring(2,5)
    }
    if (pair.first.contains("第")&&!pair.first.contains("七")){
        when{
            pair.second.length==5 -> res += pair.second.substring(0,2)
            pair.second.length!=5 -> res += pair.second.substring(3)
        }
    }
    if (res!!.contains("1F")){
        res=res.replace("1F","一楼")
    }
    if (res!!.contains("2F")){
        res=res.replace("2F","二楼")
    }
    return res!!
}

fun name2canteen(name:String):Int{
    when {
        name.contains("一餐") -> return 1
        name.contains("二餐") -> return 2
        name.contains("三餐") -> return 3
        name.contains("四餐") -> return 4
        name.contains("五餐") -> return 5
        name.contains("六餐") -> return 6
        name.contains("七餐") -> return 7
        name.contains("哈乐") -> return 8
        name.contains("玉兰") -> return 9
    }
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
//    filter_log("calculate distance...")
    val results=FloatArray(1)
    //add all distances
//    filter_log(start.latitude.toString()+" lat")
//    filter_log(start.longitude.toString()+" lon")
    distanceBetween(start.latitude,start.longitude,
        dest[0],
        dest[1],
        results)
//    filter_log(results[0].toString())
    return results[0]
}


