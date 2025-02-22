package com.example.androidlab3notes.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import androidx.fragment.app.FragmentActivity
import com.example.androidlab3notes.R
import com.example.androidlab3notes.data.DataProvider
import com.example.androidlab3notes.models.NoteModel
import java.util.Calendar
import java.util.Date

class NoteViewFragment(val note: NoteModel) : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_note_view, container, false)
        val calendar = view.findViewById<CalendarView>(R.id.calendar_view)
        val titleArea = view.findViewById<EditText>(R.id.title_area)
        val contentArea = view.findViewById<EditText>(R.id.content_area)
        val saveButton = view.findViewById<Button>(R.id.save_button)

        calendar.isClickable = !note.disabled
        calendar.isFocusable = !note.disabled
        titleArea.isEnabled = !note.disabled
        contentArea.isEnabled = !note.disabled
        saveButton.setText(if (note.disabled) "Close" else "Save")


        calendar.date = note.timeStamp.time
        contentArea.setText(note.content)
        titleArea.setText(note.title)

        calendar.setOnDateChangeListener{cal, year, mon, day ->
            val converter = Calendar.getInstance()
            converter.set(year, mon, day) // Month is zero-based (0 = January)
            cal.date = converter.timeInMillis
        }

        saveButton.setOnClickListener{
            if(!note.disabled) {
                val dataProvider = DataProvider(requireContext(), view) { }
                dataProvider.updateNotes(listOf(NoteModel(note.id, titleArea.text.toString(), contentArea.text.toString(), Date(calendar.date), note.isChecked, note.disabled)))
            }

            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.note_item, NoteListFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        return view
    }
}