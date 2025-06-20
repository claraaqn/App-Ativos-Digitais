package com.projeto1.desingbrabo

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.projeto1.desingbrabo.data.AppDatabase
import com.projeto1.desingbrabo.data.Product
import kotlinx.coroutines.launch

class CartAdapter(private var products: List<Pair<Product, Int>>,  private val context: Context) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    interface OnItemRemovedListener {
        fun onItemRemoved(productId: Int, wasSelected: Boolean)
        fun onItemSelectionChanged(totalValue: Double)
        fun onAllItemsDeselected()
        fun onAllItemsSelected()
        fun onAllItemsRemoved()
    }

    private var onItemRemovedListener: OnItemRemovedListener? = null

    fun setOnItemRemovedListener(listener: OnItemRemovedListener) {
        this.onItemRemovedListener = listener
    }

    private val selectedItems = mutableMapOf<Int, Boolean>()

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productName: TextView = itemView.findViewById(R.id.nome_produto)
        private val productPrice: TextView = itemView.findViewById(R.id.valor)
        private val formatos: TextView = itemView.findViewById(R.id.tipo_itpo)
        private val dono: TextView = itemView.findViewById(R.id.enviado_nome)
        val ImageButton: ImageButton = itemView.findViewById(R.id.miniatura_produto)
        val buttonLixo: Button = itemView.findViewById(R.id.button_lixo)
        val checkBox: CheckBox = itemView.findViewById(R.id.selecionar)

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

        holder.checkBox.isChecked = selectedItems[product.id] ?: false

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            selectedItems[product.id] = isChecked
            updateTotalValue()
            checkIfAllItemsDeselected()
            checkIfAllItemsSelected()
        }

        holder.ImageButton.setOnClickListener {
            val intent = Intent(context, ProdutoActivity::class.java)
            intent.putExtra("image_id", product.id)
            context.startActivity(intent)

            val wasSelected = selectedItems[product.id] ?: false
            onItemRemovedListener?.onItemRemoved(product.id, wasSelected)
        }

        holder.buttonLixo.setOnClickListener {
            removeItemFromDatabase(product.id)

            val updatedProducts = products.toMutableList()
            updatedProducts.removeAt(position)
            selectedItems.remove(product.id)
            updateData(updatedProducts)

            onItemRemovedListener?.onItemRemoved(product.id, false)

            updateTotalValue()
        }
    }

    override fun getItemCount(): Int = products.size

    fun updateData(newProducts: List<Pair<Product, Int>>) {
        // Preserve selection states for remaining items
        val newSelectedItems = mutableMapOf<Int, Boolean>()
        for ((product, _) in newProducts) {
            newSelectedItems[product.id] = selectedItems[product.id] ?: false
        }

        products = newProducts
        selectedItems.clear()
        selectedItems.putAll(newSelectedItems)

        notifyDataSetChanged()
    }

    private fun removeItemFromDatabase(productId: Int) {
        val db = AppDatabase.getDatabase(context)
        kotlinx.coroutines.GlobalScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                db.cartDao().removeCartItem(productId)
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }

    fun selectAllItems(isSelected: Boolean) {
        for ((product, _) in products) {
            selectedItems[product.id] = isSelected
        }
        notifyDataSetChanged()
        updateTotalValue()
    }

    private fun updateTotalValue() {
        var totalValue = 0.0
        for ((product, _) in products) {
            if (selectedItems[product.id] == true) {
                val price = product.price?.replace("R$ ", "")?.replace(",", ".")?.toDoubleOrNull() ?: 0.0
                totalValue += price
            }
        }
        onItemRemovedListener?.onItemSelectionChanged(totalValue)
    }

    private fun checkIfAllItemsDeselected() {
        val allDeselected = selectedItems.all { !it.value }
        if (allDeselected) {
            onItemRemovedListener?.onAllItemsDeselected()
        }
    }

    private fun checkIfAllItemsSelected() {
        val allSelected = selectedItems.all { it.value }
        if (allSelected) {
            onItemRemovedListener?.onAllItemsSelected()
        }
    }

    fun checkIfAllItemsRemoved() {
        if (products.isEmpty()) {
            onItemRemovedListener?.onAllItemsRemoved()
        }
    }

    fun safeRemoveItem(productId: Int) {
        val position = products.indexOfFirst { it.first.id == productId }
        if (position != -1) {
            val wasSelected = selectedItems[productId] ?: false

            removeItemFromDatabase(productId)

            val updatedProducts = products.toMutableList()
            updatedProducts.removeAt(position)
            updateData(updatedProducts)

            onItemRemovedListener?.onItemRemoved(productId, wasSelected)
            updateTotalValue()
            checkIfAllItemsRemoved()
        }
    }

}