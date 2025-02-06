package com.projeto1.desingbrabo.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ProductDao {
    @Query("SELECT * FROM products")
    fun getAllProducts(): List<Product>

    @Query("SELECT * FROM products WHERE id = :productId")
    fun getProductById(productId: Int): Product

    @Insert
    fun insertProduct(product: Product)
}