package com.sjtu.naivegator.filter

import android.location.Location

var  canteen1 : Array<Double> = arrayOf(
    31.024070452143818,
    121.42726513307298
)

var  canteen2 : Array<Double> = arrayOf(
    31.025432088710918,
    121.43181101226466
)

var  canteen3 : Array<Double> = arrayOf(
    31.02865283334252,
    121.42862486695843
)

var  canteen4 : Array<Double> = arrayOf(
    31.02739487403625,
    121.42255980222775
)

var  canteen5 : Array<Double> = arrayOf(
    31.02558855511843,
    121.43672017770761
)

var  canteen6 : Array<Double> = arrayOf(
    31.03102401582024,
    121.43996290668892
)

var  canteen7 : Array<Double> = arrayOf(
    31.031081992355418,
    121.43672870280365
)

var  canteenHale : Array<Double> = arrayOf(
    31.022708238379966,
    121.42774566643952
)

var  canteenYulan : Array<Double> = arrayOf(
    31.02550321473731,
    121.42665374456776
)

fun get_distance_from_canteen(start: Location, can:Int): Float{
    return when (can) {
        1 -> _get_distance(start, canteen1)
        2 -> _get_distance(start, canteen2)
        3 -> _get_distance(start, canteen3)
        4 -> _get_distance(start, canteen4)
        5 -> _get_distance(start, canteen5)
        6 -> _get_distance(start, canteen6)
        7 -> _get_distance(start, canteen7)
        8 -> _get_distance(start, canteenHale) //哈乐餐厅
        9 -> _get_distance(start, canteenYulan) //玉兰苑
        else -> {
            9999F  //something mistaken in this case
        }
    }
}

fun canteen_Pair2name(pair:Pair<String, String>): String {
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
