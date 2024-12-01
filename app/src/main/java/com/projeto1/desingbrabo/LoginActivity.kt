package com.projeto1.desingbrabo

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.projeto1.desingbrabo.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.projeto1.desingbrabo.model.LoginRequest
import com.projeto1.desingbrabo.model.LoginResponse


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela_login)

        val emailField = findViewById<EditText>(R.id.edit_text_email)
        val passwordField = findViewById<EditText>(R.id.edit_text_senha)
        val loginButton = findViewById<Button>(R.id.button_login)

        loginButton.setOnClickListener {
            val email = emailField.text.toString()
            val password = passwordField.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                login(email, password)
            } else {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun login(email: String, password: String) {
        val loginRequest = LoginRequest(email, password)

        RetrofitInstance.api.loginUser(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val userId = response.body()?.userId
                    if (userId != null) {
                        saveUserId(userId)
                        Toast.makeText(this@LoginActivity,"Login realizado com sucesso!",
                            Toast.LENGTH_SHORT).show()

                        val userName = response.body()?.nome
                        val userEmail = response.body()?.email

                        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("user_name", userName)
                        editor.putString("user_email", userEmail)
                        editor.apply()

                        val intent = Intent(this@LoginActivity, PerfilActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@LoginActivity, "Erro: ID do usuário não encontrado!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "E-mail ou senha inválidos!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Erro na conexão: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun saveUserId(userId: Int) {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("user_id", userId)
        editor.apply()
    }
}