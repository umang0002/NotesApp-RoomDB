package com.example.mynotes.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Notes::class], version = 1, exportSchema = false)
abstract class NotesRoomDatabase : RoomDatabase() {

    abstract fun notesDao() : NotesDao

    companion object {

        @Volatile
        private var INSTANCE : NotesRoomDatabase? = null

        fun getDatabase(context: Context, scope : CoroutineScope) : NotesRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NotesRoomDatabase::class.java,
                    "note_database"
                ).addCallback(NoteDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }

    private class NoteDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.notesDao())
                }
            }
        }

        suspend fun populateDatabase(noteDao: NotesDao) {
            noteDao.insertNote(Notes(title = "Note Tip #4", description = "Enjoy your day"))
            noteDao.insertNote(Notes(title = "Notes Tip #3", description = "This is Second Description"))
            noteDao.insertNote(Notes(title = "Notes Tip #2", description = "This is Second Description"))
            noteDao.insertNote(Notes(title = "Notes Tip #1", description = "This is Second Description"))
        }
    }






}