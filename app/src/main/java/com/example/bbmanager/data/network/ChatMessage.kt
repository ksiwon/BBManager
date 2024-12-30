package com.example.bbmanager.data.network

data class ChatMessage(
    val content: String,  // 메시지 내용
    val isUser: Boolean   // 사용자가 보낸 메시지 여부
)
