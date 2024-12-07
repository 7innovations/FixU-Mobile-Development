package com.example.fixu.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(

	@field:SerializedName("expiresIn")
	val expiresIn: Long,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("user")
	val user: User,

	@field:SerializedName("token")
	val token: String
)

data class User(

	@field:SerializedName("whatsapp")
	val whatsapp: String,

	@field:SerializedName("uid")
	val uid: String,

	@field:SerializedName("fullname")
	val fullname: String,

	@field:SerializedName("email")
	val email: String
)
