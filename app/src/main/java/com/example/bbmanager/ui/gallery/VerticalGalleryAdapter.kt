package com.example.bbmanager.ui.gallery

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bbmanager.R

class VerticalGalleryAdapter(
    private val context: Context,
    private val groupedImages: List<List<String>>
) : RecyclerView.Adapter<VerticalGalleryAdapter.VerticalGalleryViewHolder>() {

    class VerticalGalleryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val horizontalRecyclerView: RecyclerView = view.findViewById(R.id.horizontal_recycler_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerticalGalleryViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_vertical_gallery, parent, false)
        return VerticalGalleryViewHolder(view)
    }

    override fun onBindViewHolder(holder: VerticalGalleryViewHolder, position: Int) {
        val images = groupedImages[position]
        val adapter = HorizontalGalleryAdapter(context, images)
        holder.horizontalRecyclerView.adapter = adapter
        holder.horizontalRecyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
    }

    override fun getItemCount(): Int = groupedImages.size
}
