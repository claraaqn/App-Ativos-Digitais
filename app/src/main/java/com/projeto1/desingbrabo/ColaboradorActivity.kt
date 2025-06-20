package com.projeto1.desingbrabo

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.projeto1.desingbrabo.api.RetrofitInstance
import com.projeto1.desingbrabo.model.Colaborador
import com.projeto1.desingbrabo.model.Image
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ColaboradorActivity : AppCompatActivity() {

    private lateinit var searchInput: EditText
    private lateinit var spinnerTags: Spinner
    private val licenseEstado = mutableMapOf<Button, Boolean>()
    private val formatEstado = mutableMapOf<Button, Boolean>()
    private val coresEstado = mutableMapOf<ImageButton, Boolean>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var imageAdapter: ImageAdapter

    private lateinit var barraFiltros: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela_colaborador)

        val idColaborador = intent.getIntExtra("idColaborador", -1)

        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val idUsuario = sharedPreferences.getInt("user_id", -1)

        if(idColaborador == idUsuario) {
            startActivity(Intent(this@ColaboradorActivity, PerfilActivity::class.java))
        }

        searchInput = findViewById(R.id.search_input)
        spinnerTags = findViewById(R.id.spinner_tags)

        val nomeColaborador: TextView = findViewById(R.id.nome_usuario)
        val seguidores: TextView = findViewById(R.id.quantidade_seguidores)
        val downloads: TextView = findViewById(R.id.quantidade_downloads)
        val views: TextView = findViewById(R.id.quantidade_views)
        val recursos: TextView = findViewById(R.id.quantidade_recurtos)
        val curtidas: TextView = findViewById(R.id.quantidade_curtidas)
        val descricao: TextView = findViewById(R.id.descricao_perfil)

        val fotoPerfil: ImageView = findViewById(R.id.foto_perfil)
        val capa: ImageView = findViewById(R.id.capa)

        recyclerView = findViewById(R.id.recyclerView)

        val btnFiltros: Button = findViewById(R.id.button_filtros)
        barraFiltros = findViewById(R.id.barra_filtros)
        val btnFecharFiltros: Button = findViewById(R.id.fechar)

        recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        imageAdapter = ImageAdapter(emptyList(), this@ColaboradorActivity)
        recyclerView.adapter = imageAdapter

        setupButtons()

        RetrofitInstance.api.getColaborador(idColaborador).enqueue(object : Callback<Colaborador> {
            override fun onResponse(call: Call<Colaborador>, response: Response<Colaborador>) {
                if (response.isSuccessful) {
                    val colaborador = response.body()
                    if (colaborador != null) {
                        nomeColaborador.text = colaborador.userName ?: ""
                        seguidores.text = colaborador.totalSeguidores.toString()
                        downloads.text = colaborador.totalDownloads.toString()
                        curtidas.text = colaborador.totalCurtidas.toString()
                        views.text = colaborador.totalViews.toString()
                        recursos.text = colaborador.totalRecursos.toString()
                        descricao.text = colaborador.userDescription

                        Glide.with(this@ColaboradorActivity)
                            .load(colaborador.userProfile)
                            .placeholder(R.drawable.placeholder_image)
                            .error(R.drawable.produto4)
                            .into(fotoPerfil)

                        Glide.with(this@ColaboradorActivity)
                            .load(colaborador.userCape)
                            .placeholder(R.drawable.placeholder_image)
                            .error(R.drawable.produto4)
                            .into(capa)
                    } else {
                        Toast.makeText(this@ColaboradorActivity, "Produto não encontrado", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@ColaboradorActivity, "Erro ao carregar produto", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Colaborador>, t: Throwable) {
                Toast.makeText(this@ColaboradorActivity, "Erro: ${t.message}", Toast.LENGTH_LONG)
                    .show()
            }
        })

        RetrofitInstance.api.getImagemColaborador(idColaborador)
            .enqueue(object : Callback<List<Image>> {
                override fun onResponse(call: Call<List<Image>>, response: Response<List<Image>>) {
                    val images = response.body()
                    if (images != null) {
                        imageAdapter.updateImages(images)
                    }
                }
                override fun onFailure(call: Call<List<Image>>, t: Throwable) {
                    Toast.makeText(this@ColaboradorActivity, "${t.message}", Toast.LENGTH_LONG).show()
                }
            })

        btnFiltros.setOnClickListener {
            barraFiltros.visibility = View.VISIBLE
       }

        btnFecharFiltros.setOnClickListener {
            barraFiltros.visibility = View.GONE
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
                Toast.makeText(this@ColaboradorActivity, "Erro ao conectar ao servidor", Toast.LENGTH_SHORT).show()
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
            startActivity(Intent(this@ColaboradorActivity   , HomeActivity::class.java))
        }

        findViewById<Button>(R.id.button_perfil).setOnClickListener {
            startActivity(Intent(this@ColaboradorActivity, PerfilActivity::class.java))
        }

        findViewById<Button>(R.id.button_meus_produtos).setOnClickListener {
            startActivity(Intent(this@ColaboradorActivity, DownloadActivity::class.java))
        }

        findViewById<Button>(R.id.button_explorar).setOnClickListener {
            startActivity(Intent(this@ColaboradorActivity, ExplorarActivity::class.java))
        }

        findViewById<Button>(R.id.button_carrinho).setOnClickListener {
            startActivity(Intent(this@ColaboradorActivity, CarrinhoActivity::class.java))
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