package com.projeto1.desingbrabo.model

data class ResetPasswordRequest(
    val token: String,
    val newPassword: String
)
