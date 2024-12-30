package com.example.bbmanager.data.network

import com.example.bbmanager.data.GPTResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

object ApiConfig {
    const val API_KEY = "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"
}

interface GPTApiService {
    @Headers(
        "Content-Type: application/json",
        "Authorization: Bearer ${ApiConfig.API_KEY}"
    )
    @POST("chat/completions")
    fun getGPTResponse(@Body request: com.example.bbmanager.data.GPTRequest): Call<GPTResponse>
}
