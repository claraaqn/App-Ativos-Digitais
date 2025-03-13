package com.projeto1.desingbrabo

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ColorsAdapter(
    private val colors: List<String>,
    private val context: Context
) : RecyclerView.Adapter<ColorsAdapter.ColorViewHolder>() {

    class ColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val colorImageView: ImageView = itemView.findViewById(R.id.cor_produto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.cor, parent, false)
        return ColorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val colorHex = colors[position]

        val shape = GradientDrawable()
        shape.shape = GradientDrawable.OVAL
        try {
            shape.setColor(Color.parseColor(colorHex))
        } catch (e: IllegalArgumentException) {
            // Caso o valor não seja um hex válido, use uma cor padrão (azul, por exemplo)
            shape.setColor(Color.parseColor("#0000FF"))
        }

        holder.colorImageView.background = shape
    }

    override fun getItemCount(): Int = colors.size
}
