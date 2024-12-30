package com.example.bbmanager.data.network

import com.example.bbmanager.data.GPTResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

//object ApiConfig {
//    const val API_KEY = "api_key"
//}

interface GPTApiService {
    @Headers(
        "Content-Type: application/json",
        "Authorization: Bearer ${ApiConfig.API_KEY}"
    )
    @POST("chat/completions")
    fun getGPTResponse(@Body request: com.example.bbmanager.data.GPTRequest): Call<GPTResponse>
}
