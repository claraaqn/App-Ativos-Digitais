package com.projeto1.desingbrabo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

        val buttonVoltar: Button = findViewById(R.id.button_voltar)
        val buttonHome: Button = findViewById(R.id.button_home)
        val buttonMeusProdutos: Button = findViewById(R.id.button_meus_produtos)
        val buttonPerfil: Button = findViewById(R.id.button_perfil)


        val imagem: ImageView = findViewById(R.id.produto)
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
                        nomeProduto.text = produto.nome
                        donoImagem.text = produto.dono
                        preco.text = "R$ ${produto.preco}"
                        formatos.text = produto.formatos
                        data.text = produto.dataPublicacao
                        tamanho.text = "${produto.tamanho} MB"
                        Glide.with(this@ProdutoActivity)
                            .load(produto.url)
                            .placeholder(R.drawable.placeholder_image)
                            .error(R.drawable.produto4)
                            .into(imagem)
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
