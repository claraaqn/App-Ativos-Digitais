package com.projeto1.desingbrabo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.projeto1.desingbrabo.data.Product

class CartAdapter(private val products: List<Pair<Product, Int>>) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.produto_carrinho, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val (product, quantity) = products[position]
        holder.bind(product, quantity)
    }

    override fun getItemCount(): Int = products.size

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productName: TextView = itemView.findViewById(R.id.nome_produto)
        private val productPrice: TextView = itemView.findViewById(R.id.valor)

        fun bind(product: Product, quantity: Int) {
            productName.text = product.name
            productPrice.text = "R$ ${product.price}"
        }
    }
}