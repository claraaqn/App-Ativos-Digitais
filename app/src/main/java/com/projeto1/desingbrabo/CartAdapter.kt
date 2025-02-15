package com.projeto1.desingbrabo

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.projeto1.desingbrabo.data.Product

class CartAdapter(private var products: List<Pair<Product, Int>>,  private val context: Context) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productName: TextView = itemView.findViewById(R.id.nome_produto)
        private val productPrice: TextView = itemView.findViewById(R.id.valor)
        private val formatos: TextView = itemView.findViewById(R.id.tipo_itpo)
        private val dono: TextView = itemView.findViewById(R.id.enviado_nome)
        val ImageButton: ImageButton = itemView.findViewById(R.id.miniatura_produto)

        fun bind(product: Product) {
            productName.text = product.name
            productPrice.text = "${product.price}"
            formatos.text = product.formatos.joinToString(", ")
            dono.text = product.owner
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.produto_carrinho, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val (product) = products[position]

        holder.bind(product)

        Glide.with(context)
            .load(product.imageUrl)
            .placeholder(R.drawable.placeholder_image)
            .into(holder.ImageButton)

        holder.ImageButton.setOnClickListener {
            val intent = Intent(context, ProdutoActivity::class.java)
            intent.putExtra("image_id", product.id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = products.size

    fun updateData(newProducts: List<Pair<Product, Int>>) {
        products = newProducts
        notifyDataSetChanged()
    }
}