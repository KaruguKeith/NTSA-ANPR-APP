package com.example.ntsaanpr.network


import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("analyze") // This should match the Flask API route
    fun recognizePlate(
        @Part image: MultipartBody.Part  // Part for the image file
    ): Call<ResponseBody>  // Response is a simple JSON text with plate number
}
