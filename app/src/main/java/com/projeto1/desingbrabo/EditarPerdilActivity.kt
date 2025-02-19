package com.projeto1.desingbrabo

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditarPerfilActivity : AppCompatActivity() {

    private lateinit var  voltar: Button
    private lateinit var nomeEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var telefoneEditText: EditText
    private lateinit var salvarButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela_editar_perfil)

        voltar = findViewById(R.id.button_voltar)
        nomeEditText = findViewById(R.id.edit_nome)
        emailEditText = findViewById(R.id.edit_text_email)
        telefoneEditText = findViewById(R.id.edit_text_telefone)
        val descricao: EditText = findViewById(R.id.edit_text_descricao)
        salvarButton = findViewById(R.id.salvar_button)

        voltar.setOnClickListener{
            finish()
        }

        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        nomeEditText.setText(sharedPreferences.getString("user_name", ""))
        emailEditText.setText(sharedPreferences.getString("user_email", ""))
        telefoneEditText.setText(sharedPreferences.getString("user_phone", ""))

        salvarButton.setOnClickListener {
            val novoNome = nomeEditText.text.toString()
            val novoEmail = emailEditText.text.toString()
            val novoTelefone = telefoneEditText.text.toString()
            val novaDescricao = descricao.text.toString()

            if (novoNome.isNotEmpty() && novoEmail.isNotEmpty() && novoTelefone.isNotEmpty() && novaDescricao.isNotEmpty()) {
                editor.putString("user_name", novoNome)
                editor.putString("user_email", novoEmail)
                editor.putString("user_telefone", novoTelefone)
                editor.putString("user_descricao", novaDescricao)
                editor.apply()

                Toast.makeText(this, "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show()

                setResult(RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Por favor, preencha todos os campos!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
