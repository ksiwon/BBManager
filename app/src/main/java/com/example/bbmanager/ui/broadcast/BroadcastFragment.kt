package com.example.bbmanager.ui.broadcast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
        val inningTextView: TextView = root.findViewById(R.id.inning)
        val bsoImageView: ImageView = root.findViewById(R.id.bso_image)
        val baseImageView: ImageView = root.findViewById(R.id.base_image)

        broadcastViewModel.scoreboardData.observe(viewLifecycleOwner) { scoreboard ->
            val scores = scoreboard.split(" : ")
            if (scores.size == 2) {
                scoreLotteTextView.text = scores[0]
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

        broadcastViewModel.inningData.observe(viewLifecycleOwner) { innings ->
            inningTextView.text = innings
        }

        broadcastViewModel.bsoState.observe(viewLifecycleOwner) { bso ->
            val (b, s, o) = bso
            val bsoImageName = "bso_${b}_${s}_${o}"
            val bsoResId = resources.getIdentifier(bsoImageName, "drawable", requireContext().packageName)
            bsoImageView.setImageResource(bsoResId)
        }

        broadcastViewModel.baseState.observe(viewLifecycleOwner) { bases ->
            val (base1, base2, base3) = bases
            val baseImageName = "base_${base1}_${base2}_${base3}"
            val baseResId = resources.getIdentifier(baseImageName, "drawable", requireContext().packageName)
            baseImageView.setImageResource(baseResId)
        }

        return root
    }
}

