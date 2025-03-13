package com.projeto1.desingbrabo.api

import com.projeto1.desingbrabo.model.Cadastro
import com.projeto1.desingbrabo.model.Colaborador
import com.projeto1.desingbrabo.model.FavoriteRequest
import com.projeto1.desingbrabo.model.ForgotPasswordRequest
import com.projeto1.desingbrabo.model.LoginRequest
import com.projeto1.desingbrabo.model.LoginResponse
import com.projeto1.desingbrabo.model.UpdateProfileRequest
import com.projeto1.desingbrabo.model.GenericResponse
import com.projeto1.desingbrabo.model.PasswordChangeRequest
import com.projeto1.desingbrabo.model.ResetPasswordRequest
import com.projeto1.desingbrabo.model.UserProfileResponse
import com.projeto1.desingbrabo.model.ValidacaoEmailRequest
import com.projeto1.desingbrabo.model.ValidarCodigoEmailRequest
import com.projeto1.desingbrabo.model.ValidarCodigoRequest
import com.projeto1.desingbrabo.model.Image
import com.projeto1.desingbrabo.model.LikeResponse
import com.projeto1.desingbrabo.model.Produto
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

    @POST("validar_codigo_redefinir_senha")
    fun validar_codigo(@Body validarCodigo: ValidarCodigoRequest): Call<GenericResponse>

    @POST("redefinir_senha")
    fun redefinir_senha(@Body resetPasswordRequest: ResetPasswordRequest): Call<GenericResponse>

    @POST("enviar_email_validação")
    fun validar_email(@Body validarEmailRequest: ValidacaoEmailRequest): Call<GenericResponse>

    @POST("validar_codigo_validacao_email")
    fun validar_codigo(@Body validarCodigoEmailRequest: ValidarCodigoEmailRequest): Call<GenericResponse>

    @GET("images")
    fun getImages(): Call<List<Image>>

    @GET("produto/{id}")
    fun getProduto(@Path("id") id: Int): Call<Produto>

    @GET("search/images")
    fun searchImages(
        @Query("tag") tag: String?,
        @Query("isPremium") isPremium: Boolean,
        @Query("isGratis") isGratis: Boolean,
        @Query("formats") formats: List<String>,
        @Query("categoria") categoria: String,
        @Query("color") color: List<String>,
        @Query("userId") userId: Int?
    ): Call<List<Image>>

    @POST("/add_favorite")
    fun addFavorite(@Body request: FavoriteRequest): Call<LikeResponse>

    @POST("/remove_favorite")
    fun removeFavorite(@Body request: FavoriteRequest): Call<LikeResponse>

    @GET("/get_like_status")
    fun getLikeStatus(
        @Query("image_id") imageId: Int,
        @Query("user_id") userId: Int
    ): Call<LikeResponse>

    @GET("/check_if_liked")
    fun checkIfLiked(@Query("image_id") imageId: Int, @Query("user_id") userId: Int): Call<LikeResponse>

    @GET("colaborador/{id}")
    fun getColaborador(@Path("id") userId: Int): Call<Colaborador>

    @GET("image/{id}")
    fun getImagemColaborador(@Path("id") userId: Int): Call<List<Image>>

}
