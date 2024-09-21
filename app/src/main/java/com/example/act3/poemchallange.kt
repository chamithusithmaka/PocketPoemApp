package com.example.act3

import PoemDataHelper
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer

import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class poemchallange : AppCompatActivity() {

    lateinit var nameEditText: EditText
    lateinit var dateEditText: EditText
    lateinit var topicEditText: EditText
    lateinit var poemEditText: EditText
    lateinit var submitButton: Button
    lateinit var backButton: Button

    lateinit var sharedPrefHelper: PoemDataHelper  // Use SharedPreferences helper

    // Declare UI elements
    private lateinit var timerTextView: TextView
    private lateinit var startButton: Button
    private lateinit var stopButton: Button

    // Timer variables
    private lateinit var countDownTimer: CountDownTimer
    private var isTimerRunning = false
    private var totalTimeInMillis: Long = 60000 // 1 minute

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_poemchallange)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize the SharedPreferences helper
        sharedPrefHelper = PoemDataHelper(this)

        // Initialize UI elements
        timerTextView = findViewById(R.id.timerTextView)
        startButton = findViewById(R.id.startButton)
        stopButton = findViewById(R.id.stopButton)
        val resetButton: Button = findViewById(R.id.resetbtn)

        // Set initial time display
        timerTextView.text = String.format("%02d:%02d", 1, 0) // 1 minute

        val remainingTime = sharedPrefHelper.getRemainingTime()
        if (remainingTime > 0) {
            totalTimeInMillis = remainingTime
            updateTimerTextView(remainingTime) // Update display to remaining time
        } else {
            totalTimeInMillis = 60000 // Reset to 1 minute
            updateTimerTextView(totalTimeInMillis) // Update display to show 1 minute
        }

        // Set up Start button listener
        startButton.setOnClickListener {
            if (!isTimerRunning) {
                startTimer()
            }
        }

        // Set up Stop button listener
        stopButton.setOnClickListener {
            if (isTimerRunning) {
                stopTimer()
            }
        }

        // Set up Reset button listener
        resetButton.setOnClickListener {
            resetTimer() // Call the resetTimer function
        }

        // Initialize other fields
        nameEditText = findViewById(R.id.nameEditText)
        dateEditText = findViewById(R.id.dateEditText)
        topicEditText = findViewById(R.id.topicEditText)
        poemEditText = findViewById(R.id.poemEditText)
        submitButton = findViewById(R.id.submitButton)
        backButton = findViewById(R.id.backButton)

        // Submit Button Click Listener
        submitButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val date = dateEditText.text.toString().trim()
            val topic = topicEditText.text.toString().trim()
            val poem = poemEditText.text.toString().trim()

            // Input validation (optional)
            if (name.isNotEmpty() && date.isNotEmpty() && topic.isNotEmpty() && poem.isNotEmpty()) {
                val isInserted: Boolean = sharedPrefHelper.addPoem(name, date, topic, poem)

                if (isInserted) {
                    Toast.makeText(this, "Poem added successfully!", Toast.LENGTH_SHORT).show()
                    clearFields() // Clear the fields after saving
                } else {
                    Toast.makeText(this, "Failed to add poem", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Back Button Click Listener
        backButton.setOnClickListener {
            finish()  // Go back to the previous activity
        }
    }

    // Function to update timer display
    private fun updateTimerTextView(millis: Long) {
        val seconds = (millis / 1000) % 60
        val minutes = (millis / 1000) / 60
        timerTextView.text = String.format("%02d:%02d", minutes, seconds)
    }


    private var remainingTime: Long = 0L  // Class-level variable to store remaining time
    private var remainingTimeInMillis: Long = totalTimeInMillis  // Remaining time (starts as total time)

    // Function to start or resume the timer
    private fun startTimer() {
        isTimerRunning = true

        countDownTimer = object : CountDownTimer(remainingTimeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTimeInMillis = millisUntilFinished  // Save the remaining time
                val secondsRemaining = remainingTimeInMillis / 1000
                timerTextView.text = String.format("%02d:%02d", secondsRemaining / 60, secondsRemaining % 60)
            }

            override fun onFinish() {
                isTimerRunning = false
                remainingTimeInMillis = totalTimeInMillis  // Reset the remaining time
                timerTextView.text = "00:00"
                Toast.makeText(this@poemchallange, "Time's up!", Toast.LENGTH_SHORT).show()
            }
        }.start()
    }

    // Function to stop the timer
    private fun stopTimer() {
        if (isTimerRunning) {
            countDownTimer.cancel()  // Stop the timer
            isTimerRunning = false
        }
    }

    private fun resetTimer() {
        // Stop the timer if it's running
        if (isTimerRunning) {
            stopTimer()
        }

        // Reset totalTimeInMillis to 1 minute
        totalTimeInMillis = 60000 // 1 minute in milliseconds

        // Update the timer display
        updateTimerTextView(totalTimeInMillis)

        // Save the reset time to SharedPreferences
        sharedPrefHelper.saveRemainingTime(totalTimeInMillis)

        // Set isTimerRunning to false
        isTimerRunning = false
    }



    private fun clearFields() {
        nameEditText.text.clear()
        dateEditText.text.clear()
        topicEditText.text.clear()
        poemEditText.text.clear()
    }
    // onPause method to save the state when the activity is paused
    override fun onPause() {
        super.onPause()
        if (isTimerRunning) {
            sharedPrefHelper.saveRemainingTime(remainingTimeInMillis)  // Save the remaining time
            stopTimer()  // Stop the timer when the activity is paused
        }
    }
    // onResume method to restore the state when the activity is resumed
    override fun onResume() {
        super.onResume()

        // Retrieve saved time or set to totalTimeInMillis if nothing is saved
        remainingTimeInMillis = sharedPrefHelper.getRemainingTime()

        // If no remaining time is saved (first app launch), set it to the initial total time
        if (remainingTimeInMillis == 0L) {
            remainingTimeInMillis = totalTimeInMillis  // Default to 1 minute if no time is saved
        }

        // Update the timer text view to reflect the restored time
        val secondsRemaining = remainingTimeInMillis / 1000
        timerTextView.text = String.format("%02d:%02d", secondsRemaining / 60, secondsRemaining % 60)
    }

    private fun loadDraft() {
        val draft = sharedPrefHelper.getDraft()
        nameEditText.setText(draft["name"] ?: "")
        dateEditText.setText(draft["date"] ?: "")
        topicEditText.setText(draft["topic"] ?: "")
        poemEditText.setText(draft["poem"] ?: "")
    }

    private fun saveDraft() {
        val name = nameEditText.text.toString().trim()
        val date = dateEditText.text.toString().trim()
        val topic = topicEditText.text.toString().trim()
        val poem = poemEditText.text.toString().trim()

        sharedPrefHelper.saveDraft(name, date, topic, poem)
    }


}