package com.projeto1.desingbrabo

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.projeto1.desingbrabo.api.RetrofitInstance
import com.projeto1.desingbrabo.data.AppDatabase
import com.projeto1.desingbrabo.data.CartDao
import com.projeto1.desingbrabo.data.CartItem
import com.projeto1.desingbrabo.data.Product
import com.projeto1.desingbrabo.model.FavoriteRequest
import com.projeto1.desingbrabo.model.LikeResponse
import com.projeto1.desingbrabo.model.Produto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProdutoActivity : AppCompatActivity() {

    private lateinit var cartDao: CartDao
    private var loadedProduct: Product? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela_produto)

        val imageId = intent.getIntExtra("image_id", -1)
        if (imageId == -1) {
            Toast.makeText(this, "Produto não encontrado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val intentCo = Intent(this@ProdutoActivity, ColaboradorActivity::class.java)

        val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getInt("user_id", -1)

        val colorsRecyclerView: RecyclerView = findViewById(R.id.colorsRecyclerView)
        colorsRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val buttonVoltar: Button = findViewById(R.id.button_voltar)
        val buttonCurtir: ImageButton = findViewById(R.id.button_curtida)
        val buttonSeguir: Button = findViewById(R.id.button_seguir)
        val buttonAdicionarCarrinho: Button = findViewById(R.id.button_adicionar_carrinho)
        val buttonDenuncia: Button = findViewById(R.id.button_denuciar)

        val btnPropImage: Button = findViewById(R.id.nome_proprietario_imagem)

        val imagem: ImageView = findViewById(R.id.produto1)
        val donoImagem: TextView = findViewById(R.id.nome_proprietario_imagem)
        val uderPhoto: ImageButton = findViewById(R.id.foto_perfil)
        val nomeProduto: TextView = findViewById(R.id.nome_produto)
        val likes: TextView = findViewById(R.id.curtidas)
        val licenca: TextView = findViewById(R.id.tipo_licenca)
        val formatos: TextView = findViewById(R.id.valor_formato)
        val tamanho: TextView = findViewById(R.id.valor_tamanho)
        val categoria: TextView = findViewById(R.id.valor_categora)
        val data: TextView = findViewById(R.id.valor_data)

        val db = AppDatabase.getDatabase(this)
        cartDao = db.cartDao()

        RetrofitInstance.api.getProduto(imageId).enqueue(object : Callback<Produto> {
            override fun onResponse(call: Call<Produto>, response: Response<Produto>) {
                if (response.isSuccessful) {
                    val produto = response.body()
                    if (produto != null) {
                        nomeProduto.text = produto.nome ?: ""
                        donoImagem.text = produto.dono ?: ""
                        categoria.text = produto.categorias.joinToString() ?: ""
                        formatos.text = produto.formatos?.joinToString(", ") ?: ""
                        data.text = produto.dataPublicacao ?: ""
                        tamanho.text = "${produto.tamanho ?: ""} MB"
                        likes.text = "${produto.likes ?: ""} curtidas"

                        intentCo.putExtra("idColaborador", produto.idColaborador)

                        val licencaText = produto.licenca

                        if (licencaText == "premium") {
                            licenca.text = "Premium "
                            licenca.setTextColor(Color.parseColor("#7DF19E"))
                            licenca.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.selo, 0)
                        } else {
                            licenca.text = "Grátis "
                            licenca.setTextColor(Color.parseColor("#7DF19E"))
                            licenca.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.free, 0)

                        }

                        Glide.with(this@ProdutoActivity)
                            .load(produto.url)
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.icon_fechar)
                            .into(imagem)

                        Glide.with(this@ProdutoActivity)
                            .load(produto.fotoDono)
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.icon_fechar)
                            .into(uderPhoto)

                        colorsRecyclerView.adapter = ColorsAdapter(produto.cores ?: emptyList(), this@ProdutoActivity)

                        loadedProduct = Product(
                            id = produto.id ?: 0,
                            name = produto.nome ?: "Sem nome",
                            price = produto.preco?.let { "R$ $it" } ?: "R$ 0,00",
                            formatos = produto.formatos,
                            owner = produto.dono ?: "Desconhecido",
                            imageUrl = produto.url ?: ""
                        )

                        loadedProduct?.let { product ->
                            lifecycleScope.launch(Dispatchers.IO) {
                                try {
                                    db.productDao().insertProduct(product)
                                } catch (e: Exception) {
                                    Log.e("ProdutoActivity", "Erro ao salvar produto no banco: ${e.message}")
                                }
                            }
                        }

                    } else {
                        Toast.makeText(this@ProdutoActivity, "Produto não encontrado", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@ProdutoActivity, "Erro ao carregar produto", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Produto>, t: Throwable) {
                Toast.makeText(this@ProdutoActivity, "Erro: ${t.message}", Toast.LENGTH_LONG).show()
                Log.e("Produto", "Erro:  ${t.message}")
            }
        })

        btnPropImage.setOnClickListener {
            startActivity(intentCo)
        }

        buttonAdicionarCarrinho.setOnClickListener {
            if (loadedProduct != null) {
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        addToCart(loadedProduct!!)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@ProdutoActivity, "Produto adicionado ao carrinho!", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Log.e("ProdutoActivity", "Erro ao adicionar ao carrinho: ${e.message}")
                    }
                }
            } else {
                Log.e("ProdutoActivity", "Produto é null!")
                Toast.makeText(this@ProdutoActivity, "Produto não carregado", Toast.LENGTH_SHORT).show()
            }
        }

        var isLiked = false
        RetrofitInstance.api.checkIfLiked(imageId, userId).enqueue(object : Callback<LikeResponse> {
            override fun onResponse(call: Call<LikeResponse>, response: Response<LikeResponse>) {
                if (response.isSuccessful) {
                    isLiked = response.body()?.isLiked ?: false
                    if (isLiked) {
                        buttonCurtir.setImageResource(R.drawable.like_icon)
                    } else {
                        buttonCurtir.setImageResource(R.drawable.nolike_icon)
                    }
                } else {
                    Toast.makeText(this@ProdutoActivity, "Erro ao verificar curtida", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<LikeResponse>, t: Throwable) {
                Toast.makeText(this@ProdutoActivity, "Erro de conexão", Toast.LENGTH_SHORT).show()
            }
        })

        buttonCurtir.setOnClickListener {
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val request = FavoriteRequest(image_id = imageId, user_id = userId, timestamp = timestamp)
            if (!isLiked) {
                RetrofitInstance.api.addFavorite(request).enqueue(object : Callback<LikeResponse> {
                    override fun onResponse(call: Call<LikeResponse>, response: Response<LikeResponse>) {
                        if (response.isSuccessful) {
                            buttonCurtir.setImageResource(R.drawable.like_icon)
                            isLiked = true
                            val likesCount = response.body()?.likes ?: 0
                            updateLikesCount(likesCount)
                        } else {
                            Toast.makeText(this@ProdutoActivity, "Erro ao adicionar curtida", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<LikeResponse>, t: Throwable) {
                        Toast.makeText(this@ProdutoActivity, "Erro ao conectar ao servidor", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                RetrofitInstance.api.removeFavorite(request).enqueue(object : Callback<LikeResponse> {
                    override fun onResponse(call: Call<LikeResponse>, response: Response<LikeResponse>) {
                        if (response.isSuccessful) {
                            buttonCurtir.setImageResource(R.drawable.nolike_icon)
                            isLiked = true
                            val likesCount = response.body()?.likes ?: 0
                            updateLikesCount(likesCount)
                        } else {
                            Toast.makeText(this@ProdutoActivity, "Erro ao remover curtida", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<LikeResponse>, t: Throwable) {
                        Toast.makeText(this@ProdutoActivity, "Erro ao conectar ao servidor", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

        var isSeguindo = false
        buttonSeguir.setOnClickListener {
            isSeguindo = !isSeguindo
            buttonSeguir.text = if (isSeguindo) "Seguindo" else "Seguir"
            buttonSeguir.setBackgroundResource(if (isSeguindo) R.drawable.bg_button_seguindo else R.drawable.bg_button_seguir)
            buttonSeguir.setCompoundDrawablesWithIntrinsicBounds(0, 0, if (isSeguindo) R.drawable.ic_check else R.drawable.ic_plus, 0)
        }

        buttonVoltar.setOnClickListener { finish() }

        findViewById<Button>(R.id.button_home).setOnClickListener {
            startActivity(Intent(this@ProdutoActivity, HomeActivity::class.java))
        }
        findViewById<Button>(R.id.button_meus_produtos).setOnClickListener {
            startActivity(Intent(this@ProdutoActivity, DownloadActivity::class.java))
        }
        findViewById<Button>(R.id.button_carrinho).setOnClickListener {
            startActivity(Intent(this@ProdutoActivity, CarrinhoActivity::class.java))
        }
        findViewById<Button>(R.id.button_perfil).setOnClickListener {
            startActivity(Intent(this@ProdutoActivity, PerfilActivity::class.java))
        }

        buttonDenuncia.setOnClickListener {
            val bottomSheet = DenuciarSheet()
            bottomSheet.show(supportFragmentManager, bottomSheet.tag)
        }
    }

    private suspend fun addToCart(product: Product) {
        val existingCartItem = cartDao.getCartItemByProductId(product.id)
        if (existingCartItem != null) {
            cartDao.incrementQuantity(product.id)
        } else {
            val cartItem = CartItem(productId = product.id, quantity = 1)
            cartDao.insertCartItem(cartItem)
        }
        withContext(Dispatchers.Main) {
            Toast.makeText(this@ProdutoActivity, "Produto adicionado ao carrinho", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateLikesCount(count: Int) {
        val likesTextView: TextView = findViewById(R.id.curtidas)
        likesTextView.text = "$count curtidas"
    }
}
