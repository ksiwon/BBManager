package com.example.bbmanager.ui.dashboard

import android.content.Context
import android.content.res.XmlResourceParser
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bbmanager.R

class DashboardViewModel(private val context: Context) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text

    private val _imagePath = MutableLiveData<Int>()

    // 이미지 경로를 LiveData로 노출
    val imagePath: LiveData<Int> = _imagePath

    init {
        loadImagePathFromXml()
    }

    // XML에서 이미지 경로 로드
    private fun loadImagePathFromXml() {
        val parser: XmlResourceParser = context.resources.getXml(R.xml.image_paths)
        try {
            while (parser.eventType != XmlResourceParser.END_DOCUMENT) {
                if (parser.eventType == XmlResourceParser.START_TAG && parser.name == "path") {
                    val path = parser.nextText()
                    val resId = context.resources.getIdentifier(
                        path.replace("@res/drawable/zoom_to_rect_large.png", "zoom"), // "@drawable/example_image" -> "example_image"
                        "drawable",
                        context.packageName
                    )
                    _imagePath.value = resId
                }
                parser.next()
            }
        } finally {
            parser.close()
        }
    }
}
