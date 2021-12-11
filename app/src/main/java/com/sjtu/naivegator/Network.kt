package com.sjtu.naivegator

import android.util.Log
import com.google.gson.GsonBuilder
import com.sjtu.naivegator.api.canteen.CanteenBean
import com.sjtu.naivegator.api.studyroom.StudyroomBean
import com.sjtu.naivegator.interceptor.TimeConsumeInterceptor
import okhttp3.*
import java.io.IOException

object Network {


    val studyroomBuildIdMap = mapOf(
        "126" to "上院",
        "128" to "中院",
        "127" to "下院",
        "122" to "东上院",
        "564" to "东中院",
        "124" to "东下院"
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
                val studyroomBean = gson.fromJson(bodyString, StudyroomBean::class.java)
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
                            if (studyroom.zws != null) {
                                studyroomMap.put(
                                    Pair(buildingName, studyroomName),
                                    Triple(
                                        studyroom.zws,
                                        stuNumb,
                                        studyroom.sensorTemp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }, id)


    }
}