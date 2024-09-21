package com.example.act3

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat

class AlarmReceiver : BroadcastReceiver() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onReceive(context: Context, intent: Intent) {
        val message = intent.getStringExtra("message") ?: "Your reminder!"
        sendNotification(context, message)

        // Play custom sound
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        mediaPlayer = MediaPlayer.create(context, soundUri)
        mediaPlayer?.start()
        mediaPlayer?.setOnCompletionListener {
            it.release() // Release the MediaPlayer when done
        }

    }

    private fun sendNotification(context: Context, message: String) {
        val channelId = "reminderChannel"
        val notificationManager = context.getSystemService(NotificationManager::class.java)

        // Create Notification Channel if it doesn't exist
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = notificationManager.getNotificationChannel(channelId) ?: run {
                val newChannel = NotificationChannel(
                    channelId,
                    "Reminder Notifications",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM), null)
                }
                notificationManager.createNotificationChannel(newChannel)
                newChannel
            }
        }

        // Create an intent for the notification click action
        val intent = Intent(context, ReminderActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Reminder")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)) // Set the sound for the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(1, notificationBuilder.build())
    }
    // Method to stop the alarm
    fun stopAlarm() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
