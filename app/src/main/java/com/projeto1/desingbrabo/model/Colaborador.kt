package com.projeto1.desingbrabo.model

data class Colaborador(
    val id: Int,
    val userName: String,
    val userProfile: String,
    val userCape: String,
    val userDescription: String,
    val totalSeguidores: Int,
    val totalCurtidas: Int,
    val totalDownloads: Int
)
