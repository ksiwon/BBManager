package com.example.bbmanager.ui.broadcast

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.json.JSONObject

class BroadcastViewModel : ViewModel() {

    data class Pitcher(
        val name: String,
        val era: Double,
        val k: Int,
        val whip: Double,
        val war: Double
    )

    data class Batter(
        val name: String,
        val avg: Double,
        val ops: Double,
        val wrc: Double,
        val war: Double
    )

    data class BatterVsPitcher(
        val batterName: String,
        val pitcherName: String,
        val ab: Int,
        val h: Int,
        val hr: Int,
        val rbi: Int,
        val bb: Int,
        val so: Int,
        val avg: Double,
        val ops: Double
    )

    private val _inningData = MutableLiveData<String>().apply { value = "8초" }
    val inningData: LiveData<String> = _inningData

    private val _scoreboardData = MutableLiveData<String>().apply { value = "6 : 7" }
    val scoreboardData: LiveData<String> = _scoreboardData

    private val _teamNames = MutableLiveData<String>().apply { value = "롯데 vs LG" }
    val teamNames: LiveData<String> = _teamNames

    private val _bsoState = MutableLiveData<Triple<Int, Int, Int>>().apply { value = Triple(1, 2, 1) }
    val bsoState: LiveData<Triple<Int, Int, Int>> = _bsoState

    private val _baseState = MutableLiveData<Triple<String, String, String>>().apply { value = Triple("x", "x", "o") }
    val baseState: LiveData<Triple<String, String, String>> = _baseState

    private val _pitcherInfo = MutableLiveData<Pitcher>().apply {
        value = Pitcher("김진성", 3.97, 61, 1.21, 1.23)
    }
    val pitcherInfo: LiveData<Pitcher> = _pitcherInfo

    private val _batterInfo = MutableLiveData<Batter>().apply {
        value = Batter("정훈", 0.267, 0.775, 94.9, 0.76)
    }
    val batterInfo: LiveData<Batter> = _batterInfo

    private val _batterVsPitcherInfo = MutableLiveData<BatterVsPitcher>().apply {
        value = BatterVsPitcher("정훈", "김진성", 22, 3, 0, 0, 4, 6, 0.136, 0.496)
    }
    val batterVsPitcherInfo: LiveData<BatterVsPitcher> = _batterVsPitcherInfo

    fun loadBatterVsPitcherData(context: Context, batterName: String) {
        try {
            val jsonString = context.assets.open("player_info.json").bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(jsonString)
            val batterData = jsonObject.getJSONObject(batterName)

            val batterVsPitcher = BatterVsPitcher(
                batterName = batterName,
                pitcherName = "김진성",
                ab = batterData.getInt("AB"),
                h = batterData.getInt("H"),
                hr = batterData.getInt("HR"),
                rbi = batterData.getInt("RBI"),
                bb = batterData.getInt("BB"),
                so = batterData.getInt("SO"),
                avg = batterData.getDouble("AVG"),
                ops = batterData.getDouble("OPS")
            )

            _batterVsPitcherInfo.postValue(batterVsPitcher)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
