package com.example.bbmanager.ui.gallery

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bbmanager.R

class StadiumsAdapter(
    private val context: Context,
    private val stadiums: List<Stadium>
) : RecyclerView.Adapter<StadiumsAdapter.StadiumViewHolder>() {

    class StadiumViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.stadium_name)
        val horizontalRecyclerView: RecyclerView = view.findViewById(R.id.stadium_images_recycler)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StadiumViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_stadium, parent, false)
        return StadiumViewHolder(view)
    }

    override fun onBindViewHolder(holder: StadiumViewHolder, position: Int) {
        val stadium = stadiums[position]
        holder.titleTextView.text = stadium.name
        holder.horizontalRecyclerView.adapter = ImagesAdapter(context, stadium.images)
        holder.horizontalRecyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
    }

    override fun getItemCount(): Int = stadiums.size
}
