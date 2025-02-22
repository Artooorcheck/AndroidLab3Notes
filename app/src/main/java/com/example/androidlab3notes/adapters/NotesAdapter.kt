package com.example.androidlab3notes.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.androidlab3notes.R
import com.example.androidlab3notes.data.DataProvider
import com.example.androidlab3notes.models.NoteModel

class NotesAdapter(var activity: Activity): RecyclerView.Adapter<NotesAdapter.ContactViewHolder>(){
    private var items = arrayOf<NoteModel>()
    private lateinit var provider: DataProvider
    private var onSelectItem: ((NoteModel) -> Unit)? = null

    class ContactViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val isCompleteTask = view.findViewById<CheckBox>(R.id.is_complete_task)
        val titleArea = view.findViewById<TextView>(R.id.title_area)
        val selectButton = view.findViewById<LinearLayout>(R.id.note_item)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        provider = DataProvider(recyclerView.context, recyclerView){newItems ->
            items = newItems
            activity.runOnUiThread{
                notifyDataSetChanged()
            }
        }
        provider.run()

        items = provider.getLocalNotes().map { a ->
            a.value
        }.toTypedArray()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_note_item, parent, false)
        return ContactViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  items.count()
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.titleArea.text = items[position].title

        holder.isCompleteTask.setOnCheckedChangeListener{ _, _ -> }

        holder.isCompleteTask.isChecked = items[position].isChecked ?: false

        holder.isCompleteTask.setOnCheckedChangeListener { _, isChecked ->
            val item = items[position]
            provider.updateNotes(listOf(NoteModel(item.id, item.title, item.content, item.timeStamp, isChecked, item.disabled)))
        }


        holder.selectButton.setOnClickListener {
            onSelectItem?.invoke(items[position])
        }
    }

    fun setOnClickItemListener(action: (NoteModel) -> Unit){
        onSelectItem = action
    }

    fun stop() {
        provider.stop()
    }
}