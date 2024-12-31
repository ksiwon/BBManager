package com.example.bbmanager.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bbmanager.R
import org.json.JSONObject

class GalleryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    // 1) 오버레이와 확대 이미지 View 참조 변수
    private lateinit var enlargedImageContainer: FrameLayout
    private lateinit var enlargedImageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_gallery, container, false)

        // RecyclerView
        recyclerView = root.findViewById(R.id.gallery_recycler_view)

        // 2) 오버레이 레이아웃, 확대 이미지를 가져옴
        enlargedImageContainer = root.findViewById(R.id.enlarged_image_container)
        enlargedImageView = root.findViewById(R.id.enlarged_image_view)

        // 오버레이 바깥 부분(FrameLayout)을 누르면 숨기기
        enlargedImageContainer.setOnClickListener {
            it.visibility = View.GONE
        }
        // 만약 "이미지를 눌러도 닫히지 않게" 하려면,
        // 아래처럼 이미지 자체에 이벤트를 소비시키면 됩니다.
        // enlargedImageView.setOnClickListener { /* do nothing */ }

        // Stadiums 데이터 불러오기
        val stadiums = loadStadiumsFromJson()

        // 3) StadiumsAdapter 에 콜백을 넘김 (람다식 이용)
        val adapter = StadiumsAdapter(requireContext(), stadiums, ::onImageClicked)

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

    // 4) 실제로 이미지를 클릭했을 때, 오버레이로 확대 이미지 표시하는 콜백
    private fun onImageClicked(imageName: String) {
        // 이미지 리소스 ID 획득
        val imageResId = resources.getIdentifier(imageName, "drawable", requireContext().packageName)
        // 확대 이미지에 적용
        enlargedImageView.setImageResource(imageResId)
        // 오버레이 보이기
        enlargedImageContainer.visibility = View.VISIBLE
    }
}
