package com.projeto1.desingbrabo.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items")
    fun getAllCartItems(): List<CartItem>

    @Query("SELECT * FROM cart_items WHERE productId = :productId")
    fun getCartItemByProductId(productId: Int): CartItem?

    @Insert
    fun insertCartItem(cartItem: CartItem)

    @Query("UPDATE cart_items SET quantity = quantity + 1 WHERE productId = :productId")
    fun incrementQuantity(productId: Int)

    @Query("DELETE FROM cart_items WHERE productId = :productId")
    fun removeCartItem(productId: Int)
}