package com.projeto1.desingbrabo.model

data class User(
    val userName: String,
    val email: String,
    val password: String,
    val userPhone: String,
    val isVerify: Boolean,
    val userProfile: String? = null,    // Pode ser nulo
    val userCape: String? = null,       // Pode ser nulo
    val id_endereco: Int? = null,       // Pode ser nulo
    val userDescription: String? = null // Pode ser nulo
)
