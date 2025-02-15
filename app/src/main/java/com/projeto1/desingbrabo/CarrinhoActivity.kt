package com.projeto1.desingbrabo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.projeto1.desingbrabo.data.AppDatabase
import com.projeto1.desingbrabo.data.CartItem
import com.projeto1.desingbrabo.data.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CarrinhoActivity : AppCompatActivity() {

    private lateinit var cartAdapter: CartAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela_carrinho)

        recyclerView = findViewById(R.id.recyclerViewcarrinho)
        recyclerView.layoutManager = LinearLayoutManager(this)

        cartAdapter = CartAdapter(emptyList(), this@CarrinhoActivity)
        recyclerView.adapter = cartAdapter

        loadCartItems()
    }

    private fun loadCartItems() {
        val db = AppDatabase.getDatabase(this)
        lifecycleScope.launch(Dispatchers.IO) {
            val cartItems = db.cartDao().getAllCartItems()
            val productsWithQuantity = cartItems.map { cartItem ->
                val product = db.productDao().getProductById(cartItem.productId)
                Pair(product, cartItem.quantity)
            }
            withContext(Dispatchers.Main) {
                cartAdapter.updateData(productsWithQuantity)
            }
        }
    }
}