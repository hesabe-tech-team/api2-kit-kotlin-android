package com.hesabe.hesabe_payment_kotlin.api

import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiInterface {
    @POST("/checkout")
    fun hesabePay(
        @Header("accessCode") accessToken:String,
        @Query("data") data: String
    ): Call<String?>?
}