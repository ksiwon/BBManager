package com.example.bbmanager.ui.broadcast

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.bbmanager.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class BroadcastFragment : Fragment() {

    private val broadcastViewModel: BroadcastViewModel by viewModels()
    private lateinit var mediaPlayer: MediaPlayer
    private var isPlaying: Boolean = false
    private var isMuted: Boolean = false

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

        // 투수 Card
        val pitcherNameTextView: TextView = root.findViewById(R.id.pitcher_name)
        val pitcherEraTextView: TextView = root.findViewById(R.id.pitcher_era)
        val pitcherKTextView: TextView = root.findViewById(R.id.pitcher_k)
        val pitcherWhipTextView: TextView = root.findViewById(R.id.pitcher_whip)
        val pitcherWarTextView: TextView = root.findViewById(R.id.pitcher_war)

        // 타자 Card
        val batterNameTextView: TextView = root.findViewById(R.id.batter_name)
        val batterAvgTextView: TextView = root.findViewById(R.id.batter_avg)
        val batterOpsTextView: TextView = root.findViewById(R.id.batter_ops)
        val batterWrcTextView: TextView = root.findViewById(R.id.batter_wrc)
        val batterWarTextView: TextView = root.findViewById(R.id.batter_war)

        // VS Card 데이터 연결
        val matchupBatterNameTextView: TextView = root.findViewById(R.id.matchup_batter_name)
        val matchupPitcherNameTextView: TextView = root.findViewById(R.id.matchup_pitcher_name)
        val matchupAbTextView: TextView = root.findViewById(R.id.matchup_ab)
        val matchupHitsTextView: TextView = root.findViewById(R.id.matchup_hits)
        val matchupHrsTextView: TextView = root.findViewById(R.id.matchup_hrs)
        val matchupRbisTextView: TextView = root.findViewById(R.id.matchup_rbis)
        val matchupBbsTextView: TextView = root.findViewById(R.id.matchup_bbs)
        val matchupSosTextView: TextView = root.findViewById(R.id.matchup_sos)
        val matchupAvgTextView: TextView = root.findViewById(R.id.matchup_avg)
        val matchupOpsTextView: TextView = root.findViewById(R.id.matchup_ops)
        val radioStationName: TextView = root.findViewById(R.id.radio_station_name)
        val radioPlayPauseButton: ImageView = root.findViewById(R.id.radio_play_pause_button)
        val radioVolumeIcon: ImageView = root.findViewById(R.id.radio_volume_icon)

        // Floating Action Button
        val fabChat: FloatingActionButton = root.findViewById(R.id.fab_chat)

        fabChat.show()
        fabChat.setOnClickListener {
            val customDialog = CustomDialogFragment()
            customDialog.show(parentFragmentManager, "CustomDialog")
        }

        // 데이터 관찰 및 업데이트
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

        broadcastViewModel.pitcherInfo.observe(viewLifecycleOwner) { pitcher ->
            pitcherNameTextView.text = pitcher.name
            pitcherEraTextView.text = pitcher.era.toString()
            pitcherKTextView.text = pitcher.k.toString()
            pitcherWhipTextView.text = pitcher.whip.toString()
            pitcherWarTextView.text = pitcher.war.toString()
        }

        broadcastViewModel.batterInfo.observe(viewLifecycleOwner) { batter ->
            batterNameTextView.text = batter.name
            batterAvgTextView.text = batter.avg.toString()
            batterOpsTextView.text = batter.ops.toString()
            batterWrcTextView.text = batter.wrc.toString()
            batterWarTextView.text = batter.war.toString()
        }

        broadcastViewModel.batterVsPitcherInfo.observe(viewLifecycleOwner) { info ->
            matchupBatterNameTextView.text = info.batterName
            matchupPitcherNameTextView.text = info.pitcherName
            matchupAbTextView.text = info.ab.toString()
            matchupHitsTextView.text = info.h.toString()
            matchupHrsTextView.text = info.hr.toString()
            matchupRbisTextView.text = info.rbi.toString()
            matchupBbsTextView.text = info.bb.toString()
            matchupSosTextView.text = info.so.toString()
            matchupAvgTextView.text = String.format("%.3f", info.avg)
            matchupOpsTextView.text = String.format("%.3f", info.ops)
        }

        // 초기 데이터 로드
        broadcastViewModel.loadBatterVsPitcherData(requireContext(), "정훈") // 타자 이름 설정

        // MediaPlayer 초기화
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.radio)
        mediaPlayer.isLooping = true // 라디오 음원을 반복 재생

        // 플레이/일시정지 버튼 클릭 이벤트
        radioPlayPauseButton.setOnClickListener {
            if (isPlaying) {
                mediaPlayer.pause()
                radioPlayPauseButton.setImageResource(R.drawable.ic_play)
            } else {
                mediaPlayer.start()
                radioPlayPauseButton.setImageResource(R.drawable.ic_pause)
            }
            isPlaying = !isPlaying
        }

        // 볼륨 버튼 클릭 이벤트
        radioVolumeIcon.setOnClickListener {
            if (isMuted) {
                // 볼륨 복구
                mediaPlayer.setVolume(1.0f, 1.0f) // 왼쪽 및 오른쪽 볼륨 설정
                radioVolumeIcon.setImageResource(R.drawable.ic_volume)
            } else {
                // 볼륨 음소거
                mediaPlayer.setVolume(0.0f, 0.0f)
                radioVolumeIcon.setImageResource(R.drawable.ic_mute)
            }
            isMuted = !isMuted
        }

        // MediaPlayer 해제
        mediaPlayer.setOnCompletionListener {
            radioPlayPauseButton.setImageResource(R.drawable.ic_play)
            isPlaying = false
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (this::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
        val fabChat = requireActivity().findViewById<FloatingActionButton>(R.id.fab_chat)
        fabChat?.hide()
    }
}
