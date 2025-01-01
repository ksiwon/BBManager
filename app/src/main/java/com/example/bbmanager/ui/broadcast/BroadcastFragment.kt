package com.example.bbmanager.ui.broadcast

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import org.json.JSONObject
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader


import com.github.mikephil.charting.formatter.ValueFormatter

class PlayerNameFormatter(private val playerNames: List<String>) : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        val index = value.toInt()
        return if (index >= 0 && index < playerNames.size) {
            playerNames[index] // 인덱스에 해당하는 선수명 반환
        } else {
            "" // 범위를 벗어난 경우 빈 문자열 반환
        }
    }
}


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
        setupObservers() // LiveData 관찰 설정
        setupMediaPlayer(root) // 미디어 플레이어 초기화
        loadTimingEvents() // JSON 이벤트 로드

        val initialData = readInitialData()

        // root 뷰를 통해 lineChart 찾기
        val lineChart = root.findViewById<LineChart>(R.id.line_chart)
        setupChart(lineChart, initialData)
        Log.d("BroadcastFragment", "LineChart view initialized: $lineChart")

        return root
    }

    private fun readTimingData(): List<Long> {
        val timingData = mutableListOf<Long>()
        try {
            val inputStream = resources.openRawResource(R.raw.timing_event_for_win_per) // JSON 파일 경로
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val jsonString = bufferedReader.use{it.readText()}
            Log.d("BroadcastFragment", "Raw JSON timing data: $jsonString")

            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                if (jsonObject.has("time")) {
                    timingData.add(jsonObject.getLong("time"))
                }
            }
        } catch (e: Exception) {
            Log.e("BroadcastFragment", "Error reading timing data", e)
            e.printStackTrace()
        }
            if (timingData.isEmpty()) {
                Log.e("BroadcastFragment", "업뎃안됨슈바 cannot update chart.")
                return listOf(0L, 12000L, 38000L, 55000L)
            }

        return timingData
    }

    private fun createLineDataSet(entries: ArrayList<Entry>, label: String): LineDataSet {
        val dataSet = LineDataSet(entries, label)
        dataSet.color = ContextCompat.getColor(requireContext(), R.color.Navy) // 라인 색상
        dataSet.valueTextColor = ContextCompat.getColor(requireContext(), R.color.Navy) // 값 텍스트 색상
        dataSet.lineWidth = 4f // 라인 두께
        dataSet.circleRadius = 4f // 원의 반지름
        dataSet.setDrawCircles(true) // 원 표시
        dataSet.setDrawValues(true) // 값 표시
        return dataSet
    }

    private fun setupYAxis(lineChart: LineChart) {
        val leftAxis = lineChart.axisLeft
        leftAxis.axisMinimum = 0f // 최소값
        leftAxis.axisMaximum = 100f // 최대값
        leftAxis.granularity = 10f // 간격
        //leftAxis.setDrawGridLines(true) // 그리드 라인 표시
        leftAxis.setDrawLabels(true) // 레이블 표시


    }

    private fun setupChart(lineChart: LineChart, initialData: MutableList<MutableMap<String, Any>>) {
        // LineDataSet 생성
        val entries = ArrayList<Entry>()
        val playerNames = mutableListOf<String>()

        for ((index, player) in initialData.withIndex()) {
            val playerName = player["선수명"] as String
            val percent = player["percent"] as Float

            entries.add(Entry(index.toFloat(), percent))
            playerNames.add(playerName)
        }

        val dataSet = createLineDataSet(entries, "롯데 승리 확률")

        // LineData 생성 및 차트에 데이터 설정
        val lineData = LineData(dataSet)
        lineChart.data = lineData
        val xAxis = lineChart.xAxis
        xAxis.valueFormatter = PlayerNameFormatter(playerNames)
        xAxis.granularity = 1f
        xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)

        setupYAxis(lineChart) // 세로축 설정 추가

        lineChart.description.isEnabled = false // 차트 설명 비활성화

        //lineChart.axisLeft.setDrawGridLines(false)
        //lineChart.axisRight.isEnabled = false // 오른쪽 Y축 비활성화
        lineChart.invalidate() // 차트 갱신


        //updateChart(lineChart, initialData)
    }




    private fun updateChartWithTiming(lineChart: LineChart, initialData: MutableList<MutableMap<String, Any>>) {
        val timingData = readTimingData()
        val newDataQueue = mutableListOf(
            mutableMapOf("선수명" to "정훈", "percent" to 37.2f),
            mutableMapOf("선수명" to "정훈", "percent" to 18.0f),
            mutableMapOf("선수명" to "박승욱", "percent" to 20.6f) ,
            mutableMapOf("선수명" to "정보근", "percent" to 67.4f)
        )
        if (timingData.isEmpty()) {
            Log.e("BroadcastFragment", "no timing data ㅅㅂ")
            return
        }
        val runnable = object : Runnable {
            private var currentIndex = 0
            override fun run() {
                if (currentIndex >= timingData.size || newDataQueue.isEmpty()) {
                    Log.d("BroadcastFragment", "All updates completed or no more data.")
                    return // 모든 업데이트가 완료되면 종료
                }

                if (initialData.isNotEmpty()) {
                    // 기존 데이터에서 첫 번째 항목 제거
                    initialData.removeAt(0)
                }

                // 새 데이터 삽입
                if (newDataQueue.isNotEmpty()) {
                    @Suppress("UNCHECKED_CAST")
                    val newData = newDataQueue.removeAt(0) as MutableMap<String, Any>
                    initialData.add(newData)
                    Log.d("BroadcastFragment", "Data added: $newData")
                }
                // 차트 데이터 갱신
                val entries = ArrayList<Entry>()
                val playerNames = mutableListOf<String>()
                for ((index, player) in initialData.withIndex()) {
                    val playerName = player["선수명"] as String
                    val percent = player["percent"] as Float
                    entries.add(Entry(index.toFloat(), percent))
                    playerNames.add(playerName)
                }

                val dataSet = createLineDataSet(entries, "롯데 승리 확률")
                val lineData = LineData(dataSet)
                lineChart.data = lineData

                //val xAxis = lineChart.xAxis
                //xAxis.valueFormatter = PlayerNameFormatter(playerNames)
                //xAxis.granularity = 1f
                //xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM

                //lineChart.axisLeft.setDrawGridLines(false)
                //lineChart.description.isEnabled = false
                setupYAxis(lineChart) // 세로축 설정 유지
                lineChart.invalidate() // 차트 새로고침

                if (currentIndex < timingData.size -1) {
                    val nextDelay = timingData[currentIndex + 1] - timingData[currentIndex]
                    currentIndex++
                    handler.postDelayed(this, nextDelay)
                }
            }
        }
        //handler.removeCallbacksAndMessages(null)

        // 5초마다 실행 시작
        handler.post(runnable)
    }


    private fun readInitialData(): MutableList<MutableMap<String, Any>> {
        val initialData = mutableListOf<MutableMap<String, Any>>()

        try {
            // JSON 파일 읽기
            val inputStream = resources.openRawResource(R.raw.winning_percent)
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()
            bufferedReader.useLines { lines -> lines.forEach { stringBuilder.append(it) } }
            val jsonString = stringBuilder.toString()
            Log.d("BroadcastFragment", "Raw JSON data: $jsonString")

            // JSON 파싱
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val playerName = jsonObject.getString("선수명") // 선수명
                val percent = jsonObject.getDouble("percent").toFloat() // 퍼센트 값
                //initialData.add(mutableMapOf("선수명" to playerName, "percent" to percent))

                val data = mutableMapOf<String, Any> (
                    "선수명" to playerName,
                    "percent" to percent
                )
                initialData.add(data)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return initialData
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
                // 차트 갱신 중단
                handler.removeCallbacksAndMessages(null)
            } else {
                val result = audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    mediaPlayer.start()
                    radioPlayPauseButton.setImageResource(R.drawable.ic_pause)
                    trackAudioProgress()
                    // 차트 갱신 시작
                    val lineChart = requireView().findViewById<LineChart>(R.id.line_chart)
                    val initialData = readInitialData()
                    updateChartWithTiming(lineChart, initialData)

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

