package com.projeto1.desingbrabo.api

import com.projeto1.desingbrabo.model.Cadastro
import com.projeto1.desingbrabo.model.ForgotPasswordRequest
import com.projeto1.desingbrabo.model.LoginRequest
import com.projeto1.desingbrabo.model.LoginResponse
import com.projeto1.desingbrabo.model.UpdateProfileRequest
import com.projeto1.desingbrabo.model.GenericResponse
import com.projeto1.desingbrabo.model.PasswordChangeRequest
import com.projeto1.desingbrabo.model.ResetPasswordRequest
import com.projeto1.desingbrabo.model.UserProfileResponse
import com.projeto1.desingbrabo.model.ValidarCodigoRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {
    @POST("/register")
    fun registerUser(@Body cadastro: Cadastro): Call<ResponseBody>

    @POST("/login")
    fun loginUser(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @GET("user/{id}")
    fun getUser(@Path("id") userId: Int): Call<UserProfileResponse>

    @POST("update_profile")
    fun updateProfile(@Body updateRequest: UpdateProfileRequest): Call<GenericResponse>

    @DELETE("/delete_user/{id}")
    fun delete_user(@Path("id") id: Int): Call<Void>

    @POST("change_password")
    fun changePassword(@Body passwordChangeRequest: PasswordChangeRequest): Call<GenericResponse>

    @POST("enviar_email_redefinicao")
    fun enviar_email_redefinicao(@Body forgotPasswordRequest: ForgotPasswordRequest): Call<GenericResponse>

    @POST("validar_codigo")
    fun validar_codigo(@Body validarCodigo: ValidarCodigoRequest): Call<GenericResponse>

    @POST("/redefinir_senha")
    fun redefinir_senha(@Body resetPasswordRequest: ResetPasswordRequest): Call<GenericResponse>
}
