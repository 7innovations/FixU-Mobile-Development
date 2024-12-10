package com.example.fixu.database

import com.google.gson.annotations.SerializedName

data class EditNoteRequest (
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String
)