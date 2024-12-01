package com.projeto1.desingbrabo.model

data class LoginResponse(
    val success: Boolean,
    val userId: Int?,
    val message: String,
    val nome: String?,
    val email: String?
)