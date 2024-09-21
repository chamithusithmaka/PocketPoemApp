package com.example.act3
import android.provider.Settings
import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*

class ReminderActivity : AppCompatActivity() {

    private lateinit var etReminderTime: EditText
    private lateinit var etMessage: EditText
    private lateinit var tvFeedback: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var alarmManager: AlarmManager

    private lateinit var backButton: Button
    private lateinit var btnSetReminder: Button
    private lateinit var btnCancelReminder: Button

    companion object {
        const val REQUEST_CODE = 1
        const val REQUEST_SCHEDULE_EXACT_ALARM = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)

        backButton = findViewById(R.id.back_btn)
        etReminderTime = findViewById(R.id.et_reminder_time)
        etMessage = findViewById(R.id.et_reminder_message)
        tvFeedback = findViewById(R.id.tv_feedback)
        btnSetReminder = findViewById(R.id.btn_set_reminder)
        btnCancelReminder = findViewById(R.id.btn_cancel_reminder)

        sharedPreferences = getSharedPreferences("ReminderPrefs", Context.MODE_PRIVATE)
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        btnSetReminder.setOnClickListener {
            if (checkPermission()) {
                if (canScheduleExactAlarms()) {
                    setReminder()
                } else {
                    requestExactAlarmPermission()
                }
            } else {
                requestPermission()
            }
        }

        btnCancelReminder.setOnClickListener {
            cancelAlarm()
            tvFeedback.text = "Reminder cancelled"
        }

        backButton.setOnClickListener {
            finish()  // Go back to the previous activity
        }


    }

    private fun setReminder() {
        val timeInMinutes = etReminderTime.text.toString().toLongOrNull()
        val message = etMessage.text.toString()

        if (timeInMinutes != null && timeInMinutes > 0) {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.MINUTE, timeInMinutes.toInt())
            setAlarm(calendar.timeInMillis, message)
            tvFeedback.text = "Reminder set for $timeInMinutes minutes"
        } else {
            tvFeedback.text = "Please enter a valid time in minutes."
        }
    }

    private fun setAlarm(timeInMillis: Long, message: String) {
        val intent = Intent(this, AlarmReceiver::class.java).apply {
            putExtra("message", message)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
        displayAlarmStatus(timeInMillis)
    }

    private fun cancelAlarm() {
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    private fun displayAlarmStatus(timeInMillis: Long) {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val alarmTime = sdf.format(Date(timeInMillis))
        tvFeedback.text = "Alarm set for: $alarmTime"
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            REQUEST_CODE
        )
    }

    private fun canScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else {
            true // Prior versions do not need the permission check
        }
    }

    private fun requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            startActivity(intent)
        } else {
            // Fallback for older versions (optional)
            tvFeedback.text = "Exact alarms not needed for this version."
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                tvFeedback.text = "Permission granted. Set your reminder."
            } else {
                tvFeedback.text = "Permission denied. Unable to set reminders."
            }
        }
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            // Stop the alarm when volume buttons are pressed
            AlarmReceiver().stopAlarm()
            return true // Indicate that the event has been handled
        }
        return super.onKeyDown(keyCode, event)
    }
}
