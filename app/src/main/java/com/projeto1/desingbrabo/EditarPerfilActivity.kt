package com.projeto1.desingbrabo

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.AmazonClientException
import com.projeto1.desingbrabo.api.RetrofitInstance
import com.projeto1.desingbrabo.model.GenericResponse
import com.projeto1.desingbrabo.model.UpdateProfileRequest
import com.yalantis.ucrop.UCrop
import java.io.File
import com.amazonaws.services.s3.model.PutObjectRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.projeto1.desingbrabo.api.ConfigHelper
import com.projeto1.desingbrabo.api.S3Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Properties

class EditarPerfilActivity : AppCompatActivity() {

    private lateinit var fotoPerfil: ImageView
    private var selectedImageUri: Uri? = null

    private val REQUEST_CODE_GALLERY = 100
    private val REQUEST_CODE_CROP = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela_editar_perfil)

        ConfigHelper.initialize(this@EditarPerfilActivity)

        fotoPerfil = findViewById(R.id.foto_perfil)

        val voltar: Button = findViewById(R.id.button_voltar)
        val nomeEditText: EditText = findViewById(R.id.edit_nome)
        val emailEditText: EditText = findViewById(R.id.edit_text_email)
        val telefoneEditText: EditText = findViewById(R.id.edit_text_telefone)
        val descricaoEditText: EditText = findViewById(R.id.edit_text_descricao)
        val salvarButton: Button = findViewById(R.id.salvar_button)
        val btnFotoPerfil: Button = findViewById(R.id.editar_foto)

        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val idUsuario = sharedPreferences.getInt("user_id", -1)

        nomeEditText.setText(sharedPreferences.getString("user_name", ""))
        emailEditText.setText(sharedPreferences.getString("user_email", ""))
        telefoneEditText.setText(sharedPreferences.getString("user_phone", ""))  // Corrigido para "user_phone"
        descricaoEditText.setText(sharedPreferences.getString("user_description", ""))

        val currentImageUrl = sharedPreferences.getString("user_profile_image", "")
        Log.d("EditarPerfil", "URL da imagem: $currentImageUrl")

        Glide.with(this@EditarPerfilActivity)
            .load(currentImageUrl)
            .skipMemoryCache(true)
            .error(R.drawable.icon_fechar)
            .into(fotoPerfil)

        btnFotoPerfil.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE_GALLERY)
        }

        salvarButton.setOnClickListener {
            val novoNome = nomeEditText.text.toString()
            val novoEmail = emailEditText.text.toString()
            val novoTelefone = telefoneEditText.text.toString()
            val novaDescricao = descricaoEditText.text.toString()

            if (novoNome.isNotEmpty() && novoEmail.isNotEmpty() && novoTelefone.isNotEmpty() && novaDescricao.isNotEmpty()) {
                if (selectedImageUri != null) {
                    handleImageSelection(selectedImageUri!!, idUsuario) { imageUrl ->
                        if (imageUrl != null) {
                            Log.d("UploadImage", "URL da imagem retornada")
                            saveData(idUsuario, novoNome, novoEmail, novoTelefone, novaDescricao, imageUrl)
                        } else {
                            Toast.makeText(this, "Erro ao salvar imagem, tente novamente.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    saveData(idUsuario, novoNome, novoEmail, novoTelefone, novaDescricao, currentImageUrl ?: "")
                }
            } else {
                Toast.makeText(this@EditarPerfilActivity, "Por favor, preencha todos os campos!", Toast.LENGTH_SHORT).show()
            }
        }

        voltar.setOnClickListener { finish() }
    }

    private fun saveData(userId: Int, nome: String, email: String, phone: String, descricao: String, imageUrl: String) {
        val editor = getSharedPreferences("user_prefs", Context.MODE_PRIVATE).edit()
        editor.putString("user_name", nome)
        editor.putString("user_email", email)
        editor.putString("user_phone", phone)
        editor.putString("user_description", descricao)
        editor.putString("user_profile_image", imageUrl)
        editor.apply()

        salvarBanco(userId, nome, email, phone, descricao, imageUrl)
        setResult(RESULT_OK)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_GALLERY -> {
                    data?.data?.let { uri ->
                        val fileName = "cropped_image_${System.currentTimeMillis()}.jpg"
                        val destinationUri = Uri.fromFile(File(cacheDir, fileName))

                        UCrop.of(uri, destinationUri)
                            .withAspectRatio(1f, 1f)
                            .start(this@EditarPerfilActivity)
                    }
                }
                UCrop.REQUEST_CROP -> {
                    val resultUri = data?.let { UCrop.getOutput(it) }
                    if (resultUri != null) {
                        selectedImageUri = resultUri
                        Glide.with(this@EditarPerfilActivity)
                            .load(resultUri)
                            .skipMemoryCache(true)
                            .into(fotoPerfil)
                    } else {
                        Toast.makeText(this, "Erro ao obter imagem cortada", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = data?.let { UCrop.getError(it) }
            cropError?.printStackTrace()
            cropError?.message?.let {
                Toast.makeText(this, "Erro ao cortar imagem: $it", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun salvarBanco(userId: Int, nome: String, email: String, phone: String, descricao: String, imageUrl: String) {
        val novo = UpdateProfileRequest(
            userId = userId,
            nome = nome,
            email = email,
            phone = phone,
            descricao = descricao,
            userProfile = imageUrl
        )

        RetrofitInstance.api.updateProfile(novo).enqueue(object : Callback<GenericResponse> {
            override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (it.success) {
                            Toast.makeText(this@EditarPerfilActivity, "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@EditarPerfilActivity, "Erro ao atualizar perfil: ${it.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(this@EditarPerfilActivity, "Erro na resposta do servidor", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                Toast.makeText(this@EditarPerfilActivity, "Falha na conexão: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private suspend fun uploadImageToWasabi(file: File, userId: Int): String? {
        return withContext(Dispatchers.IO) {
            var retryCount = 0
            val maxRetries = 3

            while (retryCount < maxRetries) {
                try {
                    if (!file.exists() || !file.isFile) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@EditarPerfilActivity, "Arquivo inválido ou não encontrado.", Toast.LENGTH_LONG).show()
                        }
                        return@withContext null
                    }

                    val bucketName = getBucketName()
                    val key = "fotoPerfil/profile_$userId.jpg"

                    if (S3Client.client == null) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@EditarPerfilActivity, "Erro: Cliente S3 não inicializado.", Toast.LENGTH_LONG).show()
                        }
                        return@withContext null
                    }

                    Log.d("UploadImage", "Iniciando upload do arquivo")

                    S3Client.client.putObject(PutObjectRequest(bucketName, key, file))

                    Log.d("UploadImage", "Upload concluído com sucesso.")

                    val imageUrl = "https://$bucketName.s3.wasabisys.com/$key"
                    return@withContext imageUrl

                } catch (e: AmazonClientException) {
                    retryCount++
                    if (retryCount == maxRetries) {
                        Log.e("UploadImage", "Falha após $maxRetries tentativas: ${e.message}")
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@EditarPerfilActivity, "Erro no cliente S3: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                        return@withContext null
                    }
                    Log.d("UploadImage", "Tentativa $retryCount falhou. Tentando novamente...")
                }
            }
            null
        }
    }

    private fun handleImageSelection(imageUri: Uri, userId: Int, onComplete: (String?) -> Unit) {
        val file = uriToFile(imageUri)
        if (file != null) {
            CoroutineScope(Dispatchers.Main).launch {
                val imageUrl = withContext(Dispatchers.IO) {
                    uploadImageToWasabi(file, userId)
                }
                if (imageUrl != null) {
                    saveImageUrlToPreferences(imageUrl)
                    onComplete(imageUrl)
                } else {
                    Toast.makeText(this@EditarPerfilActivity, "Falha ao enviar imagem", Toast.LENGTH_SHORT).show()
                    onComplete(null)
                }
            }
        } else {
            Toast.makeText(this@EditarPerfilActivity, "Erro ao processar imagem", Toast.LENGTH_SHORT).show()
            onComplete(null)
        }
    }

    private fun saveImageUrlToPreferences(imageUrl: String) {
        getSharedPreferences("user_prefs", Context.MODE_PRIVATE).edit()
            .putString("user_profile_image", imageUrl)
            .apply()
    }

    private fun uriToFile(uri: Uri): File? {
        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val tempFile = File.createTempFile("upload_", ".jpg", cacheDir)
            inputStream.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getBucketName(): String {
        val properties = Properties()
        assets.open("local.properties").use {
            properties.load(it)
        }
        return properties.getProperty("wasabi.bucket.name", "default_bucket_name")
    }
}