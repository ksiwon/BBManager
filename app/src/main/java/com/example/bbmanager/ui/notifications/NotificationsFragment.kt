package com.example.bbmanager.ui.notifications

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bbmanager.R

class NotificationsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // fragment_notifications.xml이 res/layout에 있어야 합니다.
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }
}
