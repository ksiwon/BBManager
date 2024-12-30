package com.example.bbmanager.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://api.openai.com/v1/"
    private const val API_KEY = "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"




    // Retrofit 인스턴스
    val apiService: GPTApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Gson 변환기 추가
            .build()
            .create(GPTApiService::class.java) // GPTApiService 생성
    }

}
