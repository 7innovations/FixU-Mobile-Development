package com.example.fixu.response

import com.google.gson.annotations.SerializedName

data class MLResponse(

	@field:SerializedName("result")
	val result: Result,

	@field:SerializedName("message")
	val message: String
)

data class Result(

	@field:SerializedName("feedback")
	val feedback: String,

	@field:SerializedName("result")
	val result: String,

	@field:SerializedName("probability")
	val probability: Any
)
