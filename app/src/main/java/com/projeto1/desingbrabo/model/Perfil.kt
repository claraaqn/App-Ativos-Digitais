package com.projeto1.desingbrabo.model

import android.app.Application
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
            RetrofitInstance.api.getUser(userId).enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            _userLiveData.postValue(it)
                        }
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    // DEUS SABE
                }
            })
        }
    }
}
