package com.example.fixu.retrofit

import com.example.fixu.database.AnswersStudent
import com.example.fixu.database.AnswersProfessional
import com.example.fixu.database.LoginRequest
import com.example.fixu.database.SignUpRequest
import com.example.fixu.response.HistoryResponse
import com.example.fixu.response.LoginResponse
import com.example.fixu.response.MLResponse
import com.example.fixu.response.SignUpResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("/predict/professional/result")
    fun postProfessionalAnswers(
        @Body answers: AnswersProfessional
    ): Call<MLResponse>

    @POST("/predict/student/result")
    fun postStudentAnswers(
        @Body answers: AnswersStudent
    ): Call<MLResponse>

    @POST("/auth/login")
    fun login(
        @Body loginRequest: LoginRequest
    ): Call<LoginResponse>

    @POST("/auth/signup")
    fun signUp(
        @Body signUpRequest: SignUpRequest
    ): Call<SignUpResponse>

    @GET("/history")
    fun getHistory(): Call<HistoryResponse>

}