package com.example.androidlab3notes.models

import java.util.Date

class NoteModel(val id: String, val title: String, val content: String, val timeStamp: Date, val isChecked: Boolean?, val disabled: Boolean)