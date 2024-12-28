package com.example.bbmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bbmanager.ui.gallery.GalleryFragment

class Gallery : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, GalleryFragment()) // newInstance 대신 직접 생성
                .commitNow()
        }
    }
}
