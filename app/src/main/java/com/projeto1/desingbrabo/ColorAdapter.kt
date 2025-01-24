package com.projeto1.desingbrabo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ColorsAdapter(
    private val colors: List<String>,
    private val context: Context
) : RecyclerView.Adapter<ColorsAdapter.ColorViewHolder>() {

    private val colorDrawables = mapOf(
        "preto" to R.drawable.btn_preto,
        "cinza" to R.drawable.btn_cinza,
        "rosa" to R.drawable.btn_rosa,
        "branco" to R.drawable.btn_branco,
        "amarelo" to R.drawable.btn_amarelo,
        "laranja" to R.drawable.btn_laranja,
        "azul" to R.drawable.btn_azul,
        "marrom" to R.drawable.btn_marrom,
        "roxo" to R.drawable.btn_roxo,
        "verde" to R.drawable.btn_verde,
        "vermelho" to R.drawable.btn_vermelho
    )

    class ColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val colorImageView: ImageView = itemView.findViewById(R.id.cor_produto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.cor, parent, false)
        return ColorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val color = colors[position]

        val drawableId = colorDrawables[color]
        if (drawableId != null) {
            holder.colorImageView.setImageResource(drawableId)
        } else {
            holder.colorImageView.setImageResource(R.drawable.btn_azul)
        }
    }

    override fun getItemCount(): Int = colors.size
}
