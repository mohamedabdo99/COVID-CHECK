package com.mohamed.abdo.myhealth.pojo

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.mohamed.abdo.myhealth.R
import com.mohamed.abdo.myhealth.ui.notifi.NotificationActivity

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {
        val i = Intent(p0,NotificationActivity::class.java)
        p1!!.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(p0,0,i,0)
        val builder = NotificationCompat.Builder(p0!!,"mohamed")
            .setContentTitle("Corona Virus")
            .setContentText("Stay save")
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.logo)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        val notificationManger = NotificationManagerCompat.from(p0)
        notificationManger.notify(123,builder.build())

    }
}