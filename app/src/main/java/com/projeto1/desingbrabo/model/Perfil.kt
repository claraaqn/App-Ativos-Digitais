package com.projeto1.desingbrabo.model

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.projeto1.desingbrabo.api.RetrofitInstance
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Perfil(application: Application) : AndroidViewModel(application) {

    private val _userLiveData = MutableLiveData<User>()
    val userLiveData: LiveData<User> get() = _userLiveData

    fun getUser(userId: Int) {
        viewModelScope.launch {
            RetrofitInstance.api.getUser(userId).enqueue(object : Callback<UserProfileResponse> {
                override fun onResponse(call: Call<UserProfileResponse>, response: Response<UserProfileResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            Toast.makeText(getApplication(), "Erro: Resposta vazia do servidor.", Toast.LENGTH_SHORT).show()
                            it.user?.let { user ->
                                _userLiveData.postValue(user)
                            } ?: run {
                                Toast.makeText(getApplication(), "Erro: Dados do usuário não encontrados.", Toast.LENGTH_SHORT).show()
                            }
                        } ?: run {
                            Toast.makeText(getApplication(), "Erro: Resposta vazia do servidor.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else {
                        Toast.makeText(getApplication(), "Erro ao carregar perfil. Código: ${response.code()}", Toast.LENGTH_SHORT).show()

                    }
                }

                override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                    Toast.makeText(getApplication(), "Erro de conexão. Tente novamente.", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
