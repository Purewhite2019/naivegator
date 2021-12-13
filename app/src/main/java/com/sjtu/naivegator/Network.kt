package com.sjtu.naivegator

import android.util.Log
import com.google.gson.GsonBuilder
import com.sjtu.naivegator.api.bathroom.BathroomBean
import com.sjtu.naivegator.api.canteen.CanteenBean
import com.sjtu.naivegator.api.studyroom.StudyroomBean
import com.sjtu.naivegator.interceptor.TimeConsumeInterceptor
import com.sjtu.naivegator.filter.filterByTime
import okhttp3.*
import okhttp3.EventListener
import java.io.IOException
import java.lang.Exception
import java.lang.IllegalStateException
import java.util.*

object Network {


    val studyroomBuildIdMap = mapOf(
        "126" to "上院",
        "128" to "中院",
        "127" to "下院",
        "122" to "东上院",
        "564" to "东中院",
        "124" to "东下院"
    )

    val sectionMap = mapOf(
        1 to Pair(Pair(8, 0), Pair(8, 45)),
        2 to Pair(Pair(8, 55), Pair(9, 40)),
        3 to Pair(Pair(10, 0), Pair(10, 45)),
        4 to Pair(Pair(10, 55), Pair(11, 40)),
        5 to Pair(Pair(12, 0), Pair(12, 45)),
        6 to Pair(Pair(12, 55), Pair(13, 40)),
        7 to Pair(Pair(14, 0), Pair(14, 45)),
        8 to Pair(Pair(14, 55), Pair(15, 40)),
        9 to Pair(Pair(16, 0), Pair(16, 45)),
        10 to Pair(Pair(16, 55), Pair(17, 40)),
        11 to Pair(Pair(18, 0), Pair(18, 45)),
        12 to Pair(Pair(18, 55), Pair(19, 40)),
        13 to Pair(Pair(20, 0), Pair(20, 20)),
        14 to Pair(Pair(21, 15), Pair(22, 0)),
    )

    val okhttpListener = object : EventListener() {
        override fun dnsStart(call: Call, domainName: String) {
            super.dnsStart(call, domainName)
            Log.i("Translator", "Dns Search: $domainName")
        }

        override fun responseBodyStart(call: Call) {
            super.responseBodyStart(call)
            Log.i("Translator", "Response Start")
        }
    }

    val client: OkHttpClient = OkHttpClient
        .Builder()
        .addInterceptor(TimeConsumeInterceptor())
        .eventListener(okhttpListener).build()

    val gson = GsonBuilder().create()

    fun requestCanteen(url: String, callback: Callback) {
        val request: Request = Request.Builder()
            .url(url)
            .header("User-Agent", "NaiveGator")
            .build()
        client.newCall(request).enqueue(callback)
    }


    fun requestStudyroom(url: String, callback: Callback, id: String) {
        val builder = FormBody.Builder()
        builder.add("buildId", id)
        val formBody = builder.build()
        val request: Request = Request.Builder()
            .url(url)
            .method("POST", formBody)
            .header("User-Agent", "NaiveGator")
            .build()
        client.newCall(request).enqueue(callback)
    }

    fun requestBathroom(url: String, callback: Callback) {
        val request: Request = Request.Builder()
            .url(url)
            .header("User-Agent", "NaiveGator")
            .build()
        client.newCall(request).enqueue(callback)
    }


    fun getCanteenData(id: Int) {
        // Input：id, the id of the selected canteen (0 for overall situation)
        var url = ""
        url = if (id == 0) {
            "https://canteen.sjtu.edu.cn/CARD/Ajax/Place"
        } else {
            "https://canteen.sjtu.edu.cn/CARD/Ajax/PlaceDetails/${id}"
        }

        requestCanteen(url, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("request", e.message.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                val bodyString = response.body?.string()
                val canteenList: List<CanteenBean> =
                    gson.fromJson(bodyString, Array<CanteenBean>::class.java).toList()
                if (id == 0) {
                    for (canteen in canteenList) {
                        canteenMap[canteen.id] =
                            Triple(Pair(canteen.name, ""), canteen.seat_s, canteen.seat_u)
                    }
                } else {
                    for (canteen in canteenList) {
                        canteenMap[canteen.id] = Triple(
                            Pair(canteenMap[id]!!.first.first, canteen.name),
                            canteen.seat_s,
                            canteen.seat_u
                        )
                    }
                }
            }
        })
    }

    fun getStudyroomData(id: String) {
        // Input：id, the id of the selected canteen (0 for overall situation)
        val url = "https://ids.sjtu.edu.cn/build/findBuildRoomType"

        requestStudyroom(url, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("request", e.message.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                val bodyString = response.body?.string()
                var studyroomBean: StudyroomBean? = null
                try {
                    studyroomBean = gson.fromJson(bodyString, StudyroomBean::class.java)
                } catch (e: Exception) {
                    Log.e("JSON Parse", bodyString ?: "(Empty)")
                    return
                }

                val floorList = studyroomBean.data.floorList
                for (floor in floorList) {
                    val stuNumbList = floor.roomStuNumbs
                    val studyroomList = floor.children
                    for (studyroom in studyroomList) {
                        val buildingName = studyroomBuildIdMap[id]
                        val studyroomName = studyroom.name.replace(buildingName!!, "")
                        var stuNumb: Int = 0
                        if (stuNumbList != null) {
                            for (room in stuNumbList) {
                                if (room.roomId == studyroom.id) {
                                    stuNumb = room.actualStuNum
                                    break
                                }
                            }
                            if (studyroom.zws != null && studyroom.roomCourseList != null) {
                                var haveCourse: Boolean = false

                                for (course in studyroom.roomCourseList) {
                                    val startSection = course.startSection
                                    val endSection = course.endSection
                                    val currentTime = filterByTime()
//                                    println(currentTime.time_str)
//                                    println("${studyroom.name}:${startSection},${endSection}")
                                    haveCourse = currentTime.isBetween(
                                        Pair(
                                            sectionMap[startSection]!!.first,
                                            sectionMap[endSection]!!.second
                                        )
                                    )
//                                    println("${studyroom.name}: $haveCourse")
                                }

                                studyroomMap[Pair(buildingName, studyroomName)] = Triple(
                                    studyroom.zws,
                                    stuNumb,
                                    Pair(
                                        studyroom.sensorTemp,
                                        haveCourse
                                    )
                                )
                                if (studyroomNameMap[buildingName] == null) {
                                    studyroomNameMap[buildingName] = mutableSetOf()
                                }
                                studyroomNameMap[buildingName]!!.add(studyroomName)
                            }
                        }
                    }
                }
            }
        }, id)
    }

    fun getBathroomData(area: Char, id: Int) {
        // Input：id, the id of the selected canteen (0 for overall situation)
        val url = "https://plus.sjtu.edu.cn/api/sjtu/bathroom"

        requestCanteen(url, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("request", e.message.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                val bodyString = response.body?.string()
//                println(bodyString)
                val bathroomBean = gson.fromJson(bodyString, BathroomBean::class.java)
                var dormName = ""
                var dormNameSimple = ""
                for (dormitory in bathroomBean.data) {
                    when (area) {
                        'd', 'D', '东' -> if (("东" in dormitory.name) and (id.toString() in dormitory.name)) {
                            dormName = dormitory.name
                        }
                        'x', 'X', '西' -> if (("西" in dormitory.name) and (id.toString() in dormitory.name)) {
                            dormName = dormitory.name
                        }
                    }
                    print(dormName)
                    if (dormName == dormitory.name) {
                        bathroomInfo["${area}${id}"] = Triple(
                            first = dormitory.status_count.free + dormitory.status_count.used,
                            second = dormitory.status_count.used,
                            third = listOf(true) // 暂时未用到的接口
                        )
                        break
                    }
                }
            }
        })
    }

}