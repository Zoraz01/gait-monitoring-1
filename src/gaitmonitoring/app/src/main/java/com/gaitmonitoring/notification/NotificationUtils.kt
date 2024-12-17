package com.gaitmonitoring.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.gaitmonitoring.R
import com.gaitmonitoring.utils.isApi26

class NotificationUtils(private val context: Context) {

    /* for api 26 and more */
    fun createNotificationChannel(
    ) {
        if (isApi26) {
            val channelId = context.packageName
            val name = context.getString(R.string.notification_channel_name)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(channelId, name, importance).apply {
                description = context.getString(R.string.notification_channel_description)
                lightColor = android.graphics.Color.RED
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }



    fun getNotification(
    ): Notification {
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, context.packageName)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(context.getString(R.string.connection_state))
                .setAutoCancel(false)
        builder.setAutoCancel(true)
        return builder.build()
    }

}


