package com.projeto1.desingbrabo

import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.projeto1.desingbrabo.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ConfiguracoesActivity : AppCompatActivity() {

    private lateinit var voltarButton: Button
    private lateinit var buttonSeguranca: Button
    private lateinit var buttonContas: Button
    private lateinit var buttonTermos: Button
    private lateinit var switchTemaEscuro: Switch
    private lateinit var switchAcessoCamera: Switch
    private lateinit var switchAcessoArmazenamento: Switch
    private lateinit var switchPermitirNotificacoes: Switch

    private lateinit var buttonExcluirConta: Button

    companion object {
        private const val REQUEST_CODE_CAMERA = 100
        const val REQUEST_CODE_NOTIFICACAO = 1001
        private const val REQUEST_PERMISSION_STORAGE = 200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela_configuracoes)

        voltarButton = findViewById(R.id.button_voltar)
        buttonSeguranca = findViewById(R.id.button_seguranca)
        buttonContas = findViewById(R.id.button_contas_conectadas)
        buttonTermos = findViewById(R.id.button_termos_condições)

        switchTemaEscuro = findViewById(R.id.custom_switch)
        switchAcessoCamera = findViewById(R.id.acesso_camera_witch)
        switchAcessoArmazenamento = findViewById(R.id.acesso_armazenamento_witch)
        switchPermitirNotificacoes = findViewById(R.id.permitir_notificacoes_switch)

        buttonExcluirConta = findViewById(R.id.excluir_conta)

        voltarButton.setOnClickListener {
            finish()
        }

        buttonSeguranca.setOnClickListener {
            val intent = Intent(this, SegurancaActivity::class.java)
            startActivity(intent)
        }

        buttonContas.setOnClickListener {
            // val intent = Intent(this, ContasConectadasActivity::class.java)
            startActivity(intent)
        }

        buttonTermos.setOnClickListener {
            val intent = Intent(this, TermosCondicoesActivity::class.java)
            startActivity(intent)
        }


        switchTemaEscuro.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                switchTemaEscuro.thumbDrawable = getDrawable(R.drawable.track)
                switchTemaEscuro.trackDrawable = getDrawable(R.drawable.fundo_switch_ativado)
            } else {
                switchTemaEscuro.thumbDrawable = getDrawable(R.drawable.track)
                switchTemaEscuro.trackDrawable = getDrawable(R.drawable.fundo_switch_desativadp)
            }
        }

        switchAcessoCamera.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                switchAcessoCamera.thumbDrawable = getDrawable(R.drawable.track)
                switchAcessoCamera.trackDrawable = getDrawable(R.drawable.fundo_switch_ativado)
                if (ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CODE_CAMERA)
                } else {
                    Toast.makeText(this, "Acesso à câmera já está habilitado", Toast.LENGTH_SHORT).show()
                }
            } else {
                switchAcessoCamera.thumbDrawable = getDrawable(R.drawable.track)
                switchAcessoCamera.trackDrawable = getDrawable(R.drawable.fundo_switch_desativadp)
                Toast.makeText(this, "Acesso à câmera desativado", Toast.LENGTH_SHORT).show()

            }
        }

        switchAcessoArmazenamento.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                switchAcessoArmazenamento.thumbDrawable = getDrawable(R.drawable.track)
                switchAcessoArmazenamento.trackDrawable = getDrawable(R.drawable.fundo_switch_ativado)
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES), REQUEST_CODE_CAMERA)
                } else {
                    Toast.makeText(this, "Permissão já concedida!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Acesso ao armazenamento desativado", Toast.LENGTH_SHORT).show()
                switchAcessoArmazenamento.thumbDrawable = getDrawable(R.drawable.track)
                switchAcessoArmazenamento.trackDrawable = getDrawable(R.drawable.fundo_switch_desativadp)
            }
        }

        switchPermitirNotificacoes.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    switchPermitirNotificacoes.thumbDrawable = getDrawable(R.drawable.track)
                    switchPermitirNotificacoes.trackDrawable =
                        getDrawable(R.drawable.fundo_switch_ativado)
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                            REQUEST_CODE_NOTIFICACAO
                        )
                    } else {
                        Toast.makeText(
                            this,
                            "Permissão de notificações já concedida",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(this, "Notificações desativadas", Toast.LENGTH_SHORT).show()
                    switchPermitirNotificacoes.thumbDrawable = getDrawable(R.drawable.track)
                    switchPermitirNotificacoes.trackDrawable =
                        getDrawable(R.drawable.fundo_switch_desativadp)
                }
            }

        buttonExcluirConta.setOnClickListener {
            AlertDialog.Builder(this).setTitle("Excluir Conta")
                .setMessage("Tem certeza de que deseja excluir sua conta? Esta ação não pode ser desfeita.")
                .setPositiveButton("Sim") { dialog, _ ->
                    val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
                    val userId = sharedPreferences.getInt("user_id", -1)

                    if (userId != -1) {
                        excluirConta(userId)
                    } else {
                        Toast.makeText(this@ConfiguracoesActivity, "Erro: ID do usuário não encontrado!", Toast.LENGTH_LONG).show()
                    }

                    dialog.dismiss()
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }
    private fun excluirConta(userId: Int) {
        val apiService = RetrofitInstance.api
        val call = apiService.delete_user(userId)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ConfiguracoesActivity, "Conta excluída com sucesso", Toast.LENGTH_LONG).show()
                    val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.clear()
                    editor.apply()

                    val intent = Intent(this@ConfiguracoesActivity, CadastroActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@ConfiguracoesActivity, "Erro ao excluir a conta", Toast.LENGTH_LONG).show()
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@ConfiguracoesActivity, "Falha ao conectar ao servidor", Toast.LENGTH_LONG).show()
            }
        })
    }
}
