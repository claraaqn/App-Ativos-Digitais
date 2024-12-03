package com.projeto1.desingbrabo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.projeto1.desingbrabo.model.Perfil
import com.projeto1.desingbrabo.model.UserProfileResponse
import com.projeto1.desingbrabo.api.RetrofitInstance
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PerfilActivity : AppCompatActivity() {

    private lateinit var voltarButton: Button
    private lateinit var nomeUsuarioTextView: TextView
    private lateinit var emailUsuarioTextView: TextView
    private lateinit var fotoPerfilImageView: ImageView
    private lateinit var minhaAssinaturaButton: Button
    private lateinit var editarPerfilButton: Button
    private lateinit var configuracoesButton: Button
    private lateinit var sairButton: Button

    private lateinit var buttonHome: Button
    private lateinit var buttonUpload: Button
    private lateinit var buttonColecao: Button
    private lateinit var buttonCarrinho: Button
    private lateinit var buttonPerfil: Button

    private lateinit var userViewModel: Perfil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela_perfil)

        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

        voltarButton = findViewById(R.id.button_voltar)
        nomeUsuarioTextView = findViewById(R.id.nome_usuario)
        emailUsuarioTextView = findViewById(R.id.email_cliente)
        fotoPerfilImageView = findViewById(R.id.foto_perfil)

        minhaAssinaturaButton = findViewById(R.id.button_minha_assinatura)
        editarPerfilButton = findViewById(R.id.button_editar_perfil)
        configuracoesButton = findViewById(R.id.button_configuracoes)
        sairButton = findViewById(R.id.button_sair)

        buttonHome = findViewById(R.id.button_home)
        buttonUpload = findViewById(R.id.button_upload)
        buttonColecao = findViewById(R.id.button_colecao)
        buttonCarrinho = findViewById(R.id.button_carrinho)
        buttonPerfil = findViewById(R.id.button_perfil)

        carregarDadosPerfil(sharedPreferences)

        editarPerfilButton.setOnClickListener {
            val intent = Intent(this, EditarPerfilActivity::class.java)
            startActivityForResult(intent, 1)
        }

        sairButton.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        voltarButton.setOnClickListener {
            finish()
        }
    }

    // Carrega os dados do perfil a partir de SharedPreferences
    private fun carregarDadosPerfil(sharedPreferences: SharedPreferences) {
        val nomeUsuario = sharedPreferences.getString("user_name", "Nome não encontrado")
        val emailUsuario = sharedPreferences.getString("user_email", "Email não encontrado")

        nomeUsuarioTextView.text = nomeUsuario
        emailUsuarioTextView.text = emailUsuario
    }

    // Sobrescreve o método onActivityResult para recarregar os dados após edição
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            carregarDadosPerfil(sharedPreferences)  // Recarrega os dados após edição
        }
    }
}
