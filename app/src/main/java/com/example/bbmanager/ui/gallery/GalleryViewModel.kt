package com.example.bbmanager.ui.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GalleryViewModel : ViewModel() {

    // 이미지 리스트 (샘플 데이터)
    private val _images = MutableLiveData<List<String>>().apply {
        value = listOf(
            "busan_1",
            "busan_2",
            "busan_3",
            "busan_4",
            "daegu_1",
            "daegu_2",
            "daegu_3",
            "daegu_4",
            "changwon_1",
            "changwon_2",
            "changwon_3",
            "changwon_4",
            "daejeon_1",
            "daejeon_2",
            "daejeon_3",
            "daejeon_4",
            "gocheok_1",
            "gocheok_2",
            "gocheok_3",
            "gocheok_4",
            "gwangju_1",
            "gwangju_2",
            "gwangju_3",
            "gwangju_4",
            "jamsil_1",
            "jamsil_2",
            "jamsil_3",
            "jamsil_4",
            "incheon_1",
            "incheon_2",
            "incheon_3",
            "incheon_4",
            "suwon_1",
            "suwon_2",
            "suwon_3",
            "suwon_4"
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
