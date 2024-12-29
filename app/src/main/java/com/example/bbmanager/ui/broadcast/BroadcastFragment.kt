package com.example.bbmanager.ui.broadcast

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bbmanager.R
//import com.example.bbmanager.ui.R
import android.util.Log

class BroadcastFragment : Fragment() {

    companion object {
        fun newInstance() = BroadcastFragment()
    }

    private val viewModel: BroadcastViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("BroadcastFragment", "BroadcastFragment created")

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_broadcast, container, false)
    }
}