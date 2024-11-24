package com.example.fixu.response

import com.google.gson.annotations.SerializedName

data class MLResponse(

	@field:SerializedName("feedback")
	val feedback: String,

	@field:SerializedName("result")
	val result: String,

	@field:SerializedName("probability")
	val probability: Any
)
