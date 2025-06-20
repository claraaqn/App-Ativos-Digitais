package com.projeto1.desingbrabo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.projeto1.desingbrabo.api.RetrofitInstance
import com.projeto1.desingbrabo.model.Colaborador
import com.projeto1.desingbrabo.model.Image
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PerfilActivity : AppCompatActivity() {

    private lateinit var nomeUsuarioTextView: TextView
    private lateinit var emailUsuarioTextView: TextView
    private lateinit var fotoPerfilImageView: ImageView
    private lateinit var descricaoTextView: TextView
    private lateinit var minhaAssinaturaButton: Button
    private lateinit var editarPerfilButton: Button
    private lateinit var configuracoesButton: Button
    private lateinit var sairButton: Button

    private lateinit var buttonHome: Button
    private lateinit var buttonMeusProdutos: Button
    private lateinit var buttonExplorar: Button
    private lateinit var buttonCarrinho: Button
    private lateinit var buttonPerfil: Button

    private lateinit var searchInput: EditText
    private lateinit var spinnerTags: Spinner
    private val licenseEstado = mutableMapOf<Button, Boolean>()
    private val formatEstado = mutableMapOf<Button, Boolean>()
    private val coresEstado = mutableMapOf<ImageButton, Boolean>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var imageAdapter: ImageAdapter

    private lateinit var barraPerfil: View
    private lateinit var barraFiltros: View

    private lateinit var fotoPerfil: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela_perfil)

        fotoPerfil = findViewById(R.id.foto_perfil)

        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val idUsuario = sharedPreferences.getInt("user_id", -1)

        val capa: ImageView = findViewById(R.id.produto1)
        val seguidores: TextView = findViewById(R.id.quantidade_seguidores)
        val downloads: TextView = findViewById(R.id.quantidade_downloads)
        val views: TextView = findViewById(R.id.quantidade_views)
        val recursos: TextView = findViewById(R.id.quantidade_recurtos)
        val curtidas: TextView = findViewById(R.id.quantidade_curtidas)
        val descricao: TextView = findViewById(R.id.descricao_perfil)

        nomeUsuarioTextView = findViewById(R.id.nome_usuario)
        emailUsuarioTextView = findViewById(R.id.email_perfil)
        descricaoTextView = findViewById(R.id.descricao_perfil)
        fotoPerfilImageView = findViewById(R.id.foto_perfil)

        minhaAssinaturaButton = findViewById(R.id.button_minha_assinatura)
        editarPerfilButton = findViewById(R.id.button_editar_perfil)
        configuracoesButton = findViewById(R.id.button_configuracoes)
        sairButton = findViewById(R.id.button_sair)

        buttonHome = findViewById(R.id.button_home)
        buttonMeusProdutos = findViewById(R.id.button_meus_produtos)
        buttonExplorar = findViewById(R.id.button_explorar)
        buttonCarrinho = findViewById(R.id.button_carrinho)
        buttonPerfil = findViewById(R.id.button_perfil)

        val btnOpcoes: Button = findViewById(R.id.opcoes)
        val btnFechar: Button = findViewById(R.id.button_fechar)
        barraPerfil = findViewById(R.id.barra_perfil)

        val btnFiltros: Button = findViewById(R.id.button_filtros)
        barraFiltros = findViewById(R.id.barra_filtros)
        val btnFecharFiltros: Button = findViewById(R.id.fechar)

        val buttonSeguir: Button = findViewById(R.id.button_seguir)

        searchInput = findViewById(R.id.search_input)
        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        imageAdapter = ImageAdapter(emptyList(), this@PerfilActivity)
        recyclerView.adapter = imageAdapter

        RetrofitInstance.api.getImagemColaborador(idUsuario)
            .enqueue(object : Callback<List<Image>> {
                override fun onResponse(call: Call<List<Image>>, response: Response<List<Image>>) {
                    Log.d("API_RESPONSE", "Status: ${response.code()}")
                    Log.d("API_RESPONSE", "Body: ${response.body()}")
                    val images = response.body()
                    if (images != null) {
                        Log.d("API_RESPONSE", "Número de imagens: ${images.size}")
                        imageAdapter.updateImages(images)
                    } else {
                        Log.d("API_RESPONSE", "Resposta vazia ou nula")
                    }
                }
                override fun onFailure(call: Call<List<Image>>, t: Throwable) {
                    Log.e("API_ERROR", "Erro: ${t.message}", t)
                    Toast.makeText(this@PerfilActivity, "${t.message}", Toast.LENGTH_LONG).show()
                }
            })

        setupButtons()


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

        configuracoesButton.setOnClickListener {
            val intent = Intent(this, ConfiguracoesActivity::class.java)
            startActivity(intent)
        }

        // barra de navegaçãp
        findViewById<Button>(R.id.button_home).setOnClickListener {
            startActivity(Intent(this@PerfilActivity, HomeActivity::class.java))
        }

        findViewById<Button>(R.id.button_perfil).setOnClickListener {
            startActivity(Intent(this@PerfilActivity, PerfilActivity::class.java))
        }

        findViewById<Button>(R.id.button_meus_produtos).setOnClickListener {
            startActivity(Intent(this@PerfilActivity, DownloadActivity::class.java))
        }

        findViewById<Button>(R.id.button_explorar).setOnClickListener {
            startActivity(Intent(this@PerfilActivity, ExplorarActivity::class.java))
        }

        findViewById<Button>(R.id.button_carrinho).setOnClickListener {
            startActivity(Intent(this@PerfilActivity, CarrinhoActivity::class.java))
        }

        btnOpcoes.setOnClickListener {
            barraPerfil.visibility = View.VISIBLE
        }

        btnFechar.setOnClickListener {
            barraPerfil.visibility = View.GONE
        }

        btnFiltros.setOnClickListener {
            barraFiltros.visibility = View.VISIBLE
        }

        btnFecharFiltros.setOnClickListener {
            barraFiltros.visibility = View.GONE
        }



        RetrofitInstance.api.getColaborador(idUsuario).enqueue(object : Callback<Colaborador> {
            override fun onResponse(call: Call<Colaborador>, response: Response<Colaborador>) {
                if (response.isSuccessful) {
                    val colaborador = response.body()
                    if (colaborador != null) {
                        nomeUsuarioTextView.text = colaborador.userName ?: ""
                        seguidores.text = colaborador.totalSeguidores.toString()
                        downloads.text = colaborador.totalDownloads.toString()
                        curtidas.text = colaborador.totalCurtidas.toString()
                        views.text = colaborador.totalViews.toString()
                        recursos.text = colaborador.totalRecursos.toString()
                        descricao.text = colaborador.userDescription

                        Glide.with(this@PerfilActivity)
                            .load(colaborador.userProfile)
                            .placeholder(R.drawable.placeholder_image)
                            .error(R.drawable.produto4)
                            .into(fotoPerfil)

                        Glide.with(this@PerfilActivity)
                            .load(colaborador.userCape)
                            .placeholder(R.drawable.placeholder_image)
                            .error(R.drawable.produto4)
                            .into(capa)
                    } else {
                        Toast.makeText(this@PerfilActivity, "Produto não encontrado", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@PerfilActivity, "Erro ao carregar produto", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Colaborador>, t: Throwable) {
                Toast.makeText(this@PerfilActivity, "Erro: ${t.message}", Toast.LENGTH_LONG)
                    .show()
            }
        })

        var isSeguindo = false
        buttonSeguir.setOnClickListener {
            isSeguindo = !isSeguindo
            buttonSeguir.text = if (isSeguindo) "Seguindo" else "Seguir"
            buttonSeguir.setBackgroundResource(if (isSeguindo) R.drawable.bg_button_seguindo else R.drawable.bg_button_seguir)
            buttonSeguir.setCompoundDrawablesWithIntrinsicBounds(0, 0, if (isSeguindo) R.drawable.ic_check else R.drawable.ic_plus, 0)
        }
    }


    private fun searchImages(tag: String, formats: List<String>, color: List<String>) {

        RetrofitInstance.api.searchImages(
            tag = tag,
            isPremium = licenseEstado[findViewById(R.id.button_premium)] == true,
            isGratis = licenseEstado[findViewById(R.id.button_gratis)] == true,
            formats = formats,
            color = color,
            userId = intent.getIntExtra("idColaborador", -1)
        ).enqueue(object : Callback<List<Image>> {
            override fun onResponse(call: Call<List<Image>>, response: Response<List<Image>>) {
                val images = response.body()!!
                if (images.isEmpty()) {
                    imageAdapter.clearImages()
                } else {
                    imageAdapter.updateImages(images)
                }
            }
            override fun onFailure(call: Call<List<Image>>, t: Throwable) {
                Toast.makeText(this@PerfilActivity, "Erro ao conectar ao servidor", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupButtons() {
        val licenseButtons = mapOf(
            findViewById<Button>(R.id.button_premium) to "premium",
            findViewById<Button>(R.id.button_gratis) to "gratis"
        )

        val formatButtons = mapOf(
            findViewById<Button>(R.id.button_jpg) to "JPG",
            findViewById<Button>(R.id.button_png) to "PNG",
            findViewById<Button>(R.id.button_svg) to "SVG",
            findViewById<Button>(R.id.button_psd) to "PSD",
            findViewById<Button>(R.id.button_pdf) to "PDF",
            findViewById<Button>(R.id.button_vetores) to "Vetores",
            findViewById<Button>(R.id.button_fotos) to "Fotos",
            findViewById<Button>(R.id.button_ai) to "IA",
            findViewById<Button>(R.id.button_icones) to "Ícones",
            findViewById<Button>(R.id.button_motions) to "Motions",
            findViewById<Button>(R.id.button_mockups) to "Mockups",
            findViewById<Button>(R.id.button_3d) to "3D",
            findViewById<Button>(R.id.button_textura) to "Textura"
        )

        licenseButtons.forEach { (button, license) ->
            button.tag = license
            button.setOnClickListener {
                val isSelected = licenseEstado[button] ?: false
                licenseEstado[button] = !isSelected
                updateButtonState(button, !isSelected)
                searchImages(
                    searchInput.text.toString().trim(),
                    getSelectedFormats(),
                    getSelectedColor()
                )
            }
        }

        formatButtons.forEach { (button, format) ->
            button.tag = format
            button.setOnClickListener {
                val isSelected = formatEstado[button] ?: false
                formatEstado[button] = !isSelected
                updateButtonState(button, !isSelected)
                searchImages(
                    searchInput.text.toString().trim(),
                    getSelectedFormats(),
                    getSelectedColor()
                )
            }
        }

        val colorButtons = mapOf(
            findViewById<ImageButton>(R.id.button_vermelho) to "vermelho",
            findViewById<ImageButton>(R.id.button_azul) to "azul",
            findViewById<ImageButton>(R.id.button_verde) to "verde",
            findViewById<ImageButton>(R.id.button_amarelo) to "amarelo",
            findViewById<ImageButton>(R.id.button_roxo) to "roxo",
            findViewById<ImageButton>(R.id.button_rosa) to "rosa",
            findViewById<ImageButton>(R.id.button_laranja) to "laranja",
            findViewById<ImageButton>(R.id.button_marrom) to "marrom",
            findViewById<ImageButton>(R.id.button_cinza) to "cinza",
            findViewById<ImageButton>(R.id.button_preto) to "preto",
            findViewById<ImageButton>(R.id.button_branco) to "branco"
        )

        colorButtons.forEach { (button, color) ->
            button.tag = color
            button.setOnClickListener {
                val isSelected = coresEstado[button] ?: false
                coresEstado[button] = !isSelected
                updateCores(button, !isSelected)
                searchImages(
                    searchInput.text.toString().trim(),
                    getSelectedFormats(),
                    getSelectedColor()
                )
            }
        }

        findViewById<Button>(R.id.search_button).setOnClickListener {
            searchImages(
                searchInput.text.toString().trim(),
                getSelectedFormats(),
                getSelectedColor()
            )
        }

        findViewById<Button>(R.id.button_home).setOnClickListener {
            startActivity(Intent(this@PerfilActivity   , HomeActivity::class.java))
        }

        findViewById<Button>(R.id.button_perfil).setOnClickListener {
            startActivity(Intent(this@PerfilActivity, PerfilActivity::class.java))
        }

        findViewById<Button>(R.id.button_meus_produtos).setOnClickListener {
            startActivity(Intent(this@PerfilActivity, DownloadActivity::class.java))
        }

        findViewById<Button>(R.id.button_explorar).setOnClickListener {
            startActivity(Intent(this@PerfilActivity, ExplorarActivity::class.java))
        }

        findViewById<Button>(R.id.button_carrinho).setOnClickListener {
            startActivity(Intent(this@PerfilActivity, CarrinhoActivity::class.java))
        }
    }

    private fun getSelectedFormats(): List<String> {
        return formatEstado.filter { it.value }
            .mapNotNull { it.key.tag?.toString() }
    }

    private fun getSelectedColor(): List<String> {
        return coresEstado.filter { it.value }
            .mapNotNull { it.key.tag?.toString() }
    }

    private fun updateButtonState(button: Button, isSelected: Boolean) {
        if (isSelected) {
            button.setTextColor(Color.parseColor("#FFFFFF"))
            button.setBackgroundResource(R.drawable.bg_filtro_selecionado)
            button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_x, 0)
        } else {
            button.setTextColor(Color.parseColor("#010715"))
            button.setBackgroundResource(R.drawable.bg_filtros)
            button.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        }
    }

    private fun updateCores(button: ImageButton, isSelected: Boolean) {
        if (isSelected) {
            button.setImageResource(R.drawable.icon_x_cinza)
        } else {
            button.setImageResource(0)
        }
    }
}
