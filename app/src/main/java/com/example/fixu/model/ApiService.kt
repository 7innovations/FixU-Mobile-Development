package com.example.fixu.model

import com.example.fixu.database.AnswersStudent
import com.example.fixu.database.AnswersProfessional
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {

    @POST("/predict/professional")
    fun postProfessionalAnswers(
        @Body answers: AnswersProfessional
    ): Call<>

    @POST("/predict/student")
    fun postStudentAnswers(
        @Body answers: AnswersStudent
    ): Call<>

}