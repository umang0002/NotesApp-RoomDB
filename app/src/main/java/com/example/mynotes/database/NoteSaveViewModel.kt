package com.example.mynotes.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteSaveViewModel(application: Application) : AndroidViewModel(application) {

    private val repository : DatabaseRepository

    init {
        val notesDao = NotesRoomDatabase.getDatabase(application, viewModelScope).notesDao()
        repository = DatabaseRepository(notesDao)
    }

    fun insertNote(note : Notes) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertNote(note)
    }

    fun updateNote(note: Notes) = viewModelScope.launch ( Dispatchers.IO) {
        repository.updateNote(note)

    }
}