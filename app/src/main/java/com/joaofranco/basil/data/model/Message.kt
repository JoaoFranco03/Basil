package com.joaofranco.basil.data

data class Message(
    val content: String,
    val isUser: Boolean,
    val isLoading: Boolean = false
)
