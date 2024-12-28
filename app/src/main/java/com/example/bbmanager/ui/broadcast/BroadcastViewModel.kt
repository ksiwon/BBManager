package com.example.bbmanager.ui.broadcast

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BroadcastViewModel : ViewModel() {

    private val _inningData = MutableLiveData<String>().apply {
        value = "▲8" // 초기 데이터: 이닝 정보
    }
    val inningData: LiveData<String> = _inningData

    private val _scoreboardData = MutableLiveData<String>().apply {
        value = "롯데 6:LG 7" // 초기 데이터: 점수 정보
    }
    val scoreboardData: LiveData<String> = _scoreboardData

    private val _teamNames = MutableLiveData<String>().apply {
        value = "롯데 vs LG" // 초기 데이터: 팀 이름 정보
    }
    val teamNames: LiveData<String> = _teamNames
}
