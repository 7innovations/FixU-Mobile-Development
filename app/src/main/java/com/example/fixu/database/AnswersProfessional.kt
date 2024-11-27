package com.example.fixu.database

import com.google.gson.annotations.SerializedName

data class AnswersProfessional(
    @SerializedName("Gender") val gender: String,
    @SerializedName("Age") val age: Int,
    @SerializedName("Work Pressure") val workPressure: Int,
    @SerializedName("Job Satisfaction") val jobSatisfaction: Int,
    @SerializedName("Sleep Duration") val sleepDuration: String,
    @SerializedName("Dietary Habits") val dietaryHabits: String,
    @SerializedName("Have you ever had suicidal thoughts ?") val suicidalThoughts: String,
    @SerializedName("Work Hours") val workHours: Int,
    @SerializedName("Financial Stress") val financialStress: Int,
    @SerializedName("Family History of Mental Illness") val familyHistory: String
)
