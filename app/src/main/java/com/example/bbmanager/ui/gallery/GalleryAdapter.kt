package com.example.bbmanager.ui.gallery

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.bbmanager.R

class GalleryAdapter(
    private val context: Context,
    private val images: List<String>
) : RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {

    // ViewHolder 정의
    class GalleryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.gallery_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_gallery_image, parent, false)
        return GalleryViewHolder(view)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        // 이미지 이름으로 리소스 ID 가져오기
        val imageResId = context.resources.getIdentifier(
            images[position].replace(".png", ""),
            "drawable",
            context.packageName
        )
        holder.imageView.setImageResource(imageResId)
    }

    override fun getItemCount(): Int {
        return images.size
    }
}
