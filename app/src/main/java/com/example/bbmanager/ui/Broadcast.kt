package com.example.bbmanager.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bbmanager.R
import com.example.bbmanager.ui.broadcast.BroadcastFragment

class Broadcast : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_broadcast)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, BroadcastFragment.newInstance())
                .commitNow()
        }
    }
}