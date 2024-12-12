package com.example.fixu.database

import com.google.gson.annotations.SerializedName

data class AnswersStudent(
    @SerializedName("Academic Pressure") val academicPressure: Int,
    @SerializedName("Age") val age: Int,
    @SerializedName("Dietary Habits") val dietaryHabits: String,
    @SerializedName("Family History of Mental Illness") val familyHistory: String,
    @SerializedName("Financial Stress") val financialStress: Int,
    @SerializedName("Gender") val gender: String,
    @SerializedName("Have you ever had suicidal thoughts ?") val suicidalThoughts: String,
    @SerializedName("Sleep Duration") val sleepDuration: String,
    @SerializedName("Study Hours") val studyHours: Int,
    @SerializedName("Study Satisfaction") val studySatisfaction: Int
)
