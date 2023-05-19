package com.example.storyapp.data.api.response

import com.google.gson.annotations.SerializedName

data class StoryListResponse(

	@field:SerializedName("listStory")
	val listStory: List<ListStoryItem?>? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class ListStoryItem(

	@field:SerializedName("photoUrl")
	var photoUrl: String? = null,

	@field:SerializedName("createdAt")
	var createdAt: String? = null,

	@field:SerializedName("name")
	var name: String? = null,

	@field:SerializedName("description")
	var description: String? = null,

	@field:SerializedName("lon")
	var lon: Any? = null,

	@field:SerializedName("id")
	var id: String? = null,

	@field:SerializedName("lat")
	var lat: Any? = null
)
