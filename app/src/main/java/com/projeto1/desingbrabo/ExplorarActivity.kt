package com.projeto1.desingbrabo

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.projeto1.desingbrabo.api.RetrofitInstance
import com.projeto1.desingbrabo.model.Image
import com.projeto1.desingbrabo.model.SearchResponse
import okhttp3.internal.format
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ExplorarActivity : AppCompatActivity() {

    private var filtrosVisiveis = true

    private lateinit var searchInput: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var aviso: TextView
    private lateinit var imageAdapter: ImageAdapter

    private var isPremium = false
    private var isGratis = false

    private var isVetores = false
    private var isFotos = false
    private var isIA = false
    private var isIcones = false
    private var isMotions = false
    private var isMockups = false
    private var isTextura = false
    private var is3D = false

    private var isJPG = false
    private var isPNG = false
    private var isSVG = false
    private var isPSD = false
    private var isPDF = false

    private var isVermelho = false
    private var isAzul = false
    private var isVerde = false
    private var isAmarelo = false
    private var isRoxo = false
    private var isRosa = false
    private var isLaranja = false
    private var isMarrom = false
    private var isCinza = false
    private var isPreto = false
    private var isBranco = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela_explorar)

        val categoriaSelecionada = intent.getStringExtra("categoria")
        if (categoriaSelecionada != null) {
            carregarImagensPorCategoria(categoriaSelecionada)
        }

        searchInput = findViewById(R.id.search_input)
        recyclerView = findViewById(R.id.recyclerView)
        val searchButton = findViewById<Button>(R.id.search_button)
        aviso = findViewById(R.id.aviso)

        val buttonFiltros = findViewById<Button>(R.id.button_filtros)

        val licenca = findViewById<TextView>(R.id.licenca)
        val buttonPremium = findViewById<Button>(R.id.button_premium)
        val buttonGratis = findViewById<Button>(R.id.button_gratis)

        val tipos = findViewById<TextView>(R.id.Tipos)
        val buttonVetores = findViewById<Button>(R.id.button_vetores)
        val buttonFotos = findViewById<Button>(R.id.button_fotos)
        val buttonIA = findViewById<Button>(R.id.button_ai)
        val buttonIcones = findViewById<Button>(R.id.button_icones)
        val button3D = findViewById<Button>(R.id.button_3d)
        val buttonMotion = findViewById<Button>(R.id.button_motions)
        val buttonMockups = findViewById<Button>(R.id.button_mockups)
        val buttonTextura = findViewById<Button>(R.id.button_textura)

        val formatos = findViewById<TextView>(R.id.formatos)
        val buttonJPG = findViewById<Button>(R.id.button_jpg)
        val buttonPNG = findViewById<Button>(R.id.button_png)
        val buttonSVG = findViewById<Button>(R.id.button_svg)
        val buttonPSD = findViewById<Button>(R.id.button_psd)
        val buttonPDF = findViewById<Button>(R.id.button_pdf)

        val cores = findViewById<TextView>(R.id.cores)
        val buttonVermelho = findViewById<ImageButton>(R.id.button_vermelho)
        val buttonAzul = findViewById<ImageButton>(R.id.button_azul)
        val buttonVerde = findViewById<ImageButton>(R.id.button_verde)
        val buttonAmarelo = findViewById<ImageButton>(R.id.button_amarelo)
        val buttonRoxoo = findViewById<ImageButton>(R.id.button_roxo)
        val buttonRosa = findViewById<ImageButton>(R.id.button_rosa)
        val buttonLaranja = findViewById<ImageButton>(R.id.button_laranja)
        val buttonMarrom = findViewById<ImageButton>(R.id.button_marrom)
        val buttonCinza = findViewById<ImageButton>(R.id.button_cinza)
        val buttonPreto = findViewById<ImageButton>(R.id.button_preto)
        val buttonBranco = findViewById<ImageButton>(R.id.button_branco)

        val buttonHome: Button = findViewById(R.id.button_home)
        val buttonMeusExplorar: Button = findViewById(R.id.button_explorar)
        val buttonMeusProdutos: Button = findViewById(R.id.button_meus_produtos)
        val buttonPerfil: Button = findViewById(R.id.button_perfil)

        // Lista de filtros a serem controlados
        val filtros = listOf(
            licenca, buttonPremium, buttonGratis,
            tipos, buttonVetores, buttonFotos, buttonIA, button3D,
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
        imageAdapter = ImageAdapter(emptyList(), this@ExplorarActivity)
        recyclerView.adapter = imageAdapter

        buttonPremium.setOnClickListener {
            isPremium = !isPremium
            updateButtonState(buttonPremium, isPremium)
        }

        buttonGratis.setOnClickListener {
            isGratis = !isGratis
            updateButtonState(buttonGratis, isGratis)
        }

        buttonJPG.setOnClickListener {
            isJPG = !isJPG
            updateButtonState(buttonJPG, isJPG)
        }

        buttonPNG.setOnClickListener {
            isPNG = !isPNG
            updateButtonState(buttonPNG, isPNG)
        }

        buttonSVG.setOnClickListener {
            isSVG = !isSVG
            updateButtonState(buttonSVG, isSVG)
        }

        buttonPSD.setOnClickListener {
            isPSD = !isPSD
            updateButtonState(buttonPSD, isPSD)
        }

        buttonPDF.setOnClickListener {
            isPDF = !isPDF
            updateButtonState(buttonPDF, isPDF)
        }

        buttonVetores.setOnClickListener {
            isVetores = !isVetores
            updateButtonState(buttonVetores, isVetores)
        }

        buttonFotos.setOnClickListener {
            isFotos = !isFotos
            updateButtonState(buttonFotos, isFotos)
        }

        buttonIA.setOnClickListener {
            isIA = !isIA
            updateButtonState(buttonIA, isIA)
        }

        buttonIcones.setOnClickListener {
            isIcones = !isIcones
            updateButtonState(buttonIcones, isIcones)
        }

        buttonMotion.setOnClickListener {
            isMotions = !isMotions
            updateButtonState(buttonMotion, isMotions)
        }

        buttonTextura.setOnClickListener {
            isTextura = !isTextura
            updateButtonState(buttonTextura, isTextura)
        }

        buttonMockups.setOnClickListener {
            isMockups = !isMockups
            updateButtonState(buttonMockups, isMockups)
        }

        button3D.setOnClickListener {
            is3D = !is3D
            updateButtonState(button3D, is3D)
        }

        buttonVermelho.setOnClickListener{
            isVermelho = !isVermelho
            updateCores(buttonVermelho, isVermelho)
        }

        buttonAzul.setOnClickListener{
            isAzul = !isAzul
            updateCores(buttonAzul, isAzul)
        }

        buttonVerde.setOnClickListener{
            isVerde = !isVerde
            updateCores(buttonVerde, isVerde)
        }

        buttonAmarelo.setOnClickListener{
            isAmarelo = !isAmarelo
            updateCores(buttonAmarelo, isAmarelo)
        }

        buttonRoxoo.setOnClickListener{
            isRoxo = !isRoxo
            updateCores(buttonRoxoo, isRoxo)
        }

        buttonRosa.setOnClickListener{
            isRosa = !isRosa
            updateCores(buttonRosa, isRosa)
        }

        buttonLaranja.setOnClickListener{
            isLaranja = !isLaranja
            updateCores(buttonLaranja, isLaranja)
        }

        buttonMarrom.setOnClickListener{
            isMarrom = !isMarrom
            updateCores(buttonMarrom, isMarrom)
        }

        buttonCinza.setOnClickListener{
            isCinza = !isCinza
            updateCores(buttonCinza, isCinza)
        }

        buttonPreto.setOnClickListener{
            isPreto = !isPreto
            updateCores(buttonPreto, isPreto)
        }

        buttonBranco.setOnClickListener{
            isBranco = !isBranco
            updateCores(buttonBranco, isBranco)
        }

        searchButton.setOnClickListener {
            val query = searchInput.text.toString().trim()
            searchImages(query, getSelectedFormats())
        }

        buttonHome.setOnClickListener{
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        buttonPerfil.setOnClickListener{
            val intent = Intent(this, PerfilActivity::class.java)
            startActivity(intent)
        }

        buttonMeusProdutos.setOnClickListener{
            val intent = Intent(this, DownloadActivity::class.java)
            startActivity(intent)
        }

        buttonMeusExplorar.setOnClickListener{
            val intent = Intent(this, ExplorarActivity::class.java)
            startActivity(intent)
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
        val formats = mutableListOf<String>()
        if (isJPG) formats.add("JPG")
        if (isPNG) formats.add("PNG")
        if (isSVG) formats.add("SVG")
        if (isPSD) formats.add("PSD")
        if (isPDF) formats.add("PDF")
        if (isVetores) formats.add("Vetores")
        if (isFotos) formats.add("Fotos")
        if (isIA) formats.add("IA")
        if (isIcones) formats.add("Ícones")
        if (isMotions) formats.add("Motions")
        if (isMockups) formats.add("Mockups")
        if (is3D) formats.add("3D")
        if (isTextura) formats.add("Textura")

        return formats
    }

    private fun searchImages(tag: String, formats: List<String>) {
        val call = RetrofitInstance.api.searchImages(tag, isPremium, isGratis, formats)
        call.enqueue(object : Callback<List<Image>> {
            override fun onResponse(call: Call<List<Image>>, response: Response<List<Image>>) {
                if (response.isSuccessful && response.body() != null) {
                    val images = response.body()!!
                    if (images.isEmpty()) {
                        aviso.text = "Imagem não encontrada"
                        imageAdapter.clearImages()
                    } else {
                        aviso.visibility = View.GONE
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

    private fun carregarImagensPorCategoria(categoria: String) {
        RetrofitInstance.api.getImagensPorCategoria(categoria).enqueue(object : Callback<List<Image>> {
            override fun onResponse(call: Call<List<Image>>, response: Response<List<Image>>) {
                if (response.isSuccessful && response.body() != null) {
                    val images = response.body()!!
                    if (images.isEmpty()) {
                        aviso.text = "Imagem não encontrada"
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
                Toast.makeText(this@ExplorarActivity, "Erro: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
