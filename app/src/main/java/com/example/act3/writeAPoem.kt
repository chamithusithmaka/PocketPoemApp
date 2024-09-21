package com.example.act3

import PoemDataHelper
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONObject

class writeAPoem : AppCompatActivity() {

    lateinit var nameEditText: EditText
    lateinit var dateEditText: EditText
    lateinit var topicEditText: EditText
    lateinit var poemEditText: EditText
    lateinit var submitButton: Button
    private lateinit var challengeEditText: EditText
    private lateinit var sharedPrefHelper: PoemDataHelper
    lateinit var backButton: Button

      // Use SharedPreferences helper

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_write_apoem)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        // Initialize the views
        nameEditText = findViewById(R.id.nameEditText)
        dateEditText = findViewById(R.id.dateEditText)
        topicEditText = findViewById(R.id.topicEditText)
        poemEditText = findViewById(R.id.poemEditText)
        submitButton = findViewById(R.id.submitButton)
        backButton = findViewById(R.id.backButton)

        // Initialize the SharedPreferences helper
        sharedPrefHelper = PoemDataHelper(this)

        //set challnage on widget
        val challengeTopicEditText: EditText = findViewById(R.id.challangeTopic)
        val addChallengeButton: Button = findViewById(R.id.addchallange)

        // Set click listener for the button
        addChallengeButton.setOnClickListener {
            val challengeTopic = challengeTopicEditText.text.toString().trim()

            if (challengeTopic.isNotEmpty()) {
                // Save the challenge topic
                sharedPrefHelper.saveLatestPoemTopic(challengeTopic)

                // Notify the widget to update after saving the new topic
                val updateIntent = Intent(this, PoemWidgetProvider::class.java)
                updateIntent.action = "UPDATE_WIDGET"
                sendBroadcast(updateIntent)

                Toast.makeText(this, "Challenge topic set!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enter a challenge topic", Toast.LENGTH_SHORT).show()
            }
        }

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

    private fun clearFields() {
        nameEditText.text.clear()
        dateEditText.text.clear()
        topicEditText.text.clear()
        poemEditText.text.clear()
    }
}
