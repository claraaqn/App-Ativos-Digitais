package com.projeto1.desingbrabo.model

// Create this in your model package (com.projeto1.desingbrabo.model)
data class GetPerfil(
    val userName: String?,
    val userEmail: String?,
    val userDescription: String?,
    val userProfile: String?,
    val userCape: String?,
    val totalSeguidores: Int,
    val totalDownloads: Int,
    val totalCurtidas: Int,
    val totalViews: Int,
    val totalRecursos: Int
)
