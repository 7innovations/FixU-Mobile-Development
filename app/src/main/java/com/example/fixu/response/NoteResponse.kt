package com.example.fixu.response

import com.google.gson.annotations.SerializedName

data class NoteResponse(

	@field:SerializedName("data")
	val data: List<NoteDataItem>,

	@field:SerializedName("message")
	val message: String
)

data class NoteDataItem(

	@field:SerializedName("uid")
	val uid: String,

	@field:SerializedName("updated_at")
	val updatedAt: String,

	@field:SerializedName("created_at")
	val createdAt: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("title")
	val title: String,

	@field:SerializedName("content")
	val content: String
)

data class PostNoteResponse(
	@field:SerializedName("message")
	val message: String
)

data class PatchNoteResponse(
	@field:SerializedName("message")
	val message: String
)

data class DeleteNoteResponse(
	@field:SerializedName("message")
	val message: String
)