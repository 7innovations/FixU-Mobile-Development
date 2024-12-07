package com.example.fixu.database

import com.google.gson.annotations.SerializedName

data class SignUpRequest(
    @SerializedName("fullname") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("whatsapp") val whatsapp: String,
    @SerializedName("password") val password: String,
    @SerializedName("confirm_password") val confirmPassword: String
)
