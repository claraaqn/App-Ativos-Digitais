package com.projeto1.desingbrabo

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.projeto1.desingbrabo.model.Cadastro
import com.projeto1.desingbrabo.api.RetrofitInstance
import com.projeto1.desingbrabo.model.ValidacaoEmailRequest
import com.projeto1.desingbrabo.model.GenericResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CadastroActivity : AppCompatActivity() {

    private lateinit var editTextEmail: EditText
    private lateinit var editTextNome: EditText
    private lateinit var editTextTelefone: EditText
    private lateinit var editTextSenha: EditText
    private lateinit var editTextConfirmeSenha: EditText
    private lateinit var buttonCadastrar: Button
    private lateinit var termosCondicoes: CheckBox
    private lateinit var termosCondicoesBurron: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela_cadastro)

        editTextEmail = findViewById(R.id.edit_text_email)
        editTextNome = findViewById(R.id.edit_nome)
        editTextTelefone = findViewById(R.id.edit_text_telefone)
        editTextSenha = findViewById(R.id.edit_text_senha)
        editTextConfirmeSenha = findViewById(R.id.edit_text_confirme_senha)
        buttonCadastrar = findViewById(R.id.button_login)
        termosCondicoes = findViewById(R.id.termos_condicoes)
        termosCondicoesBurron = findViewById(R.id.termos_condicoes_button)

        termosCondicoesBurron.setOnClickListener {
            val dialogView = LayoutInflater.from(this).inflate(R.layout.popup_termos_condicoes, null)

            val dialogBuilder = AlertDialog.Builder(this)
                .setView(dialogView)

            val alertDialog = dialogBuilder.create()

            val fecharButton = dialogView.findViewById<Button>(R.id.rechar)
            fecharButton.setOnClickListener {
                alertDialog.dismiss()
            }

            alertDialog.show()
        }

        buttonCadastrar.setOnClickListener {
            if (termosCondicoes.isChecked) {
                cadastrarUsuario()
            } else {
                Toast.makeText(this@CadastroActivity,"Você precisa concordar com nossos Termos e Condições para se cadastrar", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun cadastrarUsuario() {
        val email = editTextEmail.text.toString()
        val nome = editTextNome.text.toString()
        val telefone = editTextTelefone.text.toString()
        val senha = editTextSenha.text.toString()
        val confirmeSenha = editTextConfirmeSenha.text.toString()

        if (senha != confirmeSenha) {
            editTextConfirmeSenha.error = "As senhas não coincidem!"
            return
        }

        if (TextUtils.isEmpty(email)) {
            editTextEmail.error = "Insira seu e-mail!"
            return
        }

        if (TextUtils.isEmpty(nome)) {
            editTextNome.error = "Insira seu nome!"
            return
        }

        if (TextUtils.isEmpty(telefone)) {
            editTextTelefone.error = "Insira seu telefone!"
            return
        }

        if (TextUtils.isEmpty(senha)) {
            editTextSenha.error = "Insira sua senha!"
            return
        }

        val cadastro = Cadastro(
            userName = nome,
            email = email,
            password = senha,
            userPhone = telefone,
            isVerify = false
        )

        val apiService = RetrofitInstance.api
        val call = apiService.registerUser(cadastro)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@CadastroActivity, "Cadastro realizado com sucesso!", Toast.LENGTH_LONG).show()

                    enviarCodigoVerificacao(email)
                } else {
                    Toast.makeText(this@CadastroActivity, "Erro ao cadastrar usuário", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@CadastroActivity, "Falha na conexão com o servidor", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun enviarCodigoVerificacao(email: String) {
        val apiService = RetrofitInstance.api
        val validarEmail = ValidacaoEmailRequest(email)

        apiService.validar_email(validarEmail).enqueue(object : Callback<GenericResponse> {
            override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@CadastroActivity, "Código de verificação enviado!", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@CadastroActivity, VerificacaoEmailActivity::class.java)
                    intent.putExtra("email", email)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@CadastroActivity, "Erro ao enviar código de verificação", Toast.LENGTH_LONG).show()
                }
            }
            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                Toast.makeText(this@CadastroActivity, "Erro de conexão ao enviar código", Toast.LENGTH_LONG).show()
            }
        })
    }
}
