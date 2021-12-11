package com.sjtu.naivegator.filter

import android.icu.util.ChineseCalendar
import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.util.*

var UpperHall : Array<Double> = arrayOf(31.02153551213232,121.4262408977673)
var MiddleHall : Array<Double> = arrayOf(31.02219094386746,121.42658131249502)
var LowerHall : Array<Double> = arrayOf(31.02286849222382,121.42650225883796)
var EastUpperHall : Array<Double> = arrayOf(31.023431061064738,121.43379009686842)
var EastMiddleHall : Array<Double> = arrayOf(31.025377646358574,121.4328859518389)
var EastLowerHall : Array<Double> = arrayOf(31.026337269553142,121.43245227762415)



class filter_by_time(){
    var formatter   =   SimpleDateFormat   ("yyyy年MM月dd日 HH时mm分ss秒 EEEE");
    var curDate =  Date(System.currentTimeMillis());
    var time_str = formatter.format(curDate)
    val start = time_str.indexOf(" ")
    val hour = time_str.substring(start+1,start+3).toInt()
    val minute = time_str.substring(start+4,start+6).toInt()

    fun is_weekend():Boolean{
        return when{
            time_str.contains("六") -> true
            time_str.contains("日") -> true
            else -> false
        }
    }
    fun is_day_time():Boolean{
        return when{
            hour > 7 -> when{
                minute.toInt() > 30 -> false
                else -> true
            }
            hour <19 ->  true
            else -> false
        }

    }
    fun is_between_2230_2400():Boolean{
        return when{
            hour==22 -> when{
                minute >= 30 -> true
                else -> false
            }
            hour > 22 -> true
            else -> false
        }
    }
    fun is_between_1900_2230():Boolean{
        //19:00~22:30
        return when{
            hour in 19..22 -> true
            hour==22 && minute<=30 -> true
            else -> false
        }
    }

    fun is_midnight():Boolean{
        //24:00 --- 7:30
        return when{
            hour==7 -> when{
                minute <= 30 -> true
                else -> false
            }
            hour<7 -> true
            else ->false
        }
    }
    fun is_morning_readtime():Boolean{
        //6:00--7:30
        return when{
            hour==7 -> when{
                minute <= 30 -> true
                else -> false
            }
            hour==6 -> true
            else ->false
        }
    }

    fun is_between(other : Pair<Pair<Int,Int>,Pair<Int,Int>>):Boolean{
        return ((hour >other.first.first) or ((hour==other.first.first) and (minute >=other.first.second))) and ((hour <other.second.first) or ((hour==other.second.first) and (minute <other.second.second)))
    }
}


fun is_accessible_now(curr_date:filter_by_time,room:Pair<String,String>):Boolean{
    //return true if is not accessible now

    //In weekend or night(21:00--24:00)
    //西区 中院 1-2层 开放时间 7:30-22:30
    //东中院 东中院3号楼3-4层 7:30-22:30
    //东中院3号楼1-2层  7:30-24:00
    //In Midnight
    //中院114、115和东中院3-105、3-106四间教室为24小时通宵自习教室
    if (curr_date.is_midnight()){
        //0:00-7:30
        //可用: 中院114、115和东中院3-105、3-106
        //since I have filtered this already in filter thread.
        return true
    }else if (curr_date.is_between_2230_2400()){
        //22:30--24:00
        //可用: 东中院3号楼1-2层  中院114、115
        if(room.first=="东中院"&&room.second[0]=='3'){
            return room.second[2]=='1'||room.second[2]=='2'
        }else if (room.first=="中院"){
            return room.second=="114"||room.second=="115"
        }
        return false
    }else if(curr_date.is_weekend()||curr_date.is_between_1900_2230()){
        //19:00~22:30
        //可用: 中院1-2层, 东中院3号楼3-4层, 东中院3号楼1-2层
        if(room.first=="东中院"&&room.second[0]=='3'){
            return true
        }else if(room.first=="中院"){
            return room.second[0]=='1'||room.second[0]=='2'
        }
    }
    else{
        //weekdays 7:30---19:00
        //全部可用，需要排除上课
        return true
    }
    return true
}
fun is_audio_studyroom(name:Pair<String,String>):Boolean{
    //aim to exclud audio studyroom
    //中院101-104、201-204八间教室为“有声自习室”
    //exclude 中院101-104、201-204
    var audio_studyroom :List<Pair<String,String>> = listOf(
        Pair("中院","101"),
        Pair("中院","102"),
        Pair("中院","103"),
        Pair("中院","104"),
        Pair("中院","201"),
        Pair("中院","202"),
        Pair("中院","203"),
        Pair("中院","204")
    )
    return audio_studyroom.contains(name)
}


fun test_time(){
    var tmp = filter_by_time()
    filter_log(tmp.is_weekend().toString())
    filter_log(tmp.is_day_time().toString())
}

fun get_distance_from_studyroom(start: Location, building_str:String): Float{
    return when (building_str) {
        "上院" -> _get_distance(start, UpperHall)
        "中院" -> _get_distance(start, MiddleHall)
        "下院" -> _get_distance(start, LowerHall)
        "东上院" -> _get_distance(start, EastUpperHall)
        "东中院" -> _get_distance(start, EastMiddleHall)
        "东下院" -> _get_distance(start, EastLowerHall)
        else -> {
            9999F  //something mistaken in this case
        }
    }
}

