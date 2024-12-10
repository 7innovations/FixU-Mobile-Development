package com.example.fixu.database

import com.google.gson.annotations.SerializedName

data class AddNoteRequest(
    @SerializedName("uid") val uid: String,
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String
)
