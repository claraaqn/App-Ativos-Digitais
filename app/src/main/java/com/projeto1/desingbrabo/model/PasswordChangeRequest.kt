package com.projeto1.desingbrabo.model

data class PasswordChangeRequest(
    val userId: Int,
    val senha_atual: String,
    val nova_senha: String
)
