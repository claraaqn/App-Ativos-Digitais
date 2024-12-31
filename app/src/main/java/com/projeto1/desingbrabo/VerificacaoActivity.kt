package com.projeto1.desingbrabo

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.projeto1.desingbrabo.api.RetrofitInstance
import com.projeto1.desingbrabo.model.GenericResponse
import com.projeto1.desingbrabo.model.ValidarCodigoRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VerificacaoActivity : AppCompatActivity() {

    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela_verificacao)

        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val email = sharedPreferences.getString("user_email_redefinicao", null)

        if (email == null) {
            Toast.makeText(this, "E-mail não encontrado. Tente novamente.", Toast.LENGTH_SHORT).show()
            finish() // Finaliza a Activity caso o e-mail não seja encontrado
            return
        }

        val confirmar = findViewById<Button>(R.id.button_confirmar)

        val editTexts = listOf(
            findViewById<EditText>(R.id.etDigit6),
            findViewById<EditText>(R.id.etDigit5),
            findViewById<EditText>(R.id.etDigit4),
            findViewById<EditText>(R.id.etDigit3),
            findViewById<EditText>(R.id.etDigit2),
            findViewById<EditText>(R.id.etDigit1)
        )

        confirmar.setOnClickListener {
            val code = editTexts.joinToString("") { it.text.toString() }
            if (code.length == 6) {
                validarCodigo(email, code)
            } else {
                Toast.makeText(this, "Digite um código válido de 6 dígitos.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validarCodigo(email: String, code: String) {
        val apiService = RetrofitInstance.api

        val validarCodigoRequest = ValidarCodigoRequest(email, code)
        val call = apiService.validar_codigo(validarCodigoRequest)

        call.enqueue(object : Callback<GenericResponse> {
            override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (it.success) {
                            // Código validado com sucesso
                            Toast.makeText(this@VerificacaoActivity, "Código validado!", Toast.LENGTH_SHORT).show()
                            // Navegar para a tela de redefinição de senha
                            val intent = Intent(this@VerificacaoActivity, EsqueceuSenhaActivity::class.java)
                            intent.putExtra("email", email)
                            startActivity(intent)
                            finish()
                        } else {
                            // Código inválido ou expirado
                            Toast.makeText(this@VerificacaoActivity, it.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    // Erro na requisição
                    Log.e("ValidarCodigo", "Erro: ${response.code()}")
                    Toast.makeText(this@VerificacaoActivity, "Erro ao validar código.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                // Falha na conexão
                Log.e("ValidarCodigo", "Falha: ${t.message}")
                Toast.makeText(this@VerificacaoActivity, "Erro de conexão.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
