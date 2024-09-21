package com.example.act3

import PoemDataHelper
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray

class mypoems : AppCompatActivity() {

    private lateinit var poemRecyclerView: RecyclerView
    private lateinit var backButton: Button
    private lateinit var sharedPrefHelper: PoemDataHelper
    private lateinit var poemAdapter: PoemAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypoems)

        poemRecyclerView = findViewById(R.id.poemRecyclerView)
        backButton = findViewById(R.id.backButton)
        sharedPrefHelper = PoemDataHelper(this)

        poemRecyclerView.layoutManager = LinearLayoutManager(this)
        val poems = getPoemsFromPreferences()

        // Set up adapter with specific removal and update callbacks
        poemAdapter = PoemAdapter(poems, { poem ->
            val position = poems.indexOf(poem)
            if (position != -1) {
                sharedPrefHelper.deletePoem(poem.id.toInt())
                poems.removeAt(position)
                poemAdapter.notifyItemRemoved(position)
            }
        }, { poem ->
            navigateToUpdatePoem(poem)
        })
        poemRecyclerView.adapter = poemAdapter

        backButton.setOnClickListener {
            finish()  // Close activity and go back
        }

    }

    // Load poems from SharedPreferences
    private fun getPoemsFromPreferences(): MutableList<Poem> {
        val poems = mutableListOf<Poem>()
        val poemData = sharedPrefHelper.getAllPoems()

        if (poemData.isNotEmpty()) {
            val jsonArray = JSONArray(poemData)

            for (i in 0 until jsonArray.length()) {
                val jsonPoem = jsonArray.getJSONObject(i)
                val id = jsonPoem.getLong("id")
                val name = jsonPoem.getString("name")
                val date = jsonPoem.getString("date")
                val topic = jsonPoem.getString("topic")
                val poemText = jsonPoem.getString("poem")
                poems.add(Poem(id, name, date, topic, poemText))
            }
        }
        return poems
    }

    private fun navigateToUpdatePoem(poem: Poem) {
        val intent = Intent(this, update_poem::class.java)
        intent.putExtra("POEM_ID", poem.id)
        intent.putExtra("POEM_NAME", poem.name)
        intent.putExtra("POEM_DATE", poem.date)
        intent.putExtra("POEM_TOPIC", poem.topic)
        intent.putExtra("POEM_TEXT", poem.poem)
        startActivity(intent)
    }


}
