package com.example.act3
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ReminderService : Service() {

    private val executor = Executors.newSingleThreadScheduledExecutor()

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel() // Create notification channel
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val timeInMinutes = intent?.getLongExtra("timeInMinutes", 0) ?: 0
        val message = intent?.getStringExtra("message") ?: "Reminder!"

        // Schedule a task to send a notification after the specified time
        executor.schedule({
            sendNotification(message)
            stopSelf() // Stop the service after sending the notification
        }, timeInMinutes, TimeUnit.MINUTES)

        startForegroundNotification(message) // Optional: Start as a foreground service

        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "reminderChannel"
            val channelName = "Reminder Channel"
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun sendNotification(message: String) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            val notificationBuilder = NotificationCompat.Builder(this, "reminderChannel")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Reminder")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)

            val notificationManager = NotificationManagerCompat.from(this)
            notificationManager.notify(1, notificationBuilder.build())
        } else {
            // Handle the case where the permission is not granted
            // Consider notifying the user or logging this event
        }
    }

    private fun startForegroundNotification(message: String) {
        val notificationBuilder = NotificationCompat.Builder(this, "reminderChannel")
            .setContentTitle("Reminder Service")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)

        // Start foreground service with the notification
        startForeground(1, notificationBuilder.build())
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        executor.shutdown() // Shut down the executor to avoid memory leaks
    }


}
