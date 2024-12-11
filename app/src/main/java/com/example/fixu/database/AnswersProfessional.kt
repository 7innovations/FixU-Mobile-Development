package com.example.fixu.database

import com.google.gson.annotations.SerializedName

data class AnswersProfessional(
    @SerializedName("Age") val age: Int,
    @SerializedName("Dietary Habits") val dietaryHabits: String,
    @SerializedName("Family History of Mental Illness") val familyHistory: String,
    @SerializedName("Financial Stress") val financialStress: Int,
    @SerializedName("Gender") val gender: String,
    @SerializedName("Have you ever had suicidal thoughts ?") val suicidalThoughts: String,
    @SerializedName("Job Satisfaction") val jobSatisfaction: Int,
    @SerializedName("Sleep Duration") val sleepDuration: String,
    @SerializedName("Work Hours") val workHours: Int,
    @SerializedName("Work Pressure") val workPressure: Int
)
