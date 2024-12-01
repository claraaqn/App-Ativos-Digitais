package com.projeto1.desingbrabo.api

import com.projeto1.desingbrabo.model.Cadastro
import com.projeto1.desingbrabo.model.LoginRequest
import com.projeto1.desingbrabo.model.LoginResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/register")
    fun registerUser(@Body cadastro: Cadastro): Call<ResponseBody>

    @POST("/login")
    fun loginUser(@Body loginRequest: LoginRequest): Call<LoginResponse>
}
