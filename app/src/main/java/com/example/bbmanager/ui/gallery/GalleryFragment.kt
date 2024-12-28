package com.example.bbmanager.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bbmanager.R

class GalleryFragment : Fragment() {

    private val galleryViewModel: GalleryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_gallery, container, false)
        val recyclerView: RecyclerView = root.findViewById(R.id.gallery_recycler_view)

        // ViewModel로부터 이미지 데이터 가져오기
        galleryViewModel.images.observe(viewLifecycleOwner, Observer { images ->
            val adapter = GalleryAdapter(requireContext(), images)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        })

        return root
    }
}
