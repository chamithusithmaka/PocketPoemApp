import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.sql.Date

class PoemDataHelper(context: Context) {

    // Save the latest poem topic
    fun saveLatestPoemTopic(topic: String) {
        sharedPreferences.edit().putString("latest_poem_topic", topic).apply()
    }

    // Get the latest poem topic
    fun getLatestPoemTopic(): String? {
        return sharedPreferences.getString("latest_poem_topic", "No new challenges")
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveRemainingTime(time: Long) {
        sharedPreferences.edit().putLong("remaining_time", time).apply()
    }

    fun getRemainingTime(): Long {
        return sharedPreferences.getLong("remaining_time", 0L)
    }

    companion object {
        const val PREF_NAME = "poemData"
        const val POEMS_KEY = "poems"
    }

    // Function to add a poem to SharedPreferences
    fun addPoem(name: String, date: String, topic: String, poem: String): Boolean {
        val poemsArray = getAllPoemsArray()
        val newPoem = JSONObject().apply {
            put("id", generatePoemId())
            put("name", name)
            put("date", date)
            put("topic", topic)
            put("poem", poem)
        }
        poemsArray.put(newPoem)

        return savePoemsArray(poemsArray)
    }

    private lateinit var sharedPrefHelper: PoemDataHelper  // Use SharedPreferences helper

    // Function to get all poems as JSONArray
    private fun getAllPoemsArray(): JSONArray {
        val poemsString = sharedPreferences.getString(POEMS_KEY, "[]") ?: "[]"
        return JSONArray(poemsString)
    }

    // Function to get all poems as List of JSONObjects
    fun getAllPoems(): List<JSONObject> {
        val poemsArray = getAllPoemsArray()
        val poemsList = mutableListOf<JSONObject>()
        for (i in 0 until poemsArray.length()) {
            poemsList.add(poemsArray.getJSONObject(i))
        }
        return poemsList
    }

    // Function to get a poem by its ID
    fun getPoemById(id: Long): JSONObject? {
        val poemsArray = getAllPoemsArray()
        for (i in 0 until poemsArray.length()) {
            val poem = poemsArray.getJSONObject(i)
            if (poem.getLong("id") == id) {
                return poem
            }
        }
        return null
    }

    // Function to update a poem by its ID
    fun updatePoem(id: Long, newTopic: String, newPoem: String,newDate: String,newName: String): Boolean {
        val poemsArray = getAllPoemsArray()
        for (i in 0 until poemsArray.length()) {
            val poem = poemsArray.getJSONObject(i)
            if (poem.getLong("id") == id) {
                poem.put("topic", newTopic)
                poem.put("poem", newPoem)
                poem.put("name", newDate)
                poem.put("date", newName)

                return savePoemsArray(poemsArray)
            }
        }
        return false
    }

    // Function to delete a poem by its ID
    fun deletePoem(poemId: Int): Boolean {
        val poemsArray = getAllPoemsArray()
        val updatedPoems = JSONArray()
        var poemFound = false

        for (i in 0 until poemsArray.length()) {
            val poem = poemsArray.getJSONObject(i)
            if (poem.getLong("id").toInt() != poemId) {
                updatedPoems.put(poem)
            } else {
                poemFound = true
            }
        }

        return if (poemFound) savePoemsArray(updatedPoems) else false
    }

    // Helper function to save poems back to SharedPreferences
    private fun savePoemsArray(poemsArray: JSONArray): Boolean {
        val editor = sharedPreferences.edit()
        editor.putString(POEMS_KEY, poemsArray.toString())
        return editor.commit()
    }

    // Function to generate a unique ID for each poem
    private fun generatePoemId(): Long {
        val currentPoems = getAllPoemsArray()
        var maxId = 0L
        for (i in 0 until currentPoems.length()) {
            val poem = currentPoems.getJSONObject(i)
            if (poem.getLong("id") > maxId) {
                maxId = poem.getLong("id")
            }
        }
        return maxId + 1
    }

    // Save draft method
    fun saveDraft(name: String, date: String, topic: String, poem: String) {
        sharedPreferences.edit()
            .putString("draft_name", name)
            .putString("draft_date", date)
            .putString("draft_topic", topic)
            .putString("draft_poem", poem)
            .apply()
    }

    // Retrieve draft method
    fun getDraft(): Map<String, String> {
        val name = sharedPreferences.getString("draft_name", "") ?: ""
        val date = sharedPreferences.getString("draft_date", "") ?: ""
        val topic = sharedPreferences.getString("draft_topic", "") ?: ""
        val poem = sharedPreferences.getString("draft_poem", "") ?: ""

        return mapOf("name" to name, "date" to date, "topic" to topic, "poem" to poem)
    }
}
