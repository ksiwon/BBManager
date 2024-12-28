package com.example.bbmanager.ui.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GalleryViewModel : ViewModel() {

    // 이미지 리스트 (샘플 데이터)
    private val _images = MutableLiveData<List<String>>().apply {
        value = listOf(
            "busan1.jpg",
            "busan2.jpg",
            "busan3.jpg"
        )
    }
    val images: LiveData<List<String>> = _images

    // 선택된 이미지 관리
    private val _selectedImage = MutableLiveData<String>()
    val selectedImage: LiveData<String> = _selectedImage

    // 이미지 선택 로직
    fun selectImage(image: String) {
        _selectedImage.value = image
    }
}
