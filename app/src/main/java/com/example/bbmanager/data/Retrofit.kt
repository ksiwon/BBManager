package com.example.bbmanager.data

data class GPTRequest(
    val model: String,
    val messages: List<Message>
)

data class Message(
    val role: String,
    val content: String
)

data class GPTResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)
