package com.example.fixu.retrofit

import com.example.fixu.response.MLResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {

    @FormUrlEncoded
    @POST("/predict/professional")
    fun postProfessionalAnswers(
        @Field("Gender") gender: String,
        @Field("Age") age: Int,
        @Field("Work Pressure") workPressure: Int,
        @Field("Job Satisfaction") jobSatisfaction: Int,
        @Field("Sleep Duration") sleepDuration: String,
        @Field("Dietary Habits") dietaryHabits: String,
        @Field("Have you ever had suicidal thoughts ?") suicidalThoughts: String,
        @Field("Work Hours") workHours: Int,
        @Field("Financial Stress") financialStress: Int,
        @Field("Family History of Mental Illness") familyHistory: String
    ): Call<MLResponse>

    @FormUrlEncoded
    @POST("/predict/student")
    fun postStudentAnswers(
        @Field("Gender") gender: String,
        @Field("Age") age: Int,
        @Field("Academic Pressure") workPressure: Int,
        @Field("Study Satisfaction") jobSatisfaction: Int,
        @Field("Sleep Duration") sleepDuration: String,
        @Field("Dietary Habits") dietaryHabits: String,
        @Field("Have you ever had suicidal thoughts ?") suicidalThoughts: String,
        @Field("Study Hours") workHours: Int,
        @Field("Financial Stress") financialStress: Int,
        @Field("Family History of Mental Illness") familyHistory: String
    ): Call<MLResponse>


}