package com.projeto1.desingbrabo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.projeto1.desingbrabo.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.projeto1.desingbrabo.model.PasswordChangeRequest
import com.projeto1.desingbrabo.model.GenericResponse

class SegurancaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela_seguranca)

        val buttonVoltar = findViewById<Button>(R.id.button_voltar)
        val senhaAtual = findViewById<EditText>(R.id.senha_atual)
        val novaSenha = findViewById<EditText>(R.id.nova_senha)
        val confirmarNovaSenha = findViewById<EditText>(R.id.confirmar_nova_senha)
        val salvarButton = findViewById<Button>(R.id.salvar_button)

        buttonVoltar.setOnClickListener {
            finish()
        }

        salvarButton.setOnClickListener {
            val senhaAtualTexto = senhaAtual.text.toString()
            val novaSenhaTexto = novaSenha.text.toString()
            val confirmarNovaSenhaTexto = confirmarNovaSenha.text.toString()

            if (senhaAtualTexto.isEmpty() || novaSenhaTexto.isEmpty() || confirmarNovaSenhaTexto.isEmpty()) {
                Toast.makeText(this, "Todos os campos devem ser preenchidos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (novaSenhaTexto != confirmarNovaSenhaTexto) {
                Toast.makeText(this, "As novas senhas não coincidem.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Recupera o userId do SharedPreferences
            val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
            val userId = sharedPreferences.getInt("user_id", -1)

            if (userId == -1) {
                Toast.makeText(this, "Erro ao identificar o usuário. Faça login novamente.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Chama a função para alterar a senha
            alterarSenha(userId, senhaAtualTexto, novaSenhaTexto)
        }
    }

    private fun alterarSenha(userId: Int, senhaAtual: String, novaSenha: String) {
        val apiService = RetrofitInstance.api
        val passwordChangeRequest = PasswordChangeRequest(userId, senhaAtual, novaSenha)

        apiService.changePassword(passwordChangeRequest).enqueue(object : Callback<GenericResponse> {
            override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@SegurancaActivity, "Senha alterada com sucesso!", Toast.LENGTH_LONG).show()
                    // Redirecionar para a tela de perfil
                    val intent = Intent(this@SegurancaActivity, PerfilActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@SegurancaActivity, "Erro ao alterar a senha: ${response.message()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                Toast.makeText(this@SegurancaActivity, "Erro na conexão: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
