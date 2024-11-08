package com.example.storyapp

import com.example.storyapp.data.local.ListStoryItem
import java.util.Random
import java.util.UUID

object DataDummy {

    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 1..100) {
            val story = ListStoryItem(
                photoUrl = "https://picsum.photos/200/300?random=$i", // random URL image
                createdAt = "2024-10-${i % 31 + 1}T12:34:56Z", // random date
                name = "Story $i", // story with number
                description = "This is a description for Story $i", // story description
                lon = (100.0..140.0).random(), // random longitude
                id = UUID.randomUUID().toString(), // random ID
                lat = (-10.0..10.0).random() // random latitude
            )
            items.add(story)
        }
        return items
    }

    private fun ClosedRange<Double>.random() =
        Random().nextDouble() * (endInclusive - start) + start
}

