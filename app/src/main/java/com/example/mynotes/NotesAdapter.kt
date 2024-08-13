package com.example.mynotes

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mynotes.database.Notes
import com.example.mynotes.databinding.NotesLayoutBinding

class NotesAdapter : ListAdapter<Notes, NotesAdapter.NotesViewHolder>(NotesDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    fun getNote(position: Int): Notes = getItem(position)


    class NotesViewHolder private constructor(val binding: NotesLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Notes) {
            binding.noteTitle.text = item.title
            binding.noteDesc.text = item.description
            if (binding.noteTitle.text.isBlank())
                binding.noteTitle.visibility = View.GONE
            else
                binding.noteTitle.visibility = View.VISIBLE
            binding.noteConstraintLayout.setOnClickListener {
                val action = AllNotesFragmentDirections.actionAllNotesFragmentToEditFragment2()
                action.updateNotes = item
                Navigation.findNavController(it).navigate(action)
            }

            binding.noteConstraintLayout.setOnLongClickListener {
                Log.d("Adapter", item.noteId.toString())
                return@setOnLongClickListener true
            }
        }

        companion object {
            fun from(parent: ViewGroup): NotesViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = NotesLayoutBinding.inflate(layoutInflater, parent, false)
                return NotesViewHolder(binding)
            }
        }
    }
}

class NotesDiffCallback  : DiffUtil.ItemCallback<Notes> (){
    override fun areContentsTheSame(oldItem: Notes, newItem: Notes): Boolean {
            return oldItem.noteId == newItem.noteId
    }

    override fun areItemsTheSame(oldItem: Notes, newItem: Notes): Boolean {
            return oldItem == newItem
    }
}
