package com.projeto1.desingbrabo

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.projeto1.desingbrabo.api.RetrofitInstance
import com.projeto1.desingbrabo.model.Cadastro
import com.projeto1.desingbrabo.model.GenericResponse
import com.projeto1.desingbrabo.model.UpdateProfileRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class EditarPerfilActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView

    private val REQUEST_CODE_GALLERY = 100
    private val REQUEST_CODE_CROP = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela_editar_perfil)

        val voltar: Button = findViewById(R.id.button_voltar)
        val nomeEditText: EditText = findViewById(R.id.edit_nome)
        val emailEditText: EditText = findViewById(R.id.edit_text_email)
        val telefoneEditText: EditText = findViewById(R.id.edit_text_telefone)
        val descricaoEditText: EditText = findViewById(R.id.edit_text_descricao)
        val salvarButton: Button = findViewById(R.id.salvar_button)

        val btnFotoPerfil: Button = findViewById(R.id.editar_foto)
        imageView = findViewById(R.id.foto_perfil)

        voltar.setOnClickListener {
            finish()
        }

        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val idUsuario = sharedPreferences.getInt("user_id", -1)

        nomeEditText.setText(sharedPreferences.getString("user_name", ""))
        emailEditText.setText(sharedPreferences.getString("user_email", ""))
        telefoneEditText.setText(sharedPreferences.getString("user_phone", ""))
        descricaoEditText.setText(sharedPreferences.getString("user_descricao", ""))

        salvarButton.setOnClickListener {
            val novoNome = nomeEditText.text.toString()
            val novoEmail = emailEditText.text.toString()
            val novoTelefone = telefoneEditText.text.toString()
            val novaDescricao = descricaoEditText.text.toString()

            if (novoNome.isNotEmpty() && novoEmail.isNotEmpty() && novoTelefone.isNotEmpty() && novaDescricao.isNotEmpty()) {
                editor.putString("user_name", novoNome)
                editor.putString("user_email", novoEmail)
                editor.putString("user_telefone", novoTelefone)
                editor.putString("user_descricao", novaDescricao)
                editor.apply()

                salvarBanco(idUsuario, novoNome, novoEmail, novoTelefone, novaDescricao)

                setResult(RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Por favor, preencha todos os campos!", Toast.LENGTH_SHORT).show()
            }
        }

        btnFotoPerfil.setOnClickListener {
            // fazer a lógica da foto de perfil
        }
    }

    private fun salvarBanco (userId: Int, nome: String, email: String, phone: String, descricao: String) {

        val novo = UpdateProfileRequest(
            userId = userId,
           nome = nome,
            email = email,
            phone = phone,
            descricao = descricao
        )

        RetrofitInstance.api.updateProfile(novo).enqueue(object : Callback<GenericResponse> {
            override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                if (response.isSuccessful) {
                    val genericResponse = response.body()
                    if (genericResponse != null && genericResponse.success) {
                        println("Perfil atualizado: ${genericResponse.message}")
                    } else {
                        Toast.makeText(this@EditarPerfilActivity, "Erro ao editar perfil", Toast.LENGTH_LONG).show()

                    }
                } else {
                    Toast.makeText(this@EditarPerfilActivity, "Erro na requisição", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                Toast.makeText(this@EditarPerfilActivity, "Falha na conexão com o servidor", Toast.LENGTH_LONG).show()

            }
        })
    }
}