package com.projeto1.desingbrabo.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey val id: Int,
    val name: String,
    val price: String,
    val type: String,
    val owner: String,
    val imageUrl: String
)
