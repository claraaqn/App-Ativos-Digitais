package com.projeto1.desingbrabo.model

data class UpdateProfileRequest(
    val userId: Int,
    val nome: String,
    val email: String,
    val phone: String
)
