package com.example.fixu.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class Question(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val status: String,
    val questionText: String,
    val answerType: String,
    val options: List<String>?
)
