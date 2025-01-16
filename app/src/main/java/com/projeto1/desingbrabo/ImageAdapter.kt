package com.projeto1.desingbrabo

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.projeto1.desingbrabo.model.Image

class ImageAdapter(private val images: List<Image>, private val context: Context) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageButton: ImageButton = view.findViewById(R.id.imageButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_imagem, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = images[position]

        // Carregar imagem com Glide
        Glide.with(context)
            .load(image.url)
            .placeholder(R.drawable.placeholder_image)
            .into(holder.imageButton)

        // Configurar clique
        holder.imageButton.setOnClickListener {
            val intent = Intent(context, ProdutoActivity::class.java)
            intent.putExtra("image_id", image.id)
            intent.putExtra("image_url", image.url)
            intent.putExtra("image_alt", image.alt_text)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = images.size
}
