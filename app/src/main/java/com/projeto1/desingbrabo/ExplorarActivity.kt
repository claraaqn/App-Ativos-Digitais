package com.projeto1.desingbrabo

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.projeto1.desingbrabo.api.RetrofitInstance
import com.projeto1.desingbrabo.model.Image
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ExplorarActivity : AppCompatActivity() {

    private var filtrosVisiveis = true

    private lateinit var searchInput: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var aviso: TextView
    private lateinit var imageAdapter: ImageAdapter

    private val licenseEstado = mutableMapOf<Button, Boolean>()
    private val formatEstado  = mutableMapOf<Button, Boolean>()
    private val coresEstado = mutableMapOf<ImageButton, Boolean>()

    private lateinit var spinnerTags: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela_explorar)

        initViews()
        setupRecyclerView()
        setupFiltros()
        setupButtons()
        setupSpinner()
    }

    private fun initViews() {
        searchInput = findViewById(R.id.search_input)
        recyclerView = findViewById(R.id.recyclerView)
        aviso = findViewById(R.id.aviso)
        spinnerTags = findViewById(R.id.spinner_tags)
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        imageAdapter = ImageAdapter(emptyList(), this@ExplorarActivity)
        recyclerView.adapter = imageAdapter
    }

    private fun setupFiltros() {
        val buttonFiltros = findViewById<Button>(R.id.button_filtros)

        val filtros: List<View> = listOf(
            findViewById<TextView>(R.id.licenca),
            findViewById<Button>(R.id.button_premium),
            findViewById<Button>(R.id.button_gratis),
            findViewById<TextView>(R.id.Tipos),
            findViewById<Button>(R.id.button_vetores),
            findViewById<Button>(R.id.button_fotos),
            findViewById<Button>(R.id.button_ai),
            findViewById<Button>(R.id.button_icones),
            findViewById<Button>(R.id.button_3d),
            findViewById<Button>(R.id.button_motions),
            findViewById<Button>(R.id.button_mockups),
            findViewById<Button>(R.id.button_textura),
            findViewById<Button>(R.id.formatos),
            findViewById<Button>(R.id.button_jpg),
            findViewById<Button>(R.id.button_png),
            findViewById<Button>(R.id.button_svg),
            findViewById<Button>(R.id.button_psd),
            findViewById<Button>(R.id.button_pdf),
            findViewById<TextView>(R.id.cores),
            findViewById<ImageButton>(R.id.button_vermelho),
            findViewById<ImageButton>(R.id.button_azul),
            findViewById<ImageButton>(R.id.button_verde),
            findViewById<ImageButton>(R.id.button_amarelo),
            findViewById<ImageButton>(R.id.button_roxo),
            findViewById<ImageButton>(R.id.button_rosa),
            findViewById<ImageButton>(R.id.button_laranja),
            findViewById<ImageButton>(R.id.button_marrom),
            findViewById<ImageButton>(R.id.button_cinza),
            findViewById<ImageButton>(R.id.button_preto),
            findViewById<ImageButton>(R.id.button_branco)
        )

        filtros.forEach { it.visibility = View.GONE }

        buttonFiltros.setOnClickListener {
            filtrosVisiveis = !filtrosVisiveis
            val visibilidade = if (filtrosVisiveis) View.GONE else View.VISIBLE
            filtros.forEach { it.visibility = visibilidade }

            val drawable = if (filtrosVisiveis) {
                ContextCompat.getDrawable(this, R.drawable.seta_lado_icon)
            } else {
                ContextCompat.getDrawable(this, R.drawable.seta_baixo_icon)
            }
            buttonFiltros.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        }
    }

    private fun setupButtons() {
        // Botões de licença (premium e gratis)
        val licenseButtons = mapOf(
            findViewById<Button>(R.id.button_premium) to "premium",
            findViewById<Button>(R.id.button_gratis) to "gratis"
        )

        // Botões de formatos (JPG, PNG, etc.)
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

        // Configura os botões de licença
        licenseButtons.forEach { (button, license) ->
            button.tag = license
            button.setOnClickListener {
                val isSelected = licenseEstado[button] ?: false
                licenseEstado[button] = !isSelected
                updateButtonState(button, !isSelected)
                searchImages(
                    searchInput.text.toString().trim(),
                    getSelectedFormats(),
                    spinnerTags.selectedItem.toString(),
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
                    spinnerTags.selectedItem.toString(),
                    getSelectedColor()
                )
            }
        }

        // Configura os botões de cores (mantido igual)
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
                    spinnerTags.selectedItem.toString(),
                    getSelectedColor()
                )
            }
        }

        findViewById<Button>(R.id.search_button).setOnClickListener {
            searchImages(
                searchInput.text.toString().trim(),
                getSelectedFormats(),
                spinnerTags.selectedItem.toString(),
                getSelectedColor()
            )
        }

        findViewById<Button>(R.id.button_home).setOnClickListener {
            startActivity(Intent(this@ExplorarActivity, HomeActivity::class.java))
        }

        findViewById<Button>(R.id.button_perfil).setOnClickListener {
            startActivity(Intent(this@ExplorarActivity, PerfilActivity::class.java))
        }

        findViewById<Button>(R.id.button_meus_produtos).setOnClickListener {
            startActivity(Intent(this@ExplorarActivity, DownloadActivity::class.java))
        }

        findViewById<Button>(R.id.button_explorar).setOnClickListener {
            startActivity(Intent(this@ExplorarActivity, ExplorarActivity::class.java))
        }

        findViewById<Button>(R.id.button_carrinho).setOnClickListener {
            startActivity(Intent(this@ExplorarActivity, CarrinhoActivity::class.java))
        }
    }

    private fun setupSpinner() {
        val tagsArray = resources.getStringArray(R.array.tags)

        spinnerTags.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCategoria = tagsArray[position]
                searchImages(searchInput.text.toString().trim(), getSelectedFormats(), selectedCategoria, getSelectedColor())
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Chamado quando nada é selecionado (opcional)
            }
        }
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

    private fun getSelectedFormats(): List<String> {
        return formatEstado.filter { it.value }
            .mapNotNull { it.key.tag?.toString() }
    }

    private fun getSelectedColor(): List<String> {
        return coresEstado.filter { it.value }
            .mapNotNull { it.key.tag?.toString() }
    }

    private fun searchImages(tag: String, formats: List<String>, categoria: String, color: List<String>) {
        val selectedCategoria = if (spinnerTags.selectedItem.toString() != "CATEGORIAS") categoria else ""

        val call = RetrofitInstance.api.searchImages(

            tag = tag,
            isPremium = licenseEstado[findViewById(R.id.button_premium)] == true,
            isGratis = licenseEstado[findViewById(R.id.button_gratis)] == true,
            formats = formats,
            categoria = selectedCategoria,
            color = color,
            userId = null
        )

        call.enqueue(object : Callback<List<Image>> {
            override fun onResponse(call: Call<List<Image>>, response: Response<List<Image>>) {
                if (response.isSuccessful && response.body() != null) {
                    val images = response.body()!!
                    if (images.isEmpty()) {
                        aviso.text = "Nenhuma imagem encontrada"
                        aviso.visibility = View.VISIBLE
                        imageAdapter.clearImages()
                    } else {
                        aviso.visibility = View.GONE
                        imageAdapter.updateImages(images)
                    }
                } else {
                    Toast.makeText(this@ExplorarActivity, "Erro ao buscar imagens", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Image>>, t: Throwable) {
                Toast.makeText(this@ExplorarActivity, "Erro ao conectar ao servidor", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
