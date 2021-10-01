package com.racoondog.gotaplan

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import androidx.core.app.NotificationCompat



class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationIntent = Intent(context, MainActivity::class.java)

        val id = intent.getIntExtra("id",0)
        val title = intent.getStringExtra("title")

        notificationIntent.flags = (Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

        val pendingI = PendingIntent.getActivity(context, id, notificationIntent, FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(context, "123")
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .build()
        val soundUri =
            Uri.parse("android.resource://" + context.packageName.toString() + "/" + R.raw.note)
        val powerManager =
            context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE,
            "myapp:mywakelocktag"
        )

        wakeLock.acquire(3000)

        //OREO API 26 이상에서는 채널 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            builder.setSmallIcon(R.drawable.ic_skylight_notification) //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남

            val channelName = "Notification"
            val description = "NotificationChannel"
            val importance = NotificationManager.IMPORTANCE_HIGH //소리와 알림메시지를 같이 보여줌
            val channel =
                NotificationChannel("123", channelName, importance)
            channel.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            channel.description = description
            channel.setSound(soundUri,audioAttributes)
            notificationManager.createNotificationChannel(channel)

        } else builder.setSmallIcon(R.mipmap.ic_launcher) // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남


        builder.setAutoCancel(true)

            //.setDefaults(NotificationCompat.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setContentTitle(context.resources.getString(R.string.schedule_notify))
            .setContentText(title)
            //.setOngoing(true)
            //.setContentInfo("INFO")
            .setContentIntent(pendingI)
            .setSound(soundUri)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        // 노티피케이션 동작시킴
        notificationManager.notify(id, builder.build())

        val weekInterval : Long = (7 * 24 * 60 * 60 * 1000).toLong()

        val preference = context.getSharedPreferences("alarm", Context.MODE_PRIVATE)
        val time = preference.getLong("$id",0L)

        val editor = preference.edit()
        editor.putLong("$id", time+weekInterval).apply()


    }
}