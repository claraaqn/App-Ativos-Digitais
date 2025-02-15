package com.projeto1.desingbrabo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
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

class CarrinhoActivity : AppCompatActivity(), CartAdapter.OnItemRemovedListener {

    private lateinit var cartAdapter: CartAdapter
    private lateinit var valorTotal: TextView
    private lateinit var selectAllCheckBox: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela_carrinho)

        valorTotal = findViewById(R.id.valor_total)
        selectAllCheckBox = findViewById(R.id.checkBox)

        val buttonVoltar: Button = findViewById(R.id.button_voltar)

        cartAdapter = CartAdapter(emptyList(), this@CarrinhoActivity)
        cartAdapter.setOnItemRemovedListener(this@CarrinhoActivity)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewcarrinho)
        recyclerView.layoutManager = LinearLayoutManager(this@CarrinhoActivity)
        recyclerView.adapter = cartAdapter

        selectAllCheckBox.setOnCheckedChangeListener { _, isChecked ->
            cartAdapter.selectAllItems(isChecked)
        }

        loadCartItems()

        buttonVoltar.setOnClickListener { finish() }

        findViewById<Button>(R.id.button_home).setOnClickListener {
            startActivity(Intent(this@CarrinhoActivity, HomeActivity::class.java))
        }

        findViewById<Button>(R.id.button_perfil).setOnClickListener {
            startActivity(Intent(this@CarrinhoActivity, PerfilActivity::class.java))
        }

        findViewById<Button>(R.id.button_meus_produtos).setOnClickListener {
            startActivity(Intent(this@CarrinhoActivity, DownloadActivity::class.java))
        }

        findViewById<Button>(R.id.button_explorar).setOnClickListener {
            startActivity(Intent(this@CarrinhoActivity, ExplorarActivity::class.java))
        }

        findViewById<Button>(R.id.button_carrinho).setOnClickListener {
            startActivity(Intent(this@CarrinhoActivity, CarrinhoActivity::class.java))
        }
    }

    private fun loadCartItems() {
        val db = AppDatabase.getDatabase(this@CarrinhoActivity)
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
    override fun onItemRemoved(productId: Int, wasSelected: Boolean) {
        if (wasSelected) {
            updateTotalValue()
        }
        cartAdapter.checkIfAllItemsRemoved()
    }
    override fun onItemSelectionChanged(totalValue: Double) {
        valorTotal.text = "R$ %.2f".format(totalValue)
    }
    override fun onAllItemsDeselected() {
        selectAllCheckBox.isChecked = false
    }
    override fun onAllItemsSelected() {
        selectAllCheckBox.isChecked = true
    }

    override fun onAllItemsRemoved() {
        selectAllCheckBox.isChecked = false
        valorTotal.text = "R$ 0,00"
    }

    private fun updateTotalValue() {
        val db = AppDatabase.getDatabase(this@CarrinhoActivity)
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