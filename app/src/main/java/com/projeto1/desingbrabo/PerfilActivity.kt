package com.projeto1.desingbrabo

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.projeto1.desingbrabo.model.Perfil
import kotlinx.coroutines.launch

class PerfilActivity : AppCompatActivity() {

    private  lateinit var voltarButton: Button
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

        userViewModel = ViewModelProvider(this).get(Perfil::class.java)

        val userId = getUserId()

        if (userId != -1) {
            loadUserData(userId)
        }

        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val nomeUsuario = sharedPreferences.getString("user_name", "Nome não encontrado")
        val emailUsuario = sharedPreferences.getString("user_email", "Email não encontrado")

        nomeUsuarioTextView.text = nomeUsuario
        emailUsuarioTextView.text = emailUsuario

        voltarButton.setOnClickListener {
            val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()

            finish()
        }

        minhaAssinaturaButton.setOnClickListener {
            // val intent = Intent(this, MinhaAssinaturaActivity::class.java)
            startActivity(intent)
        }

        editarPerfilButton.setOnClickListener {
            // val intent = Intent(this@LoginActivity, EditarPerfilActivity::class.java)
            startActivity(intent)
        }

        configuracoesButton.setOnClickListener {
            // val intent = Intent(this, ConfiguracoesActivity::class.java)
            startActivity(intent)
        }

        sairButton.setOnClickListener {
            // Lógica para logout e voltar para a tela de login
            finish()
        }

        buttonHome.setOnClickListener {
            // val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        buttonUpload.setOnClickListener {
            // val intent = Intent(this, UploadActivity::class.java)
            startActivity(intent)
        }

        buttonColecao.setOnClickListener {
            // val intent = Intent(this, ColecaoActivity::class.java)
            startActivity(intent)
        }

        buttonCarrinho.setOnClickListener {
            // val intent = Intent(this, CarrinhoActivity::class.java)
            startActivity(intent)
        }

        buttonPerfil.setOnClickListener {
            // val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

    }

    private fun getUserId(): Int {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        return sharedPreferences.getInt("user_id", -1)
    }

    private fun loadUserData(userId: Int) {
        lifecycleScope.launch {
            userViewModel.userLiveData.observe(this@PerfilActivity) { user ->
                if (user != null) {
                    nomeUsuarioTextView.text = user.name
                    emailUsuarioTextView.text = user.email

                    // FAZER AS MODIFICAÇÕES PARA FOTO DE PERFIL
                }
            }
        }
    }
}
