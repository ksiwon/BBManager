package com.example.bbmanager.adapters

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


data class Message(
    val sender: String,
    val message: String,
    val timestamp: String = getCurrentTime()
)

fun getCurrentTime(): String {
    val dateFormat = SimpleDateFormat("HH:MM", Locale.getDefault())
    return dateFormat.format(Date())
}