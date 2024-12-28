package com.example.bbmanager.ui.broadcast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.bbmanager.R

class BroadcastFragment : Fragment() {

    private val broadcastViewModel: BroadcastViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_broadcast, container, false)

        val teamLotteTextView: TextView = root.findViewById(R.id.team_lotte)
        val scoreLotteTextView: TextView = root.findViewById(R.id.score_lotte)
        val teamLgTextView: TextView = root.findViewById(R.id.team_lg)
        val scoreLgTextView: TextView = root.findViewById(R.id.score_lg)

        broadcastViewModel.scoreboardData.observe(viewLifecycleOwner) { scoreboard ->
            val scores = scoreboard.split(" : ")
            if (scores.size == 2) {
                scoreLotteTextView.text = scores[0].split(" ")[1]
                scoreLgTextView.text = scores[1]
            }
        }

        broadcastViewModel.teamNames.observe(viewLifecycleOwner) { teamNames ->
            val teams = teamNames.split(" vs ")
            if (teams.size == 2) {
                teamLotteTextView.text = teams[0]
                teamLgTextView.text = teams[1]
            }
        }

        return root
    }
}
