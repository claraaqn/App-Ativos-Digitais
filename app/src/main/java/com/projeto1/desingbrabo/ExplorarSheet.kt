package com.projeto1.desingbrabo

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.projeto1.desingbrabo.api.RetrofitInstance
import com.projeto1.desingbrabo.model.Image
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExplorarSheet(context: Context, private val recyclerView: RecyclerView, private val idColaborador: Int, private val searchInput: EditText) {

    private val view: View = LayoutInflater.from(context).inflate(R.layout.barra_filtros, null)
    private val spinnerTags: Spinner = view.findViewById(R.id.spinner_tags)
    private val licenseEstado = mutableMapOf<Button, Boolean>()
    private val formatEstado = mutableMapOf<Button, Boolean>()
    private val coresEstado = mutableMapOf<ImageButton, Boolean>()

    init {
        setupButtons()
        setupSpinner()
    }

    fun getView(): View {
        return view
    }

    private fun setupSpinner() {
        val tagsArray = arrayOf("Todas", "Natureza", "Cidades", "Pessoas", "Animais")
        val adapter = ArrayAdapter(view.context, android.R.layout.simple_spinner_item, tagsArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTags.adapter = adapter

        spinnerTags.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                searchImages()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun searchImages() {
        val tag = searchInput.text.toString().trim()
        val formats = getSelectedFormats()
        val colors = getSelectedColor()

        RetrofitInstance.api.searchImages(
            tag = tag,
            isPremium = licenseEstado[view.findViewById(R.id.button_premium)] == true,
            isGratis = licenseEstado[view.findViewById(R.id.button_gratis)] == true,
            formats = formats,
            color = colors,
            userId = idColaborador
        ).enqueue(object : Callback<List<Image>> {
            override fun onResponse(call: Call<List<Image>>, response: Response<List<Image>>) {
                response.body()?.let { images ->
                    (recyclerView.adapter as? ImageAdapter)?.updateImages(images)
                }
            }

            override fun onFailure(call: Call<List<Image>>, t: Throwable) {
                Toast.makeText(view.context, "Erro ao buscar imagens", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupButtons() {
        val licenseButtons = mapOf(
            view.findViewById<Button>(R.id.button_premium) to "premium",
            view.findViewById<Button>(R.id.button_gratis) to "gratis"
        )

        val formatButtons = mapOf(
            view.findViewById<Button>(R.id.button_jpg) to "JPG",
            view.findViewById<Button>(R.id.button_png) to "PNG",
            view.findViewById<Button>(R.id.button_svg) to "SVG"
        )

        licenseButtons.forEach { (button, license) ->
            button.tag = license
            button.setOnClickListener {
                val isSelected = licenseEstado[button] ?: false
                licenseEstado[button] = !isSelected
                updateButtonState(button, !isSelected)
                searchImages()
            }
        }

        formatButtons.forEach { (button, format) ->
            button.tag = format
            button.setOnClickListener {
                val isSelected = formatEstado[button] ?: false
                formatEstado[button] = !isSelected
                updateButtonState(button, !isSelected)
                searchImages()
            }
        }

        val colorButtons = mapOf(
            view.findViewById<ImageButton>(R.id.button_vermelho) to "vermelho",
            view.findViewById<ImageButton>(R.id.button_azul) to "azul",
            view.findViewById<ImageButton>(R.id.button_verde) to "verde"
        )

        colorButtons.forEach { (button, color) ->
            button.tag = color
            button.setOnClickListener {
                val isSelected = coresEstado[button] ?: false
                coresEstado[button] = !isSelected
                updateCores(button, !isSelected)
                searchImages()
            }
        }

        view.findViewById<Button>(R.id.search_button).setOnClickListener {
            searchImages()
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
        } else {
            button.setTextColor(Color.parseColor("#010715"))
            button.setBackgroundResource(R.drawable.bg_filtros)
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