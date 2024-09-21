package com.example.act3

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PoemAdapter(
    private val poems: MutableList<Poem>,
    private val deletePoemCallback: (Poem) -> Unit,
    private val updatePoemCallback: (Poem) -> Unit
) : RecyclerView.Adapter<PoemAdapter.PoemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PoemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_poem, parent, false)
        return PoemViewHolder(view)
    }

    override fun onBindViewHolder(holder: PoemViewHolder, position: Int) {
        val poem = poems[position]
        holder.bind(poem)
    }

    override fun getItemCount(): Int = poems.size


    inner class PoemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.poemNameTextView)
        private val dateTextView: TextView = itemView.findViewById(R.id.poemDateTextView)
        private val topicTextView: TextView = itemView.findViewById(R.id.poemTopicTextView)
        private val poemTextView: TextView = itemView.findViewById(R.id.poemTextView)
        private val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
        private val updateButton: Button = itemView.findViewById(R.id.updateButton)

        fun bind(poem: Poem) {
            nameTextView.text = poem.name
            dateTextView.text = poem.date
            topicTextView.text = poem.topic
            poemTextView.text = poem.poem

            deleteButton.setOnClickListener {
                val builder = AlertDialog.Builder(itemView.context)
                builder.setMessage("Do you want to delete this poem?")
                    .setPositiveButton("Yes") { _, _ ->
                        // Call the delete function
                        deletePoem(poem)
                    }
                    .setNegativeButton("No", null)
                builder.create().show()
            }

            updateButton.setOnClickListener {
                updatePoem(poem)
            }
        }

        private fun deletePoem(poem: Poem) {
            // Get the adapter position safely
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                // Call the delete callback to handle deletion from SharedPreferences
                try {
                    deletePoemCallback(poem)
                    poems.removeAt(position)  // Remove the poem from the list
                    notifyItemRemoved(position)  // Notify the adapter that an item was removed
                    notifyItemRangeChanged(position, poems.size)  // Update the range
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        private fun updatePoem(poem: Poem) {
            updatePoemCallback(poem)
        }

        // This method refreshes the list when new data is inserted or updated
        fun refreshData(newPoems: List<Poem>) {
            poems.clear()
            poems.addAll(newPoems)
            notifyDataSetChanged()  // Notify the adapter that the data has changed
        }


    }
}
