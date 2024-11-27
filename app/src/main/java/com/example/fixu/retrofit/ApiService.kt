package com.example.fixu.retrofit

import com.example.fixu.database.AnswersStudent
import com.example.fixu.database.AnswersProfessional
import com.example.fixu.response.MLResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("/predict/professional")
    fun postProfessionalAnswers(
        @Body answers: AnswersProfessional
    ): Call<MLResponse>

    @POST("/predict/student")
    fun postStudentAnswers(
        @Body answers: AnswersStudent
    ): Call<MLResponse>

}