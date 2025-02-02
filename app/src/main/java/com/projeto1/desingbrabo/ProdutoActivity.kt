package com.projeto1.desingbrabo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.projeto1.desingbrabo.api.ApiService
import com.projeto1.desingbrabo.api.RetrofitInstance
import com.projeto1.desingbrabo.model.Produto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProdutoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela_produto)

        val imageId = intent.getIntExtra("image_id", -1)
        if (imageId == -1) {
            Toast.makeText(this, "Produto não encontrado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val colorsRecyclerView: RecyclerView = findViewById(R.id.colorsRecyclerView)
        colorsRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val buttonVoltar: Button = findViewById(R.id.button_voltar)
        val buttonHome: Button = findViewById(R.id.button_home)
        val buttonMeusProdutos: Button = findViewById(R.id.button_meus_produtos)
        val buttonPerfil: Button = findViewById(R.id.button_perfil)

        val buttonCurtir: ImageButton = findViewById(R.id.button_curtida)
        val buttonSeguir: Button = findViewById(R.id.button_seguir)

        val imagem: ImageView = findViewById(R.id.produto1)
        val donoImagem: TextView = findViewById(R.id.nome_proprietario_imagem)
        val nomeProduto: TextView = findViewById(R.id.nome_produto)
        val preco: TextView = findViewById(R.id.valor_preco)
        val formatos: TextView = findViewById(R.id.valor_formato)
        val tamanho: TextView = findViewById(R.id.valor_tamanho)
        val data: TextView = findViewById(R.id.valor_data)

        RetrofitInstance.api.getProduto(imageId).enqueue(object : Callback<Produto> {
            override fun onResponse(call: Call<Produto>, response: Response<Produto>) {
                if (response.isSuccessful) {
                    val produto = response.body()
                    if (produto != null) {
                        nomeProduto.text = produto.nome ?: "Sem Nome"
                        donoImagem.text = produto.dono ?: "Desconhecido"
                        preco.text = "R$ ${produto.preco ?: "0,00"}"
                        formatos.text = produto.formatos ?: "N/A"
                        data.text = produto.dataPublicacao ?: "N/A"
                        tamanho.text = "${produto.tamanho ?: "0"} MB"

                        Glide.with(this@ProdutoActivity)
                            .load(produto.url)
                            .placeholder(R.drawable.placeholder_image)
                            .error(R.drawable.produto4)
                            .into(imagem)

                        val cores = produto.cores ?: emptyList()
                        colorsRecyclerView.adapter = ColorsAdapter(cores, this@ProdutoActivity)
                    } else {
                        Toast.makeText(this@ProdutoActivity, "Produto não encontrado", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@ProdutoActivity, "Erro ao carregar produto", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Produto>, t: Throwable) {
                Toast.makeText(this@ProdutoActivity, "Erro: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })

        buttonVoltar.setOnClickListener{
            finish()
        }
        var isLiked = false

        buttonCurtir.setOnClickListener {
            if (isLiked) {
                buttonCurtir.setImageResource(R.drawable.nolike_icon)
            } else {
                buttonCurtir.setImageResource(R.drawable.like_icon)
            }
            isLiked = !isLiked
        }

        var isSeguindo = false
        buttonSeguir.setOnClickListener {
            if (isSeguindo) {
                buttonSeguir.setText("Seguir")
                buttonSeguir.setBackgroundResource(R.drawable.bg_button_seguir)
                buttonSeguir.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_plus, 0)
            } else {
                buttonSeguir.setText("Seguindo")
                buttonSeguir.setBackgroundResource(R.drawable.bg_button_seguindo)
                buttonSeguir.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check, 0)
            }
            isSeguindo = !isSeguindo
        }

        buttonHome.setOnClickListener{
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
        buttonMeusProdutos.setOnClickListener{
            val intent = Intent(this, DownloadActivity::class.java)
            startActivity(intent)
        }
        buttonPerfil.setOnClickListener{
            val intent = Intent(this, PerfilActivity::class.java)
            startActivity(intent)
        }
    }
}
