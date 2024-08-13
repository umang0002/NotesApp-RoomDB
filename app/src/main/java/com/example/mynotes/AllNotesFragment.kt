package com.example.mynotes

import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.*
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.*
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.mynotes.database.NotesViewModel
import com.example.mynotes.databinding.FragmentAllNotesBinding
import com.google.android.material.snackbar.Snackbar
import com.ncorti.slidetoact.SlideToActView
import kotlin.properties.Delegates


@Suppress("DEPRECATION")
class AllNotesFragment : Fragment() {

    private var fbind : FragmentAllNotesBinding? = null
    private lateinit var noteViewModel : NotesViewModel
    private val sharedPrefKey = "appSettings"
    private val nightModeKey = "NightMode"
    lateinit var appPref : SharedPreferences
    lateinit var sharedPrefEdit : SharedPreferences.Editor
    var nightModeStatus by Delegates.notNull<Int>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_notes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentAllNotesBinding.bind(view)
        fbind = binding
        noteViewModel = ViewModelProvider(this).get(NotesViewModel::class.java)

        appPref = activity?.getSharedPreferences(sharedPrefKey, 0)!!
        nightModeStatus = appPref.getInt("NightMode", 3)

        setTheme(nightModeStatus)

        val adapter = activity?.applicationContext.let { NotesAdapter() }
        binding.noteRecycler.adapter = adapter
        binding.noteRecycler.setHasFixedSize(true)
        binding.noteRecycler.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        noteViewModel.allNotes.observe(viewLifecycleOwner, androidx.lifecycle.Observer{ notes ->
            adapter?.submitList(notes)
        })

        binding.newNoteFAB.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_allNotesFragment_to_edit_fragment2)
        }

        val itemTouchHelperCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val mNote = adapter?.getNote(viewHolder.adapterPosition)
                mNote?.let { noteViewModel.deleteNote(it) }

                if (mNote != null) {
                    Snackbar.make(view, "Note Deleted", Snackbar.LENGTH_LONG)
                        .setAction("Undo") {
                            noteViewModel.insertNote(mNote)
                        }.show()
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.noteRecycler)

    }

    override fun onDestroy() {
        super.onDestroy()
        fbind = null
    }

    private fun setTheme(nightStatus : Int) {
        when (nightStatus) {
            1 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                Log.d("AllNoteFrag", "Light theme SetThem()")
            }
            2 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                Log.d("AllNotFrag", "Dark theme SetTheme(")
            }
            else -> {
                Log.d("AllNoteFrag", "System theme SetThem()")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                else
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
            }
        }
    }

    private fun setThemeDialog() {
        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.alert_diialog_theme_select, null)
        val dialog = AlertDialog.Builder(activity)
            .setView(view)
            .create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        val themeRadioGroup = view.findViewById<RadioGroup>(R.id.theme_button_group)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            view.findViewById<RadioButton>(R.id.defaultRadioButton).text =
                getString(R.string.system_default)
        } else {
            view.findViewById<RadioButton>(R.id.defaultRadioButton).text =
                getString(R.string.follow_battery_saver)
        }

        when (nightModeStatus) {
            1 -> view.findViewById<RadioButton>(R.id.lightRadioButton).isChecked = true
            2 -> view.findViewById<RadioButton>(R.id.darkRadioButton).isChecked = true
            3 -> view.findViewById<RadioButton>(R.id.defaultRadioButton).isChecked = true
        }

        themeRadioGroup.setOnCheckedChangeListener { _, id ->
            sharedPrefEdit = appPref.edit()
            when (id) {
                R.id.lightRadioButton -> {
                    sharedPrefEdit.putInt(nightModeKey, 1)
                    sharedPrefEdit.apply()
                        nightModeStatus = 1
                        Log.d("AllNoteFrag", "Light theme")
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                R.id.dark_mode_menu -> {
                    sharedPrefEdit.putInt(nightModeKey, 2)
                    sharedPrefEdit.apply()
                    nightModeStatus = 2
                    Log.d("AllNoteFrag", "Dark theme")
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
                R.id.defaultRadioButton -> {
                    sharedPrefEdit.putInt(nightModeKey, 3)
                    sharedPrefEdit.apply()
                    nightModeStatus = 3
                    Log.d("AllNotFrag", "System theme")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    )
                    else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                }
            }
            dialog.dismiss()
        }
    }

    private fun deleteALLDialog() {

        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.alert_dialog_delete_all, null)
        val slide = view.findViewById<SlideToActView>(R.id.slideConfirm)
        val dialog = AlertDialog.Builder(activity)
            .setView(view)
            .create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        slide.onSlideCompleteListener = object  : SlideToActView.OnSlideCompleteListener {

            override fun onSlideComplete(view: SlideToActView) {
                Log.d("Test", "Deleted")

                deleteALL()
                dialog.dismiss()
            }
        }
    }

    private fun deleteALL(){
        noteViewModel.deleteAllNote()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            R.id.delete_all_menu -> deleteALLDialog()
            R.id.dark_mode_menu -> setThemeDialog()
        }

        return super.onOptionsItemSelected(item)
    }

}