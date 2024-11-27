package com.example.fixu.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface QuestionDao {
    // Mengambil pertanyaan berdasarkan status
    @Query("SELECT * FROM questions WHERE status = :status")
    suspend fun getQuestionsByStatus(status: String): List<Question>

    // Menyisipkan beberapa pertanyaan sekaligus
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<Question>)
}
