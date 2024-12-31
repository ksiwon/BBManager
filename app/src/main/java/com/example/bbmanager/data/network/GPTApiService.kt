package com.example.bbmanager.data.network

import com.example.bbmanager.data.GPTResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

object ApiConfig {
    const val API_KEY = ""
    //const val API_KEY = "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"
}

interface GPTApiService {
    @Headers(
        "Content-Type: application/json",
        "Authorization: Bearer ${ApiConfig.API_KEY}"
    )
    @POST("chat/completions")
    fun getGPTResponse(@Body request: com.example.bbmanager.data.GPTRequest): Call<GPTResponse>
}
