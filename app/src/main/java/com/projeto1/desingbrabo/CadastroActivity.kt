package com.projeto1.desingbrabo

import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.projeto1.desingbrabo.model.User
import com.projeto1.desingbrabo.api.RetrofitInstance
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CadastroActivity: AppCompatActivity() {

    private lateinit var editTextEmail: EditText
    private lateinit var editTextNome: EditText
    private lateinit var editTextTelefone: EditText
    private lateinit var editTextSenha: EditText
    private lateinit var editTextConfirmeSenha: EditText
    private lateinit var buttonCadastrar: Button
    private lateinit var termosCondicoes: TextView

    // CONECTAR NAS API
    // private lateinit var googleButton: ImageView
    // private lateinit var facebookButton: ImageView

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

        buttonCadastrar.setOnClickListener {
            cadastrarUsuario()
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

        val user = User(
            userName = nome,
            email = email,
            password = senha,
            userPhone = telefone,
            isVerify = false
        )

        val apiService = RetrofitInstance.api
        val call = apiService.registerUser(user)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@CadastroActivity, "Cadastro realizado com sucesso!", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@CadastroActivity, "Erro ao cadastrar usuário", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@CadastroActivity, "Falha na conexão com o servidor", Toast.LENGTH_LONG).show()
            }

        })

    }
}
