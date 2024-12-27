package com.projeto1.desingbrabo

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.projeto1.desingbrabo.api.RetrofitInstance
import com.projeto1.desingbrabo.model.GenericResponse
import com.projeto1.desingbrabo.model.ResetPasswordRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EsqueceuSenhaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela_esqueceu_senha)

        val etNovaSenha: EditText = findViewById(R.id.nova_senha)
        val etConfirmarSenha: EditText = findViewById(R.id.confirmar_nova_senha)
        val btnConfirmar: Button = findViewById(R.id.button_confirmar)

        btnConfirmar.setOnClickListener {
            val novaSenha = etNovaSenha.text.toString().trim()
            val confirmarSenha = etConfirmarSenha.text.toString().trim()
            val token = intent.getStringExtra("token") // Token recebido no link

            if (novaSenha.isEmpty() || confirmarSenha.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (novaSenha != confirmarSenha) {
                Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Faz a requisição para redefinir a senha
            redefinirSenha(token, novaSenha)
        }
    }

    private fun redefinirSenha(token: String?, novaSenha: String) {
        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Token inválido", Toast.LENGTH_SHORT).show()
            return
        }

        val resetPasswordRequest = ResetPasswordRequest(token, novaSenha)

        RetrofitInstance.api.redefinir_senha(resetPasswordRequest).enqueue(object : Callback<GenericResponse> {
            override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@EsqueceuSenhaActivity, "Senha redefinida com sucesso!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(
                        this@EsqueceuSenhaActivity,
                        response.body()?.message ?: "Erro ao redefinir senha",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                Toast.makeText(this@EsqueceuSenhaActivity, "Erro na conexão: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
