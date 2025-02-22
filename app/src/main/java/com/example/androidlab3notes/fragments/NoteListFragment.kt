package com.example.androidlab3notes.fragments

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.androidlab3notes.R
import com.example.androidlab3notes.adapters.NotesAdapter
import com.example.androidlab3notes.models.NoteModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Date

class NoteListFragment () : Fragment() {

    private lateinit var view: View
    private lateinit var adapter: NotesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_note_list, container, false)

        val addButton = view.findViewById<FloatingActionButton>(R.id.add_button)


        addButton.setOnClickListener{ note ->
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.note_item, NoteViewFragment(NoteModel("-1", "", "", Date(), null, false)))
            transaction.addToBackStack(null)
            transaction.commit()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        adapter = NotesAdapter(requireActivity())
        val noteContainer = view.findViewById<RecyclerView>(R.id.notes_container)
        noteContainer.layoutManager = LinearLayoutManager(context)
        noteContainer.adapter = adapter
        adapter.setOnClickItemListener { noteModel ->
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.note_item, NoteViewFragment(noteModel))
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    override fun onPause() {
        super.onPause()
        adapter.stop()
    }
}