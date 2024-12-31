package com.projeto1.desingbrabo

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.projeto1.desingbrabo.api.RetrofitInstance
import com.projeto1.desingbrabo.model.ForgotPasswordRequest
import com.projeto1.desingbrabo.model.GenericResponse
import com.projeto1.desingbrabo.model.ValidarCodigoRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date
import android.os.CountDownTimer
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Locale
import android.text.Editable
import android.text.TextWatcher


class VerificacaoEmailActivity : AppCompatActivity() {
    private val LAST_REQUEST_TIME = "lastRequestTime"
    private lateinit var email: String
    private lateinit var countDownTimer: CountDownTimer
    private val twoMinutes = 120000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela_verificacao_esqueceu_senha)

        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val email = sharedPreferences.getString("user_email_redefinicao", null)

        if (email == null) {
            Toast.makeText(this, "E-mail não encontrado. Tente novamente.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val confirmar = findViewById<Button>(R.id.button_confirmar)
        val reenviar = findViewById<Button>(R.id.button_reenviar)
        val textContagem = findViewById<TextView>(R.id.contagem)

        val editTexts = listOf(
            findViewById<EditText>(R.id.etDigit1),
            findViewById<EditText>(R.id.etDigit2),
            findViewById<EditText>(R.id.etDigit3),
            findViewById<EditText>(R.id.etDigit4),
            findViewById<EditText>(R.id.etDigit5),
            findViewById<EditText>(R.id.etDigit6)
        )

        setupAutoFocus(editTexts)
        iniciarContador(reenviar, textContagem, sharedPreferences)

        confirmar.setOnClickListener {
            val code = editTexts.joinToString("") { it.text.toString() }
            if (code.length == 6) {
                validarCodigo(email, code)
            } else {
                Toast.makeText(this, "Digite um código válido de 6 dígitos.", Toast.LENGTH_SHORT).show()
            }
        }

        reenviar.setOnClickListener {
            val lastRequest = sharedPreferences.getLong(LAST_REQUEST_TIME, 0)
            val currentTime = Date().time
            if (currentTime - lastRequest >= twoMinutes) { // 2 minutos em milissegundos
                sendResendRequest(email)
                sharedPreferences.edit().putLong(LAST_REQUEST_TIME, currentTime).apply()
                iniciarContador(reenviar, textContagem, sharedPreferences)
            } else {
                Toast.makeText(this, "Espere um pouco antes de pedir outro código.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun iniciarContador(reenviar: Button, textContagem: TextView, sharedPreferences: SharedPreferences) {
        reenviar.isEnabled = false // Desativa o botão
        countDownTimer = object : CountDownTimer(twoMinutes, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val formatter = SimpleDateFormat("mm:ss", Locale.getDefault())
                val date = Date(millisUntilFinished)
                val formattedTime = formatter.format(date)
                textContagem.text = "$formattedTime minutos"
            }

            override fun onFinish() {
                textContagem.text = "00:00 minutos"
                reenviar.isEnabled = true // Ativa o botão novamente
            }
        }.start()
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
                            Toast.makeText(this@VerificacaoEmailActivity, "Código validado!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@VerificacaoEmailActivity, LoginActivity::class.java)
                            intent.putExtra("email", email)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@VerificacaoEmailActivity, it.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Log.e("ValidarCodigo", "Erro: ${response.code()}")
                    Toast.makeText(this@VerificacaoEmailActivity, "Erro ao validar código.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                Log.e("ValidarCodigo", "Falha: ${t.message}")
                Toast.makeText(this@VerificacaoEmailActivity, "Erro de conexão.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun sendResendRequest(email: String) {
        enviarEmailRedefinicaoSenha(email)
    }

    private fun enviarEmailRedefinicaoSenha(email: String) {
        val apiService = RetrofitInstance.api
        val forgotPasswordRequest = ForgotPasswordRequest(email)

        apiService.enviar_email_redefinicao(forgotPasswordRequest).enqueue(object : Callback<GenericResponse> {
            override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@VerificacaoEmailActivity, "Email reenviado", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@VerificacaoEmailActivity, "Tente novamente mais tarde.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                Toast.makeText(this@VerificacaoEmailActivity, "Erro ao enviar e-mail: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupAutoFocus(editTexts: List<EditText>) {
        for (i in editTexts.indices) {
            val currentEditText = editTexts[i]
            val nextEditText = if (i < editTexts.size - 1) editTexts[i + 1] else null
            val previousEditText = if (i > 0) editTexts[i - 1] else null

            currentEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (!s.isNullOrEmpty() && nextEditText != null && count > 0) {
                        nextEditText.requestFocus()
                    }
                    if (s.isNullOrEmpty() && previousEditText != null && count == 0 && start == 0) {
                        previousEditText.requestFocus()
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

}
