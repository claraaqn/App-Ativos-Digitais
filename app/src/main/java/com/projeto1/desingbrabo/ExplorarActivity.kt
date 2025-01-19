package com.projeto1.desingbrabo

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.projeto1.desingbrabo.api.RetrofitInstance
import com.projeto1.desingbrabo.model.Image
import com.projeto1.desingbrabo.model.SearchResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ExplorarActivity : AppCompatActivity() {

    private var filtrosVisiveis = true
    private var isPremium = false
    private var isGratis = false
    private lateinit var searchInput: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var aviso: TextView
    private lateinit var imageAdapter: ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela_explorar)

        searchInput = findViewById(R.id.search_input)
        recyclerView = findViewById(R.id.recyclerView)
        val searchButton = findViewById<Button>(R.id.search_button)
        aviso = findViewById(R.id.aviso)

        // Botão para expandir/retrair filtros
        val buttonFiltros = findViewById<Button>(R.id.button_filtros)

        // Elementos de Licença
        val licenca = findViewById<TextView>(R.id.licenca)
        val buttonPremium = findViewById<Button>(R.id.button_premium)
        val buttonGratis = findViewById<Button>(R.id.button_gratis)

        // Elementos de Tipos
        val tipos = findViewById<TextView>(R.id.Tipos)
        val buttonVetores = findViewById<Button>(R.id.button_vetores)
        val buttonFotos = findViewById<Button>(R.id.button_fotos)
        val buttonAI = findViewById<Button>(R.id.button_ai)
        val buttonIcones = findViewById<Button>(R.id.button_icones)
        val button3D = findViewById<Button>(R.id.button_3d)
        val buttonMotion = findViewById<Button>(R.id.button_motions)
        val buttonMockups = findViewById<Button>(R.id.button_mockups)
        val buttonTextura = findViewById<Button>(R.id.button_textura)

        // Elementos de Formatos
        val formatos = findViewById<TextView>(R.id.formatos)
        val buttonJPG = findViewById<Button>(R.id.button_jpg)
        val buttonPNG = findViewById<Button>(R.id.button_png)
        val buttonSVG = findViewById<Button>(R.id.button_svg)
        val buttonPSD = findViewById<Button>(R.id.button_psd)
        val buttonPDF = findViewById<Button>(R.id.button_pdf)

        // Elementos de Cores
        val cores = findViewById<TextView>(R.id.cores)
        val buttonVermelho = findViewById<Button>(R.id.button_vermelho)
        val buttonAzul = findViewById<Button>(R.id.button_azul)
        val buttonVerde = findViewById<Button>(R.id.button_verde)
        val buttonAmarelo = findViewById<Button>(R.id.button_amarelo)
        val buttonRoxoo = findViewById<Button>(R.id.button_roxo)
        val buttonRosa = findViewById<Button>(R.id.button_rosa)
        val buttonLaranja = findViewById<Button>(R.id.button_laranja)
        val buttonMarrom = findViewById<Button>(R.id.button_marrom)
        val buttonCinza = findViewById<Button>(R.id.button_cinza)
        val buttonPreto = findViewById<Button>(R.id.button_preto)
        val buttonBranco = findViewById<Button>(R.id.button_branco)

        // Lista de filtros a serem controlados
        val filtros = listOf(
            licenca, buttonPremium, buttonGratis,
            tipos, buttonVetores, buttonFotos, buttonAI, button3D,
            formatos, buttonJPG, buttonPNG, buttonSVG,
            cores, buttonVermelho, buttonAzul, buttonVerde,
            buttonAmarelo, buttonRoxoo, buttonRosa, buttonLaranja,
            buttonMarrom, buttonCinza, buttonPreto, buttonBranco,
            buttonIcones, buttonMotion, buttonMockups, buttonTextura,
            buttonPSD, buttonPDF
        )

        filtros.forEach { it.visibility = View.GONE }

        buttonFiltros.setOnClickListener {
            filtrosVisiveis = !filtrosVisiveis

            val visibilidade = if (filtrosVisiveis) View.GONE else View.VISIBLE
            filtros.forEach { filtro ->
                filtro.visibility = visibilidade
            }

            val drawable = if (filtrosVisiveis) {
                ContextCompat.getDrawable(this, R.drawable.seta_lado_icon)
            } else {
                ContextCompat.getDrawable(this, R.drawable.seta_baixo_icon)
            }
            buttonFiltros.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)

        }

        recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        imageAdapter = ImageAdapter(emptyList(), this@ExplorarActivity) // Inicializa seu adaptador
        recyclerView.adapter = imageAdapter

        buttonPremium.setOnClickListener {
            isPremium = !isPremium
            updateButtonState(buttonPremium, isPremium)
        }

        buttonGratis.setOnClickListener {
            isGratis = !isGratis
            updateButtonState(buttonGratis, isGratis)
        }

        // Ação do botão de pesquisa
        searchButton.setOnClickListener {
            val query = searchInput.text.toString().trim()
            searchImages(query)
        }

        // Configuração da RecyclerView
        recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    }

    // Função para atualizar o estado visual dos botões
    private fun updateButtonState(button: Button, isSelected: Boolean) {
        if (isSelected) {
            button.setTextColor(Color.parseColor("#FFFFFF"))
            button.setBackgroundResource(R.drawable.bg_filtro_selecionado)
            button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_x, 0)
        } else {
            button.setTextColor(Color.parseColor("#556F86"))
            button.setBackgroundResource(R.drawable.bg_filtros)
            button.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        }
    }

    // Função para realizar a busca
    private fun searchImages(tag: String) {
        val call = RetrofitInstance.api.searchImages(tag, isPremium, isGratis)
        call.enqueue(object : Callback<List<Image>> {
            override fun onResponse(call: Call<List<Image>>, response: Response<List<Image>>) {
                if (response.isSuccessful && response.body() != null) {
                    val images = response.body()!!
                    if (images.isEmpty()) {
                        aviso.text = "Imagem não encontrada"
                        imageAdapter.clearImages()
                    } else {
                        aviso.visibility = View.GONE
                        aviso.setTextColor(Color.parseColor("#C00000")) // não tá funcionando ajeitar depois
                        imageAdapter.updateImages(images)  // Atualiza as imagens
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

    // Função para exibir as imagens no RecyclerView
    private fun exibirImagens(imagens: List<Image>) {
        val adapter = ImageAdapter(imagens, this@ExplorarActivity)
        recyclerView.adapter = adapter
    }
}


