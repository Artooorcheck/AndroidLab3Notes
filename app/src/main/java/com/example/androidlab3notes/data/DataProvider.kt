package com.example.androidlab3notes.data

import android.app.Notification.Action
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.androidlab3notes.jsons.NotesResponse
import com.example.androidlab3notes.models.NoteModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class DataProvider(val context: Context, val view: View, val onUpdated: (Array<NoteModel>) -> Unit) {

    private val client = OkHttpClient()
    private var isRunning = false

    fun run() {
        isRunning = true
        startRepeatingTask()
    }

    fun stop() {
        isRunning = false
    }

    private fun sendRequest() {
        val request = Request.Builder()
            .url("https://v78qr.wiremockapi.cloud/notes")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Snackbar.make(view, e.message.toString(), Snackbar.LENGTH_LONG)
                    .show();
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    for ((name, value) in response.headers) {
                        println("$name: $value")
                    }

                    val json = response.body!!.string()
                    val notesList: List<NoteModel> = Gson().fromJson(json, NotesResponse::class.java).notes.map { item ->
                        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).parse(item.timestamp)
                            ?.let { date ->
                                NoteModel(item.id.toString(), item.title, item.content,
                                    date, null, true)
                            } as NoteModel
                    }
                    updateNotes(notesList);
                    onUpdated.invoke(getLocalNotes().map { a -> a.value }.toTypedArray())
                }
            }
        })
    }

    fun updateNotes(notes: List<NoteModel>) {
        val localNotes = getLocalNotes().toMutableMap()


        notes.forEach { item ->
            var isChecked: Boolean? = null
            var id = item.id
            if(id == "-1") {
                id = Date().time.toString();
            }
            if(localNotes.containsKey(item.id)) {
                isChecked = localNotes[item.id]!!.isChecked
            }
            localNotes[id] = NoteModel(id, item.title, item.content, item.timeStamp, item.isChecked ?: isChecked, item.disabled)
        }

        val newNotesList = localNotes.map { a ->
            a.value
        }.toTypedArray()

        val json = Gson().toJson(newNotesList);
        context
            .getSharedPreferences("app_storage", Context.MODE_PRIVATE)
            .edit().putString("notes", json).apply();
        Log.d("loaded json", newNotesList.map { "${it.id}: ${it.isChecked}" }.joinToString(", "))
    }

    fun getLocalNotes(): Map<String, NoteModel> {
        val json = context.getSharedPreferences("app_storage", Context.MODE_PRIVATE).getString("notes", "");
        var list = Gson().fromJson(json, Array<NoteModel>::class.java)

        if(list == null)
            list = arrayOf<NoteModel>()

        val map = list.associateBy { it.id }

        return map
    }

    fun startRepeatingTask() {
        CoroutineScope(Dispatchers.IO).launch {
            while (isRunning) {
                sendRequest()
                delay(60_000L)
            }
        }
    }
}