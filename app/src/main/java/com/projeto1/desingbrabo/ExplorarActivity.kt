package com.projeto1.desingbrabo

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.projeto1.desingbrabo.api.RetrofitInstance
import com.projeto1.desingbrabo.model.Image
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException
import javax.net.ssl.SSLHandshakeException


class ExplorarActivity : AppCompatActivity() {

    private var filtrosVisiveis = true

    private lateinit var searchInput: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var aviso: TextView
    private lateinit var imageAdapter: ImageAdapter

    private val licenseEstado = mutableMapOf<Button, Boolean>()
    private val formatEstado  = mutableMapOf<Button, Boolean>()
    private val coresEstado = mutableMapOf<ImageButton, Boolean>()


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela_explorar)

        initViews()
        setupRecyclerView()
        setupFiltros()
        setupButtons()
    }

    private fun initViews() {
        searchInput = findViewById(R.id.search_input)
        recyclerView = findViewById(R.id.recyclerView)
        aviso = findViewById(R.id.aviso)
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
            findViewById<TextView>(R.id.formatos),
            findViewById<Button>(R.id.button_psd),
            findViewById<Button>(R.id.button_png),
            findViewById<Button>(R.id.button_jpg),
            findViewById<Button>(R.id.button_jpeg),
            findViewById<Button>(R.id.button_svg),
            findViewById<Button>(R.id.button_ai),
            findViewById<Button>(R.id.button_eps),
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
            findViewById<ImageButton>(R.id.button_branco),
            findViewById<TextView>(R.id.popularidade),
            findViewById<Button>(R.id.button_em_alta)
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

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupButtons() {
        // Botões de licença (premium e gratis)
        val licenseButtons = mapOf(
            findViewById<Button>(R.id.button_premium) to "premium",
            findViewById<Button>(R.id.button_gratis) to "free"
        )

        // Botões de formatos (JPG, PNG, etc.)
        val formatButtons = mapOf(
            findViewById<Button>(R.id.button_psd) to "PSD",
            findViewById<Button>(R.id.button_png) to "PNG",
            findViewById<Button>(R.id.button_jpg) to "JPG",
            findViewById<Button>(R.id.button_jpeg) to "JPEG",
            findViewById<Button>(R.id.button_svg) to "SVG",
            findViewById<Button>(R.id.button_ai) to "AI",
            findViewById<Button>(R.id.button_eps) to "EPS"

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

        // Configura os botões de cores
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

    @RequiresApi(Build.VERSION_CODES.M)
    private fun searchImages(tag: String, formats: List<String>, color: List<String>) {
        // Verificar conexão com internet primeiro
        if (!isNetworkAvailable()) {
            Toast.makeText(this@ExplorarActivity, "Sem conexão com a internet", Toast.LENGTH_LONG).show()
            aviso.text = "Sem conexão com a internet"
            aviso.visibility = View.VISIBLE
            return
        }

        // Mostrar loading
        aviso.text = "Carregando..."
        aviso.visibility = View.VISIBLE

        val call = RetrofitInstance.api.searchImages(
            tag = if (tag.isNotEmpty()) tag else null,
            isPremium = licenseEstado[findViewById(R.id.button_premium)] == true,
            isGratis = licenseEstado[findViewById(R.id.button_gratis)] == true,
            formats = if (formats.isNotEmpty()) formats else emptyList(),
            color = if (color.isNotEmpty()) color else emptyList(),
            userId = null
        )

        call.enqueue(object : Callback<List<Image>> {
            override fun onResponse(call: Call<List<Image>>, response: Response<List<Image>>) {
                when {
                    response.isSuccessful && response.body() != null -> {
                        val images = response.body()!!
                        if (images.isEmpty()) {
                            aviso.text = "Nenhuma imagem encontrada"
                            aviso.visibility = View.VISIBLE
                        } else {
                            aviso.visibility = View.GONE
                        }
                        imageAdapter.updateImages(images)
                    }
                    response.code() == 404 -> {
                        aviso.text = "Nenhum resultado encontrado"
                        aviso.visibility = View.VISIBLE
                    }
                    response.code() in 500..599 -> {
                        aviso.text = "Problema no servidor. Tente novamente mais tarde."
                        aviso.visibility = View.VISIBLE
                    }
                    else -> {
                        aviso.text = "Erro desconhecido: ${response.code()}"
                        aviso.visibility = View.VISIBLE
                        Toast.makeText(
                            this@ExplorarActivity,
                            "Erro na resposta do servidor",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<List<Image>>, t: Throwable) {
                aviso.text = when (t) {
                    is SocketTimeoutException -> "Tempo de conexão esgotado"
                    is ConnectException -> "Não foi possível conectar ao servidor"
                    is SSLHandshakeException -> "Problema de segurança na conexão"
                    else -> "Erro de conexão: ${t.localizedMessage}"
                }
                aviso.visibility = View.VISIBLE

                Toast.makeText(
                    this@ExplorarActivity,
                    "Falha na conexão: ${t.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()

                Log.e("API_ERROR", "Erro na chamada da API", t)
            }
        })
    }

    // Função para verificar conexão com internet
    @RequiresApi(Build.VERSION_CODES.M)
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities != null &&
                (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }
}
