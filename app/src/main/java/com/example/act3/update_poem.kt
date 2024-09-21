package com.example.act3

import PoemDataHelper
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject

class update_poem : AppCompatActivity() {

    private lateinit var poemNameEditText: EditText
    private lateinit var poemDateEditText: EditText
    private lateinit var poemTopicEditText: EditText
    private lateinit var poemTextEditText: EditText
    private lateinit var updateButton: Button
    private lateinit var poemDataHelper: PoemDataHelper
    lateinit var backButton: Button
    private var poemId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_poem)

        // Initialize views
        poemNameEditText = findViewById(R.id.nameEditText)
        poemDateEditText = findViewById(R.id.dateEditText)
        poemTopicEditText = findViewById(R.id.topicEditText)
        poemTextEditText = findViewById(R.id.poemEditText)
        updateButton = findViewById(R.id.updateButton)
        poemDataHelper = PoemDataHelper(this)
        backButton = findViewById(R.id.backButton)

        // Retrieve passed poem data from the intent
        poemId = intent.getLongExtra("POEM_ID", 0)
        val poemName = intent.getStringExtra("POEM_NAME") ?: ""
        val poemDate = intent.getStringExtra("POEM_DATE") ?: ""
        val poemTopic = intent.getStringExtra("POEM_TOPIC") ?: ""
        val poemText = intent.getStringExtra("POEM_TEXT") ?: ""

        // Populate the fields with the existing data
        poemNameEditText.setText(poemName)
        poemDateEditText.setText(poemDate)
        poemTopicEditText.setText(poemTopic)
        poemTextEditText.setText(poemText)

        // Set up update button click listener
        updateButton.setOnClickListener {
            val updatedPoemName = poemNameEditText.text.toString()  // Convert to string
            val updatedPoemDate = poemDateEditText.text.toString()
            val updatedTopic = poemTopicEditText.text.toString()
            val updatedPoemText = poemTextEditText.text.toString()

            val success = poemDataHelper.updatePoem(poemId, updatedTopic, updatedPoemText, updatedPoemDate, updatedPoemName)
            if (success) {
                // Indicate success to the user and close the activity
                Toast.makeText(this, "Poem updated successfully!", Toast.LENGTH_SHORT).show()
                finish()  // Close the activity and return to the previous screen
            } else {
                // Indicate failure with a log and a Toast
                Log.e("UpdatePoem", "Error updating poem")
                Toast.makeText(this, "Failed to update the poem. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }
        // Back Button Click Listener
        backButton.setOnClickListener {
            finish()  // Go back to the previous activity
        }
    }
}
