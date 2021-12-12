package com.sjtu.naivegator

import android.content.Intent
import android.content.Context
import java.text.SimpleDateFormat

import android.R
import android.app.*
import android.content.res.Resources

import android.graphics.BitmapFactory

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.text.ParseException


private const val ACTION_SET_ALARM = "com.sjtu.naivegator.action.SetAlarm"

private const val PARAM_START_TIME = "com.sjtu.naivegator.extra.START_TIME"
private const val PARAM_END_TIME = "com.sjtu.naivegator.extra.END_TIME"
private const val PARAM_POSITION = "com.sjtu.naivegator.extra.POSITION"
private const val PARAM_CROWDNESS = "com.sjtu.naivegator.extra.CROWDNESS"

private var currentId = 0

class BathroomAlarmService : IntentService("BathroomAlarmService") {
    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            ACTION_SET_ALARM -> {
                val startTime = intent.getStringExtra(PARAM_START_TIME)!!
                val endTime = intent.getStringExtra(PARAM_END_TIME)!!
                val position = intent.getStringExtra(PARAM_POSITION)!!
                val crowdness = intent.getStringExtra(PARAM_CROWDNESS)!!
                handleActionSetAlarm(startTime, endTime, position, crowdness)
            }
        }
    }

    private fun handleActionSetAlarm(
        startTime: String,
        endTime: String,
        position: String,
        crowdness: String
    ) {
        val id = currentId + 1
        currentId = id

        val startTimeStamp = startTime.toLong()
        val endTimeStamp = endTime.toLong()
        val crownessLimit = crowdness.toInt()

//        Log.d("startTimeStamp", "$startTimeStamp")
//        Log.d("endTimeStamp", "$endTimeStamp")

        while (currentId == id) {
//            Log.d("curTimeStamp", "${System.currentTimeMillis()}")
            Network.getBathroomData(
                position[0],
                position.subSequence(1, position.length).toString().toInt()
            )
            Thread.sleep(5000)
            val info = bathroomInfo[position]

            if (System.currentTimeMillis() > endTimeStamp) {
                val channelID = "BathroomAlarmTimedout"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val importance = NotificationManager.IMPORTANCE_DEFAULT
                    val channel = NotificationChannel(
                        channelID,
                        "BathroomNotification",
                        importance
                    ).apply {
                        description =
                            "${position}, ${startTime}~${endTime}, crowdness <= ${crowdness}"
                    }
                    val notificationManager: NotificationManager =
                        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.createNotificationChannel(channel)
                }
                val bathroomContent =
                    if (info == null) "浴室容量查询失败"
                    else "目前浴室容量：${info.second}/${info.first}"

                val builder = NotificationCompat.Builder(this, channelID)
                    .setSmallIcon(com.sjtu.naivegator.R.drawable.naivegator)
                    .setContentTitle("Naivegator")
                    .setContentText("${bathroomContent}，超时了！")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)

                with(NotificationManagerCompat.from(this)) {
                    notify(id, builder.build())
                }
                Log.d("BathroomService", "End time exceeded")
                break
            }

            if (info != null && System.currentTimeMillis() > startTimeStamp && info.second * (100 - crownessLimit) <= info.first * crownessLimit) {
                val channelID = "BathroomAlarmSucceed"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val importance = NotificationManager.IMPORTANCE_DEFAULT
                    val channel = NotificationChannel(
                        channelID,
                        "BathroomNotification",
                        importance
                    ).apply {
                        description =
                            "${position}, ${startTime}~${endTime}, crowdness <= ${crowdness}"
                    }
                    val notificationManager: NotificationManager =
                        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.createNotificationChannel(channel)
                }
                val builder = NotificationCompat.Builder(this, channelID)
                    .setSmallIcon(com.sjtu.naivegator.R.drawable.naivegator)
                    .setContentTitle("Naivegator")
                    .setContentText("目前浴室容量：${info.second}/${info.first}，可以洗澡了！")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)

                with(NotificationManagerCompat.from(this)) {
                    notify(id, builder.build())
                }
                Log.d("BathroomService", "Notification sent")
                break
            }
        }
    }

    companion object {
        @JvmStatic
        fun startActionSetAlarm(
            context: Context,
            startTime: String,
            endTime: String,
            position: String,
            crowdness: String
        ) {
            val intent = Intent(context, BathroomAlarmService::class.java).apply {
                action = ACTION_SET_ALARM
                putExtra(PARAM_START_TIME, startTime)
                putExtra(PARAM_END_TIME, endTime)
                putExtra(PARAM_POSITION, position)
                putExtra(PARAM_CROWDNESS, crowdness)
            }
            context.startService(intent)
        }
    }
}