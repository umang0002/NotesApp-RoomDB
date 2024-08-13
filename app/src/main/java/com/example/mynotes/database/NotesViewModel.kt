package com.example.mynotes.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotesViewModel (application: Application) : AndroidViewModel(application) {

    private val repository : DatabaseRepository
    val allNotes : LiveData<List<Notes>>

    init {
        val notesDao = NotesRoomDatabase.getDatabase(application, viewModelScope).notesDao()
        repository = DatabaseRepository(notesDao)
        allNotes = repository.allNotes
    }

    fun insertNote(notes: Notes) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertNote(notes)
    }

    fun deleteNote(notes: Notes) = viewModelScope.launch (Dispatchers.IO) {
        repository.deleteNote(notes)
    }

    fun deleteAllNote() = viewModelScope.launch (Dispatchers.IO) {
        repository.deleteAllNote()
    }

}