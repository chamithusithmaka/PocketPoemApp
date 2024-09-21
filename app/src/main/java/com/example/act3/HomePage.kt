package com.example.act3

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HomePage : AppCompatActivity() {

   private lateinit var writePoemButton: Button
   private lateinit var mypoemsview: Button
   private lateinit var challange:Button
   private lateinit var reminder:Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        writePoemButton = findViewById(R.id.writePoem)
        mypoemsview = findViewById(R.id.mypoem)
        challange = findViewById(R.id.challange)
        reminder = findViewById(R.id.reminder)
        // Write Poem Button Click Listener
        writePoemButton.setOnClickListener {
            val intent = Intent(this, writeAPoem::class.java)
            startActivity(intent)
        }
        mypoemsview.setOnClickListener {
            val intent = Intent(this, mypoems::class.java)
            startActivity(intent)
        }

        challange.setOnClickListener {
            val intent = Intent(this, poemchallange::class.java)
            startActivity(intent)
        }
        reminder.setOnClickListener {
            val intent = Intent(this, ReminderActivity::class.java)
            startActivity(intent)
        }
    }
}