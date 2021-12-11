package com.sjtu.naivegator

import android.util.Log
import com.google.gson.GsonBuilder
import com.sjtu.naivegator.api.canteen.CanteenBean
import com.sjtu.naivegator.interceptor.TimeConsumeInterceptor
import okhttp3.*
import java.io.IOException

object Network {

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

    fun request(url: String, callback: Callback) {
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

        request(url, object : Callback {
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
}