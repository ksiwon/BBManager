package com.example.bbmanager.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bbmanager.R
import org.json.JSONObject

class GalleryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_gallery, container, false)
        val recyclerView: RecyclerView = root.findViewById(R.id.gallery_recycler_view)

        val stadiums = loadStadiumsFromJson()
        val adapter = StadiumsAdapter(requireContext(), stadiums)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        return root
    }

    private fun loadStadiumsFromJson(): List<Stadium> {
        val inputStream = requireContext().resources.openRawResource(R.raw.stadiums)
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val jsonObject = JSONObject(jsonString)
        val stadiumArray = jsonObject.getJSONArray("stadiums")

        val stadiums = mutableListOf<Stadium>()
        for (i in 0 until stadiumArray.length()) {
            val stadiumObject = stadiumArray.getJSONObject(i)
            val name = stadiumObject.getString("name")
            val images = stadiumObject.getJSONArray("images")
            val imageList = mutableListOf<String>()
            for (j in 0 until images.length()) {
                imageList.add(images.getString(j))
            }
            stadiums.add(Stadium(name, imageList))
        }
        return stadiums
    }
}
