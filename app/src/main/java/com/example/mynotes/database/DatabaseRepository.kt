package com.example.mynotes.database

import androidx.lifecycle.LiveData

class DatabaseRepository(private val notesDao: NotesDao) {

    val allNotes : LiveData<List<Notes>> = notesDao.getAllNotes()

    suspend fun insertNote(notes: Notes) {
        notesDao.insertNote(notes)
    }

    suspend fun updateNote(notes: Notes) {
        notesDao.updateNote(notes)
    }

    suspend fun deleteNote(notes: Notes) {
        notesDao.deleteNotes(notes)
    }

    suspend fun deleteAllNote() {
        notesDao.deleteAllNote()
    }
}