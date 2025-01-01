package com.example.bbmanager.ui.broadcast

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.bbmanager.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.github.mikephil.charting.formatter.ValueFormatter
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Locale

class PlayerNameFormatter(private val playerNames: List<String>) : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        val index = value.toInt()
        return if (index >= 0 && index < playerNames.size) {
            playerNames[index]
        } else {
            ""
        }
    }
}

class BroadcastFragment : Fragment(), TextToSpeech.OnInitListener {

    private val broadcastViewModel: BroadcastViewModel by viewModels()
    private lateinit var textToSpeech: TextToSpeech
    private var isTtsEnabled = true

    private lateinit var mediaPlayer: MediaPlayer
    private var isPlaying: Boolean = false
    private var isMuted: Boolean = false
    private val handler = Handler(Looper.getMainLooper())

    // **차트 관련**
    private val timingEvents = mutableListOf<Pair<Int, JSONObject>>()
    private lateinit var lineChart: LineChart

    // "승리 확률" 데이터 리스트 (최근 10개만 유지)
    // 예: [{"선수명":"박동원","percent":69.3}, {...}, ...]
    private val winningPercentData = mutableListOf<MutableMap<String, Any>>()

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

        textToSpeech = TextToSpeech(requireContext(), this)

        initializeViews(root)
        setupObservers()
        setupMediaPlayer(root)

        // 1) timing_events.json 로드
        loadTimingEvents()

        // 2) 처음 winning_percent.json의 데이터 10개 로드 → winningPercentData에 넣기
        loadInitialWinningPercentData()

        // 3) 차트 세팅
        lineChart = root.findViewById(R.id.line_chart)
        setupChart(lineChart)

        return root
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.language = Locale.KOREA
        } else {
            Log.e("BroadcastFragment", "TextToSpeech initialization failed.")
        }
    }

    /* 기존 TTS 함수 */
    private fun speakUpdate(message: String) {
        if (isTtsEnabled) {
            textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    // **(중요) 기존 updateChartWithTiming, readTimingData 제거**
    // → 이제 승률 업데이트는 timing_events.json 의 "winningPercent" 로 처리

    // 2) 초기 winning_percent.json -> 10개 로드
    private fun loadInitialWinningPercentData() {
        try {
            val inputStream = resources.openRawResource(R.raw.winning_percent)
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()
            bufferedReader.useLines { lines -> lines.forEach { stringBuilder.append(it) } }
            val jsonString = stringBuilder.toString()
            Log.d("BroadcastFragment", "Raw JSON data: $jsonString")

            val jsonArray = JSONArray(jsonString)
            // 최대 10개만 winningPercentData에 추가
            for (i in 0 until jsonArray.length().coerceAtMost(10)) {
                val jsonObject = jsonArray.getJSONObject(i)
                val playerName = jsonObject.getString("선수명")
                val percent = jsonObject.getDouble("percent").toFloat()

                val data = mutableMapOf<String, Any>(
                    "선수명" to playerName,
                    "percent" to percent
                )
                winningPercentData.add(data)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 차트 기본 설정
    private fun setupChart(lineChart: LineChart) {
        // 우선 한 번 그려주거나 기본 세팅
        renderWinningPercentChart() // 초기 데이터 렌더링

        // 우측 축, 범례 끄기
        lineChart.axisRight.isEnabled = false
        lineChart.legend.isEnabled = false

        // Y축(왼쪽) 범위 고정
        val leftAxis = lineChart.axisLeft
        leftAxis.axisMinimum = 0f
        leftAxis.axisMaximum = 100f
        leftAxis.granularity = 20f
        leftAxis.setLabelCount(6, true) // 0, 20, 40, 60, 80, 100

        // 차트 설명 숨김
        lineChart.description.isEnabled = false
    }


    // 현재 winningPercentData 를 차트로 그리는 메서드
    private fun renderWinningPercentChart() {
        val entries = ArrayList<Entry>()
        val playerNames = ArrayList<String>()
        for ((index, data) in winningPercentData.withIndex()) {
            val playerName = data["선수명"] as String
            val percent = data["percent"] as Float
            entries.add(Entry(index.toFloat(), percent))
            playerNames.add(playerName)
        }
        val dataSet = createLineDataSet(entries, "")
        val lineData = LineData(dataSet)
        lineChart.data = lineData

        // X축 라벨
        val xAxis = lineChart.xAxis
        xAxis.valueFormatter = PlayerNameFormatter(playerNames)
        xAxis.granularity = 1f
        xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)

        // 리프레시
        lineChart.invalidate()
    }

    // 새로 받은 "winningPercent" 데이터들을 chart에 반영하는 메서드
    private fun updateWinningPercentChart(newDataList: List<Map<String, Any>>) {
        // 만약 여러개가 들어온다면 순차적으로 처리
        for (newData in newDataList) {
            // 1) 가장 오래된 데이터 제거 (만약 현재 데이터가 이미 10개 이상이라면)
            if (winningPercentData.size >= 10) {
                winningPercentData.removeAt(0) // 제일 앞(오래된) 항목 제거
            }
            // 2) 새 항목 추가
            winningPercentData.add(newData.toMutableMap())
        }
        // 3) 실제 차트 다시 그리기
        renderWinningPercentChart()
    }

    // 기존 createLineDataSet
    private fun createLineDataSet(entries: ArrayList<Entry>, label: String): LineDataSet {
        val dataSet = LineDataSet(entries, label)
        dataSet.color = ContextCompat.getColor(requireContext(), R.color.Navy)
        dataSet.valueTextColor = ContextCompat.getColor(requireContext(), R.color.Navy)
        dataSet.lineWidth = 4f
        dataSet.circleRadius = 4f
        dataSet.setDrawCircles(true)
        dataSet.setDrawValues(true)
        return dataSet
    }

    private fun handleEventUpdates(event: JSONObject) {
        try {
            val updates = event.getJSONObject("updates")
            // 기존 score, bso, baseState, batter 처리
            if (updates.has("score")) {
                val score = updates.getJSONObject("score")
                val team1Score = score.getInt("team1")
                val team2Score = score.getInt("team2")
                broadcastViewModel.updateScoreboard("$team1Score : $team2Score")
            }
            if (updates.has("bso")) {
                val bso = updates.getJSONObject("bso")
                val balls = bso.getInt("balls")
                val strikes = bso.getInt("strikes")
                val outs = bso.getInt("outs")
                broadcastViewModel.updateBsoState(Triple(balls, strikes, outs))
            }
            if (updates.has("baseState")) {
                val baseState = updates.getJSONObject("baseState")
                val base1 = baseState.getString("base1")
                val base2 = baseState.getString("base2")
                val base3 = baseState.getString("base3")
                broadcastViewModel.updateBaseState(Triple(base1, base2, base3))
            }
            if (updates.has("batter")) {
                val batterName = updates.getString("batter")
                broadcastViewModel.updateBatterInfo(requireContext(), batterName)
            }

            // **새로운 승률 업데이트** (timing_events.json 안에 추가했음)
            if (updates.has("winningPercent")) {
                val array = updates.getJSONArray("winningPercent")
                val newDataList = mutableListOf<Map<String, Any>>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val playerName = obj.getString("선수명")
                    val percent = obj.getDouble("percent").toFloat()
                    newDataList.add(
                        mapOf(
                            "선수명" to playerName,
                            "percent" to percent
                        )
                    )
                }
                updateWinningPercentChart(newDataList)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadTimingEvents() {
        try {
            val jsonString =
                requireContext().assets.open("timing_events.json").bufferedReader().use { it.readText() }
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

    // 미디어 플레이어에서 currentTime에 따라 timing 이벤트
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

    // 기존 부분
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

        val fabChat = root.findViewById<FloatingActionButton>(R.id.fab_chat)
        fabChat.show()
        fabChat.setOnClickListener {
            val customDialog = CustomDialogFragment()
            customDialog.show(parentFragmentManager, "CustomDialog")
        }

        // ...
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
            val bsoResId =
                resources.getIdentifier(bsoImageName, "drawable", requireContext().packageName)
            bsoImageView.setImageResource(bsoResId)
        }
        broadcastViewModel.baseState.observe(viewLifecycleOwner) { baseState ->
            val (base1, base2, base3) = baseState
            val baseImageName = "base_${base1}_${base2}_${base3}"
            val baseResId =
                resources.getIdentifier(baseImageName, "drawable", requireContext().packageName)
            baseImageView.setImageResource(baseResId)
        }
        broadcastViewModel.batterInfo.observe(viewLifecycleOwner) { batter ->
            batterNameTextView.text = batter.name
            batterAvgTextView.text = batter.avg.toString()
            batterOpsTextView.text = batter.ops.toString()
            batterWrcTextView.text = batter.wrc.toString()
            batterWarTextView.text = batter.war.toString()
        }
        broadcastViewModel.batterVsPitcherInfo.observe(viewLifecycleOwner) { bvp ->
            matchupBatterNameTextView.text = bvp.batterName
            matchupPitcherNameTextView.text = bvp.pitcherName
            matchupAbTextView.text = bvp.ab.toString()
            matchupHitsTextView.text = bvp.h.toString()
            matchupHrsTextView.text = bvp.hr.toString()
            matchupRbisTextView.text = bvp.rbi.toString()
            matchupBbsTextView.text = bvp.bb.toString()
            matchupSosTextView.text = bvp.so.toString()
            matchupAvgTextView.text = bvp.avg.toString()
            matchupOpsTextView.text = bvp.ops.toString()
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
                // 타이머 중단 (이 경우, updateEvents도 안 돌아감)
                handler.removeCallbacksAndMessages(null)
            } else {
                val result = audioManager.requestAudioFocus(
                    null,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN
                )
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

    override fun onDestroyView() {
        super.onDestroyView()
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
        handler.removeCallbacksAndMessages(null)
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
        val fabChat = requireActivity().findViewById<FloatingActionButton>(R.id.fab_chat)
        fabChat?.hide()
    }
}
