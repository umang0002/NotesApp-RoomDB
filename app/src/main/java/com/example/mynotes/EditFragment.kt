package com.example.mynotes

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.mynotes.database.NoteSaveViewModel
import com.example.mynotes.database.Notes
import com.example.mynotes.databinding.FragmentEditFragmentBinding
import java.text.SimpleDateFormat
import java.util.Date

class EditFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_fragment, container, false)
    }

    private lateinit var notesSaveViewModel : NoteSaveViewModel
    private var fbind : FragmentEditFragmentBinding? = null
    private var note : Notes? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentEditFragmentBinding.bind(view)
        fbind = binding
        notesSaveViewModel = ViewModelProvider(this).get(NoteSaveViewModel::class.java)

        arguments?.let {
            note = EditFragmentArgs.fromBundle(it).updateNotes
            binding.titleEdittext.setText(note?.title)
            binding.descEditText.setText(note?.description)
            if (note != null) {
                binding.lastEditText.text = setDate(note!!.date)
            }
        }

        binding.saveFAB.setOnClickListener {
            val title = binding.titleEdittext.text.toString()
            val desc = binding.descEditText.text.toString()
            if (title.isBlank() and desc.isBlank()) {
                Toast.makeText(activity, " Title and Note is Empty", Toast.LENGTH_SHORT).show()
            } else {
                val mNote = Notes(title = title, description = desc)
                if (note == null) {
                    notesSaveViewModel.insertNote(mNote)
                    Toast.makeText(activity, "Note Saved!", Toast.LENGTH_LONG).show()
                } else {
                    mNote.noteId = note!!.noteId
                    mNote.date = System.currentTimeMillis()

                    notesSaveViewModel.updateNote(mNote)
                    Toast.makeText(activity, "Note Updated!!", Toast.LENGTH_SHORT).show()
                }
                activity?.let { it1 -> hideKeyboard(it1) }
                Navigation.findNavController(view).navigateUp()
            }
        }
    }

    private fun setDate(date : Long) : CharSequence? {
        val currentMilli = System.currentTimeMillis()
        val result: String
        val sdf = SimpleDateFormat("MMM dd")
        val actualDate = Date(date)
        val currentDate = Date(currentMilli)
        if (sdf.format(currentDate).toString() != sdf.format(actualDate).toString())
            result = "Edited".plus(sdf.format(actualDate))
        else
            result = "Edited".plus(SimpleDateFormat("hh:mm a").format(actualDate))

        return result
    }

    private fun hideKeyboard(activity : Activity) {
        val inputManager = activity
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        //Check if no view has focus
        val currentFocusView = activity.currentFocus
        if (currentFocusView != null) {
            inputManager.hideSoftInputFromWindow(
                currentFocusView.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fbind = null
    }

}