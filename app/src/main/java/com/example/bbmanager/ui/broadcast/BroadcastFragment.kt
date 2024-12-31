package com.example.bbmanager.ui.broadcast

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bbmanager.R
import org.json.JSONObject

class BroadcastFragment : Fragment() {

    private val broadcastViewModel: BroadcastViewModel by viewModels()

    private lateinit var mediaPlayer: MediaPlayer
    private var isPlaying: Boolean = false
    private var isMuted: Boolean = false
    private val handler = Handler(Looper.getMainLooper())
    private val timingEvents = mutableListOf<Pair<Int, JSONObject>>()

    // Views
    private lateinit var teamLotteTextView: TextView
    private lateinit var scoreLotteTextView: TextView
    private lateinit var teamLgTextView: TextView
    private lateinit var scoreLgTextView: TextView
    private lateinit var inningTextView: TextView
    private lateinit var bsoImageView: ImageView
    private lateinit var baseImageView: ImageView
    private lateinit var batterNameTextView: TextView
    private lateinit var batterAvgTextView: TextView
    private lateinit var batterOpsTextView: TextView
    private lateinit var batterWrcTextView: TextView
    private lateinit var batterWarTextView: TextView
    private lateinit var pitcherNameTextView: TextView
    private lateinit var pitcherEraTextView: TextView
    private lateinit var pitcherKTextView: TextView
    private lateinit var pitcherWhipTextView: TextView
    private lateinit var pitcherWarTextView: TextView
    private lateinit var matchupBatterNameTextView: TextView
    private lateinit var matchupPitcherNameTextView: TextView
    private lateinit var matchupAbTextView: TextView
    private lateinit var matchupHitsTextView: TextView
    private lateinit var matchupHrsTextView: TextView
    private lateinit var matchupRbisTextView: TextView
    private lateinit var matchupBbsTextView: TextView
    private lateinit var matchupSosTextView: TextView
    private lateinit var matchupAvgTextView: TextView
    private lateinit var matchupOpsTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_broadcast, container, false)

        initializeViews(root)
        initializeDefaultValues() // 초기값 강제로 반영
        setupObservers() // LiveData 관찰 설정
        setupMediaPlayer(root) // 미디어 플레이어 초기화
        loadTimingEvents() // JSON 이벤트 로드

        return root
    }

    private fun initializeViews(root: View) {
        teamLotteTextView = root.findViewById(R.id.team_lotte)
        scoreLotteTextView = root.findViewById(R.id.score_lotte)
        teamLgTextView = root.findViewById(R.id.team_lg)
        scoreLgTextView = root.findViewById(R.id.score_lg)
        inningTextView = root.findViewById(R.id.inning)
        bsoImageView = root.findViewById(R.id.bso_image)
        baseImageView = root.findViewById(R.id.base_image)
        batterNameTextView = root.findViewById(R.id.batter_name)
        batterAvgTextView = root.findViewById(R.id.batter_avg)
        batterOpsTextView = root.findViewById(R.id.batter_ops)
        batterWrcTextView = root.findViewById(R.id.batter_wrc)
        batterWarTextView = root.findViewById(R.id.batter_war)
        pitcherNameTextView = root.findViewById(R.id.pitcher_name)
        pitcherEraTextView = root.findViewById(R.id.pitcher_era)
        pitcherKTextView = root.findViewById(R.id.pitcher_k)
        pitcherWhipTextView = root.findViewById(R.id.pitcher_whip)
        pitcherWarTextView = root.findViewById(R.id.pitcher_war)
        matchupBatterNameTextView = root.findViewById(R.id.matchup_batter_name)
        matchupPitcherNameTextView = root.findViewById(R.id.matchup_pitcher_name)
        matchupAbTextView = root.findViewById(R.id.matchup_ab)
        matchupHitsTextView = root.findViewById(R.id.matchup_hits)
        matchupHrsTextView = root.findViewById(R.id.matchup_hrs)
        matchupRbisTextView = root.findViewById(R.id.matchup_rbis)
        matchupBbsTextView = root.findViewById(R.id.matchup_bbs)
        matchupSosTextView = root.findViewById(R.id.matchup_sos)
        matchupAvgTextView = root.findViewById(R.id.matchup_avg)
        matchupOpsTextView = root.findViewById(R.id.matchup_ops)

        // FAB 초기화
        val fabChat = root.findViewById<FloatingActionButton>(R.id.fab_chat)



        // BroadcastFragment에서 FAB 보이기
        fabChat.show()

        // FAB 클릭 이벤트; 팝업 두둥
        fabChat.setOnClickListener {
            val customDialog = CustomDialogFragment()
            customDialog.show(parentFragmentManager, "CustomDialog")
        }

        broadcastViewModel.scoreboardData.observe(viewLifecycleOwner) { scoreboard ->
            val scores = scoreboard.split(" : ")
            if (scores.size == 2) {
                scoreLotteTextView.text = scores[0]
                scoreLgTextView.text = scores[1]
            }
        }

        // 타자 카드 초기화
        broadcastViewModel.batterInfo.value?.let { batter ->
            batterNameTextView.text = batter.name
            batterAvgTextView.text = String.format("%.3f", batter.avg)
            batterOpsTextView.text = String.format("%.3f", batter.ops)
            batterWrcTextView.text = String.format("%.1f", batter.wrc)
            batterWarTextView.text = String.format("%.2f", batter.war)
        }

        // 투수 카드 초기화
        broadcastViewModel.pitcherInfo.value?.let { pitcher ->
            pitcherNameTextView.text = pitcher.name
            pitcherEraTextView.text = String.format("%.2f", pitcher.era)
            pitcherKTextView.text = pitcher.k.toString()
            pitcherWhipTextView.text = String.format("%.2f", pitcher.whip)
            pitcherWarTextView.text = String.format("%.2f", pitcher.war)
        }

        // VS 카드 초기화
        broadcastViewModel.batterVsPitcherInfo.value?.let { vsInfo ->
            matchupBatterNameTextView.text = vsInfo.batterName
            matchupPitcherNameTextView.text = vsInfo.pitcherName
            matchupAbTextView.text = vsInfo.ab.toString()
            matchupHitsTextView.text = vsInfo.h.toString()
            matchupHrsTextView.text = vsInfo.hr.toString()
            matchupRbisTextView.text = vsInfo.rbi.toString()
            matchupBbsTextView.text = vsInfo.bb.toString()
            matchupSosTextView.text = vsInfo.so.toString()
            matchupAvgTextView.text = String.format("%.3f", vsInfo.avg)
            matchupOpsTextView.text = String.format("%.3f", vsInfo.ops)
        }
    }

    private fun setupObservers() {
        broadcastViewModel.scoreboardData.observe(viewLifecycleOwner) { scoreboard ->
            val scores = scoreboard.split(" : ")
            if (scores.size == 2) {
                scoreLotteTextView.text = scores[0]
                scoreLgTextView.text = scores[1]
            }
        }

        broadcastViewModel.inningData.observe(viewLifecycleOwner) { inning ->
            inningTextView.text = inning
        }

        broadcastViewModel.bsoState.observe(viewLifecycleOwner) { bso ->
            val (balls, strikes, outs) = bso
            val bsoImageName = "bso_${balls}_${strikes}_${outs}"
            val bsoResId = resources.getIdentifier(bsoImageName, "drawable", requireContext().packageName)
            bsoImageView.setImageResource(bsoResId)
        }

        broadcastViewModel.baseState.observe(viewLifecycleOwner) { baseState ->
            val (base1, base2, base3) = baseState
            val baseImageName = "base_${base1}_${base2}_${base3}"
            val baseResId = resources.getIdentifier(baseImageName, "drawable", requireContext().packageName)
            baseImageView.setImageResource(baseResId)
        }

        broadcastViewModel.batterInfo.observe(viewLifecycleOwner) { batter ->
            batterNameTextView.text = batter.name
            batterAvgTextView.text = batter.avg.toString()
            batterOpsTextView.text = batter.ops.toString()
            batterWrcTextView.text = batter.wrc.toString()
            batterWarTextView.text = batter.war.toString()
        }

        broadcastViewModel.batterVsPitcherInfo.observe(viewLifecycleOwner) { batterVsPitcher ->
            matchupBatterNameTextView.text = batterVsPitcher.batterName
            matchupPitcherNameTextView.text = batterVsPitcher.pitcherName
            matchupAbTextView.text = batterVsPitcher.ab.toString()
            matchupHitsTextView.text = batterVsPitcher.h.toString()
            matchupHrsTextView.text = batterVsPitcher.hr.toString()
            matchupRbisTextView.text = batterVsPitcher.rbi.toString()
            matchupBbsTextView.text = batterVsPitcher.bb.toString()
            matchupSosTextView.text = batterVsPitcher.so.toString()
            matchupAvgTextView.text = batterVsPitcher.avg.toString()
            matchupOpsTextView.text = batterVsPitcher.ops.toString()
        }
    }

    private fun loadTimingEvents() {
        try {
            val jsonString = requireContext().assets.open("timing_events.json").bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(jsonString)
            val events = jsonObject.getJSONArray("timingEvents")

            for (i in 0 until events.length()) {
                val event = events.getJSONObject(i)
                val time = event.getInt("time")
                timingEvents.add(Pair(time, event))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkAndExecuteEvents(currentTime: Int) {
        val iterator = timingEvents.iterator()
        while (iterator.hasNext()) {
            val (time, event) = iterator.next()
            if (currentTime >= time) {
                handleEventUpdates(event)
                iterator.remove()
            }
        }
    }

    private fun handleEventUpdates(event: JSONObject) {
        try {
            val updates = event.getJSONObject("updates")

            // 점수 업데이트
            if (updates.has("score")) {
                val score = updates.getJSONObject("score")
                val team1Score = score.getInt("team1")
                val team2Score = score.getInt("team2")
                broadcastViewModel.updateScoreboard("$team1Score : $team2Score")
            }

            // BSO 상태 업데이트
            if (updates.has("bso")) {
                val bso = updates.getJSONObject("bso")
                val balls = bso.getInt("balls")
                val strikes = bso.getInt("strikes")
                val outs = bso.getInt("outs")
                broadcastViewModel.updateBsoState(Triple(balls, strikes, outs))
            }

            // Base 상태 업데이트
            if (updates.has("baseState")) {
                val baseState = updates.getJSONObject("baseState")
                val base1 = baseState.getString("base1")
                val base2 = baseState.getString("base2")
                val base3 = baseState.getString("base3")
                broadcastViewModel.updateBaseState(Triple(base1, base2, base3))
            }

            // 타자 정보 업데이트
            if (updates.has("batter")) {
                val batterName = updates.getString("batter")
                broadcastViewModel.updateBatterInfo(requireContext(), batterName)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun setupMediaPlayer(root: View) {
        val radioPlayPauseButton: ImageView = root.findViewById(R.id.radio_play_pause_button)
        val radioVolumeIcon: ImageView = root.findViewById(R.id.radio_volume_icon)

        val audioManager = requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.radio)
        mediaPlayer.isLooping = true

        radioPlayPauseButton.setOnClickListener {
            if (isPlaying) {
                mediaPlayer.pause()
                radioPlayPauseButton.setImageResource(R.drawable.ic_play)
            } else {
                val result = audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    mediaPlayer.start()
                    radioPlayPauseButton.setImageResource(R.drawable.ic_pause)
                    trackAudioProgress()
                }
            }
            isPlaying = !isPlaying
        }

        radioVolumeIcon.setOnClickListener {
            if (isMuted) {
                mediaPlayer.setVolume(1.0f, 1.0f)
                radioVolumeIcon.setImageResource(R.drawable.ic_volume)
            } else {
                mediaPlayer.setVolume(0.0f, 0.0f)
                radioVolumeIcon.setImageResource(R.drawable.ic_mute)
            }
            isMuted = !isMuted
        }
    }

    private fun trackAudioProgress() {
        handler.post(object : Runnable {
            override fun run() {
                if (mediaPlayer.isPlaying) {
                    val currentTime = mediaPlayer.currentPosition
                    checkAndExecuteEvents(currentTime)
                    handler.postDelayed(this, 100)
                }
            }
        })
        
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
        handler.removeCallbacksAndMessages(null)

        // 프래그먼트를 떠날 때 FAB 숨기기
        val fabChat = requireActivity().findViewById<FloatingActionButton>(R.id.fab_chat)
        fabChat?.hide()
    }
}

