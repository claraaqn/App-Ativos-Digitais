package com.projeto1.desingbrabo.api

import com.projeto1.desingbrabo.model.User
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/register")
    fun registerUser(@Body user: User): Call<ResponseBody>
}
