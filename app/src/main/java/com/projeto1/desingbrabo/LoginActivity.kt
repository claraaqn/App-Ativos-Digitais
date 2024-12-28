package com.projeto1.desingbrabo

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.projeto1.desingbrabo.api.ApiService
import com.projeto1.desingbrabo.api.RetrofitInstance
import com.projeto1.desingbrabo.model.LoginRequest
import com.projeto1.desingbrabo.model.LoginResponse
import com.projeto1.desingbrabo.model.GenericResponse
import com.projeto1.desingbrabo.model.ForgotPasswordRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela_login)

        val emailField = findViewById<EditText>(R.id.edit_text_email)
        val passwordField = findViewById<EditText>(R.id.edit_text_senha)
        val loginButton = findViewById<Button>(R.id.button_login)
        val buttonEsqueceuSenha = findViewById<Button>(R.id.button_esquecer_senha)

        loginButton.setOnClickListener {
            val email = emailField.text.toString()
            val password = passwordField.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                login(email, password)
            } else {
                Toast.makeText(this@LoginActivity, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            }
        }

        buttonEsqueceuSenha.setOnClickListener {
            exibirPopupEsqueceuSenha()
        }
    }

    private fun login(email: String, password: String) {
        val loginRequest = LoginRequest(email, password)

        RetrofitInstance.api.loginUser(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    salvarDadosUsuario(response.body()!!)
                    irParaTelaPerfil()
                } else {
                    Toast.makeText(this@LoginActivity, "E-mail ou senha inválidos!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Erro na conexão: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun salvarDadosUsuario(response: LoginResponse) {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("user_id", response.userId ?: -1)
        editor.putString("user_name", response.nome)
        editor.putString("user_email", response.email)
        editor.putString("user_phone", response.phone)
        editor.apply()
    }

    private fun irParaTelaPerfil() {
        Toast.makeText(this@LoginActivity, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show()
        val intent = Intent(this@LoginActivity, PerfilActivity::class.java)
        startActivity(intent)
    }

    private fun exibirPopupEsqueceuSenha() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.popup_esqueceu_senha, null)
        val etEmail: EditText = dialogView.findViewById(R.id.email)
        val btnEnviarEmail: Button = dialogView.findViewById(R.id.button_enviar)
        val btnFechar: Button = dialogView.findViewById(R.id.fechar)

        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        btnFechar.setOnClickListener {
            alertDialog.dismiss() // Fecha apenas o popup
        }

        btnEnviarEmail.setOnClickListener {
            val email = etEmail.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this@LoginActivity, "Por favor, insira um e-mail válido", Toast.LENGTH_SHORT).show()
            } else {
                enviarEmailRedefinicaoSenha(email)
                Toast.makeText(this@LoginActivity, "E-mail enviado com sucesso!", Toast.LENGTH_SHORT).show()
                alertDialog.dismiss()
            }
        }
        alertDialog.show()
    }

    private fun enviarEmailRedefinicaoSenha(email: String) {
        val apiService = RetrofitInstance.api
        val forgotPasswordRequest = ForgotPasswordRequest(email)

        apiService.enviar_email_redefinicao(forgotPasswordRequest).enqueue(object : Callback<GenericResponse> {
                override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(this@LoginActivity, "E-mail enviado com sucesso!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@LoginActivity, "Tente novamente mais tarde.", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "Erro ao enviar e-mail: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
