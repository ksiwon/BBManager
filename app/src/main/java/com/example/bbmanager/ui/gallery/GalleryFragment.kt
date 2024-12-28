package com.example.bbmanager.ui.gallery

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bbmanager.R

class GalleryFragment : Fragment() {

    companion object {
        // newInstance 메서드 추가
        fun newInstance(): GalleryFragment {
            return GalleryFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }
}
