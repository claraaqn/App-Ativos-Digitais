package com.projeto1.desingbrabo.model

data class GetPerfil(    val id: Int,
    val userName: String,
    val userProfile: String,
    val userEmail: String,
    val userCape: String,
    val userDescription: String,
    val totalSeguidores: Int,
    val totalCurtidas: Int,
    val totalDownloads: Int,
    val totalViews: Int,
    val totalRecursos:Int
)
