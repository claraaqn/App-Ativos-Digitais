package com.projeto1.desingbrabo

import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.provider.Settings
import android.net.Uri
import android.provider.MediaStore


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
        private const val REQUEST_CODE_STORAGE = 101
        const val REQUEST_CODE_NOTIFICACAO = 1001
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
            // Lógica ao clicar no botão de segurança
        }

        buttonContas.setOnClickListener {
            // Lógica ao clicar no botão de contas conectadas
        }

        buttonTermos.setOnClickListener {
            // Lógica ao clicar no botão de termos e condições
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

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    startActivityForResult(intent, REQUEST_CODE_STORAGE)
                } else {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            REQUEST_CODE_STORAGE
                        )
                    }
                }
            } else {
                switchAcessoArmazenamento.thumbDrawable = getDrawable(R.drawable.track)
                switchAcessoArmazenamento.trackDrawable = getDrawable(R.drawable.fundo_switch_desativadp)
                Toast.makeText(this, "Acesso ao armazenamento desativado", Toast.LENGTH_SHORT).show()
            }
        }

        fun acessarFotosEVideos() {
            // Lógica para acessar fotos e vídeos usando MediaStore (Android 10 ou superior)
            val cursor = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME),
                null, null, null
            )
            // Aqui você pode processar o cursor para acessar as imagens, por exemplo.
            cursor?.use {
                // Processa as imagens (se necessário)
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
                // Lógica para excluir a conta
            }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permissão concedida
                    Toast.makeText(this, "Permissão de acesso ao armazenamento concedida", Toast.LENGTH_SHORT).show()
                } else {
                    // Permissão negada
                    Toast.makeText(this, "Permissão de acesso ao armazenamento negada", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
