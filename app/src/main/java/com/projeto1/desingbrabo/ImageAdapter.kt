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

class ImageAdapter(private var images: List<Image>, private val context: Context) :
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

        Glide.with(context)
            .load(image.url)
            .placeholder(R.drawable.placeholder_image)
            .into(holder.imageButton)

        holder.imageButton.setOnClickListener {
            val intent = Intent(context, ProdutoActivity::class.java)
            intent.putExtra("image_id", image.id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = images.size

    fun updateImages(newImages: List<Image>) {
        images = newImages
        notifyDataSetChanged()
    }

    fun clearImages() {
        images = emptyList()
        notifyDataSetChanged()
    }
}
