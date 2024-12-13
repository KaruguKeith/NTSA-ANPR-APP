package com.example.ntsaanpr

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

object RetrofitClient {
    private const val BASE_URL = "http://192.168.100.20:5000/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

interface ApiService {
    @Multipart
    @POST("analyze")
    fun recognizePlate(@Part image: MultipartBody.Part): Call<ResponseBody>
}
