package com.example.fixu.response

import com.google.gson.annotations.SerializedName

data class HistoryResponse(

	@field:SerializedName("data")
	val data: List<HistoryDataItem>,

	@field:SerializedName("message")
	val message: String
)

data class HistoryDataItem(

	@field:SerializedName("feedback")
	val feedback: String,

	@field:SerializedName("result")
	val result: String,

	@field:SerializedName("uid")
	val uid: String,

	@field:SerializedName("probability")
	val probability: Any,

	@field:SerializedName("created_at")
	val createdAt: String,

	@field:SerializedName("id")
	val id: Int
)
