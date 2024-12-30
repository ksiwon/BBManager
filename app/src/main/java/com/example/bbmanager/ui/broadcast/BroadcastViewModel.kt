package com.example.bbmanager.ui.broadcast

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BroadcastViewModel : ViewModel() {

    private val _inningData = MutableLiveData<String>().apply {
        value = "8초"
    }
    val inningData: LiveData<String> = _inningData

    private val _scoreboardData = MutableLiveData<String>().apply {
        value = "6 : 7"
    }
    val scoreboardData: LiveData<String> = _scoreboardData

    private val _teamNames = MutableLiveData<String>().apply {
        value = "롯데 vs LG"
    }
    val teamNames: LiveData<String> = _teamNames

    private val _bsoState = MutableLiveData<Triple<Int, Int, Int>>().apply {
        value = Triple(1, 2, 1) // B, S, O 초기값
    }
    val bsoState: LiveData<Triple<Int, Int, Int>> = _bsoState

    private val _baseState = MutableLiveData<Triple<String, String, String>>().apply {
        value = Triple("x", "x", "o") // 1B, 2B, 3B 초기값
    }
    val baseState: LiveData<Triple<String, String, String>> = _baseState

    // 업데이트 메서드
    fun updateInning(newInning: String) {
        _inningData.value = newInning
    }

    fun updateScoreboard(newScore: String) {
        _scoreboardData.value = newScore
    }

    fun updateTeamNames(newTeamNames: String) {
        _teamNames.value = newTeamNames
    }

    fun updateBsoState(b: Int, s: Int, o: Int) {
        _bsoState.value = Triple(b, s, o)
    }

    fun updateBaseState(base1: String, base2: String, base3: String) {
        _baseState.value = Triple(base1, base2, base3)
    }
}
