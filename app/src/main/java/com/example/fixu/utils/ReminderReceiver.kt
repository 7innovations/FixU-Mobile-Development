package com.example.fixu.utils

import android.content.BroadcastReceiver
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.fixu.R


class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("ReminderReceiver", "onReceive triggered")

        if (intent == null) {
            Log.d("ReminderReceiver", "Intent is null")
        } else {
            Log.d("ReminderReceiver", "Intent received: ${intent.action}")
        }

        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "daily_reminder_channel"
        val channel = NotificationChannel(
            channelId,
            "Daily Reminder",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Channel for daily reminders"
        }
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Diary Daily Reminder")
            .setContentText("How you feel today? let's note it down in your diary note!")
            .setSmallIcon(R.drawable.ic_fixu_logo_light)
            .build()

        notificationManager.notify(1, notification)
    }

}