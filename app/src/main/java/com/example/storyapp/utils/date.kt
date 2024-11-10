package com.example.storyapp.utils

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

fun Date(createdAt: String): String {
    return try {
        val inputFormat = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            Locale.US
        )
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")

        val outputFormat = SimpleDateFormat(
            "yyyy-MM-dd 'Jam' HH.mm",
            Locale.getDefault()
        )
        outputFormat.timeZone = TimeZone.getDefault()

        val date = inputFormat.parse(createdAt)
        outputFormat.format(date!!)
    } catch (e: Exception) {
        "Invalid date"
    }
}