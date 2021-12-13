package com.sjtu.naivegator.filter

import android.icu.util.ChineseCalendar
import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.util.*

var UpperHall: Array<Double> = arrayOf(31.02153551213232, 121.4262408977673)
var MiddleHall: Array<Double> = arrayOf(31.02219094386746, 121.42658131249502)
var LowerHall: Array<Double> = arrayOf(31.02286849222382, 121.42650225883796)
var EastUpperHall: Array<Double> = arrayOf(31.023431061064738, 121.43379009686842)
var EastMiddleHall: Array<Double> = arrayOf(31.025377646358574, 121.4328859518389)
var EastLowerHall: Array<Double> = arrayOf(31.026337269553142, 121.43245227762415)

val lateNightStudyRoom: List<Pair<String, String>> = listOf(
    Pair("中院", "114"),
    Pair("中院", "115"),
    Pair("东中院", "3-105"),
    Pair("东中院", "3-106"),
    Pair("东下院", "105"),
    Pair("东下院", "115"),
)

class filterByTime() {
    var formatter = SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒 EEEE");
    var curDate = Date(System.currentTimeMillis());
    var timeStr = formatter.format(curDate)
    val start = timeStr.indexOf(" ")
    val hour = timeStr.substring(start + 1, start + 3).toInt()
    val minute = timeStr.substring(start + 4, start + 6).toInt()

    fun isWeekend(): Boolean {
        return when {
            timeStr.contains("星期六") -> true
            timeStr.contains("星期日") -> true
            else -> false
        }
    }

    fun isDayTime(): Boolean {
        return when {
            hour > 7 -> when {
                minute.toInt() > 30 -> false
                else -> true
            }
            hour < 19 -> true
            else -> false
        }

    }

    fun isBetween2230_2400(): Boolean {
        return when {
            hour == 22 -> when {
                minute >= 30 -> true
                else -> false
            }
            hour > 22 -> true
            else -> false
        }
    }

    fun isBetween1900_2230(): Boolean {
        //19:00~22:30
        return when {
            hour in 19..22 -> true
            hour == 22 && minute <= 30 -> true
            else -> false
        }
    }

    fun isMidnight(): Boolean {
        //24:00 --- 7:30
        return when {
            hour == 7 -> when {
                minute <= 30 -> true
                else -> false
            }
            hour < 7 -> true
            else -> false
        }
    }

    fun isMorningReadtime(): Boolean {
        //6:00--7:30
        return when {
            hour == 7 -> when {
                minute <= 30 -> true
                else -> false
            }
            hour == 6 -> true
            else -> false
        }
    }

    fun isBetween(other: Pair<Pair<Int, Int>, Pair<Int, Int>>): Boolean {
        return ((hour > other.first.first) or ((hour == other.first.first) and (minute >= other.first.second))) and ((hour < other.second.first) or ((hour == other.second.first) and (minute < other.second.second)))
    }
}


fun isAccessibleNow(currDate: filterByTime, room: Pair<String, String>): Boolean {
    //return true if is not accessible now
//    filterLog("room:"+room.first+room.second)
//    filterLog("time"+currDate.hour.toString())
    //In weekend or night(21:00--24:00)
    //西区 中院 1-2层 开放时间 7:30-22:30
    //东中院 东中院3号楼3-4层 7:30-22:30
    //东中院2号楼  7:30-22:30
    //东下院1层和205、215教室 7:30-22:30
    //东中院3号楼1-2层  7:30-24:00
    //In Midnight
    //中院114、115和东中院3-105、3-106四间教室为24小时通宵自习教室
    if (lateNightStudyRoom.contains(room)) {
        return true
    }


    if (currDate.isMidnight()) {
        filterLog("is Midnight")
        //0:00-7:30
        //可用: 中院114、115和东中院3-105、3-106
        //since I have filtered this already in filter thread.
        return lateNightStudyRoom.contains(room)
    } else if (currDate.isBetween2230_2400()) {
        filterLog("is 2230-2400")
        //22:30--24:00
        //可用: 东中院3号楼1-2层  中院114、115
        if (room.first == "东中院" && room.second[0] == '3') {
            return room.second[2] == '1' || room.second[2] == '2'
        } else if (room.first == "中院") {
            return room.second == "114" || room.second == "115"
        }
        return false
    } else if (currDate.isWeekend() || currDate.isBetween1900_2230()) {
        filterLog("is 1900-2230 or weekend")
        //19:00~22:30
        //可用: 中院1-2层, 东中院3号楼3-4层, 东中院3号楼1-2层, 东下院1层和205、215教室 , 东中院2号楼
        if (room.first == "东中院" && room.second[0] == '3') {
            return true
        } else if (room.first == "中院") {
            return room.second[0] == '1' || room.second[0] == '2'
        } else if (room.first == "东中院" && room.second[0] == '2') {
            return true
        } else if (room.first == "东下院") {
            return room.second[0] == '1' || room.second == "205" || room.second == "215"
        }
        return false
    } else {
        filterLog("is normal")
        //weekdays 7:30---19:00
        //全部可用，需要排除上课
        return true
    }
    return true
}

fun isAudioStudyroom(name: Pair<String, String>): Boolean {
    //aim to exclud audio studyroom
    //中院101-104、201-204八间教室为“有声自习室”
    //exclude 中院101-104、201-204
    var audioStudyroom: List<Pair<String, String>> = listOf(
        Pair("中院", "101"),
        Pair("中院", "102"),
        Pair("中院", "103"),
        Pair("中院", "104"),
        Pair("中院", "201"),
        Pair("中院", "202"),
        Pair("中院", "203"),
        Pair("中院", "204")
    )
    return audioStudyroom.contains(name)
}



fun getDistanceFromStudyroom(start: Location, buildingStr: String): Float {
    return when (buildingStr) {
        "上院" -> getDistanceHelper(start, UpperHall)
        "中院" -> getDistanceHelper(start, MiddleHall)
        "下院" -> getDistanceHelper(start, LowerHall)
        "东上院" -> getDistanceHelper(start, EastUpperHall)
        "东中院" -> getDistanceHelper(start, EastMiddleHall)
        "东下院" -> getDistanceHelper(start, EastLowerHall)
        else -> {
            9999F  //something mistaken in this case
        }
    }
}

fun studuroomPair2name(namepair: Pair<String, String>): String {
    var res: String = ""
    when {
        namepair.first[0] == '东' -> res += namepair.first.subSequence(0, 2)
        else -> res += namepair.first
    }
    res += namepair.second
    return res
}
