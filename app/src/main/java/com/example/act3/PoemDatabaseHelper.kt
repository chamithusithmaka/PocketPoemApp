import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class PoemDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        public const val DATABASE_NAME = "poemDatabase.db"
        public const val DATABASE_VERSION = 1

        // Table and columns
        public const val TABLE_NAME = "Poems"
        public const val COLUMN_ID = "id"
        public const val COLUMN_NAME = "name"
        public const val COLUMN_DATE = "date"
        public const val COLUMN_TOPIC = "topic"
        public const val COLUMN_POEM = "poem"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = ("CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT, "
                + COLUMN_DATE + " TEXT, "
                + COLUMN_TOPIC + " TEXT, "
                + COLUMN_POEM + " TEXT)")
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Function to add a poem to the database
    fun addPoem(name: String, date: String, topic: String, poem: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_NAME, name)
        contentValues.put(COLUMN_DATE, date)
        contentValues.put(COLUMN_TOPIC, topic)
        contentValues.put(COLUMN_POEM, poem)

        val result = db.insert(TABLE_NAME, null, contentValues)
        db.close()

        return result != -1L // If the insertion is successful, result will not be -1
    }
    // Function to get all poems
    fun getAllPoems(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME", null)
    }

    // Function to get a poem by its ID
    fun getPoemById(id: Long): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = ?", arrayOf(id.toString()))
    }

    // Function to update poem topic and poem text by its ID
    fun updatePoem(id: Long, newTopic: String, newPoem: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_TOPIC, newTopic)
            put(COLUMN_POEM, newPoem)
        }

        // Update the row with the given ID
        val result = db.update(
            TABLE_NAME,           // Table name
            contentValues,        // Updated values
            "$COLUMN_ID = ?",     // WHERE clause
            arrayOf(id.toString()) // WHERE arguments
        )

        db.close()
        return result > 0 // Return true if the update was successful
    }

    // Function to delete a poem from the database
    fun deletePoem(poemId: Int): Boolean {
        val db = this.writableDatabase
        val result = db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(poemId.toString()))
        db.close()
        return result > 0  // Return true if the deletion was successful
    }
}
