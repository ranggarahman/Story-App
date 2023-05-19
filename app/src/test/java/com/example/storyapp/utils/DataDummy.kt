package com.example.storyapp.utils

import com.example.storyapp.data.api.response.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                "url $i",
                "author + $i",
                "quote $i",
                "desc $i",
                "lon $i",
                "id $i",
                "lat $i"
            )
            items.add(story)
        }
        return items
    }
}