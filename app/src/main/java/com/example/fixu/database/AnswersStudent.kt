package com.example.fixu.database

import com.google.gson.annotations.SerializedName

data class AnswersStudent(
    @SerializedName("uid") val userId: String,
    @SerializedName("Gender") val gender: String,
    @SerializedName("Age") val age: Int,
    @SerializedName("Academic Pressure") val academicPressure: Int,
    @SerializedName("Study Satisfaction") val studySatisfaction: Int,
    @SerializedName("Sleep Duration") val sleepDuration: String,
    @SerializedName("Dietary Habits") val dietaryHabits: String,
    @SerializedName("Have you ever had suicidal thoughts ?") val suicidalThoughts: String,
    @SerializedName("Study Hours") val studyHours: Int,
    @SerializedName("Financial Stress") val financialStress: Int,
    @SerializedName("Family History of Mental Illness") val familyHistory: String
)
