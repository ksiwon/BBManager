package com.example.bbmanager.ui.gallery

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.bbmanager.R

class HorizontalGalleryAdapter(
    private val context: Context,
    private val images: List<String>
) : RecyclerView.Adapter<HorizontalGalleryAdapter.HorizontalGalleryViewHolder>() {

    class HorizontalGalleryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.gallery_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorizontalGalleryViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_gallery_image, parent, false)
        return HorizontalGalleryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HorizontalGalleryViewHolder, position: Int) {
        val imageResId = context.resources.getIdentifier(
            images[position],
            "drawable",
            context.packageName
        )
        holder.imageView.setImageResource(imageResId)
    }

    override fun getItemCount(): Int = images.size
}
