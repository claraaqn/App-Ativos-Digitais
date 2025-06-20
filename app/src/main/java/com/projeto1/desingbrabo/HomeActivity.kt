package com.projeto1.desingbrabo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.projeto1.desingbrabo.api.RetrofitInstance
import com.projeto1.desingbrabo.model.Image
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela_home)

        if (!isUserLoggedIn()){
            popLoginCadastro()
        }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)

        recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        RetrofitInstance.api.getImages().enqueue(object : Callback<List<Image>> {
            override fun onResponse(call: Call<List<Image>>, response: Response<List<Image>>) {
                val images = response.body()
                if (images != null) {
                    recyclerView.adapter = ImageAdapter(images, this@HomeActivity)
                }
            }
            override fun onFailure(call: Call<List<Image>>, t: Throwable) {
                Toast.makeText(this@HomeActivity, "${t.message}", Toast.LENGTH_LONG).show()
            }
        })

        findViewById<Button>(R.id.button_home).setOnClickListener {
            startActivity(Intent(this@HomeActivity, HomeActivity::class.java))
        }

        findViewById<Button>(R.id.button_perfil).setOnClickListener {
            startActivity(Intent(this@HomeActivity, PerfilActivity::class.java))
        }

        findViewById<Button>(R.id.button_meus_produtos).setOnClickListener {
            startActivity(Intent(this@HomeActivity, DownloadActivity::class.java))
        }

        findViewById<Button>(R.id.button_explorar).setOnClickListener {
            startActivity(Intent(this@HomeActivity, ExplorarActivity::class.java))
        }

        findViewById<Button>(R.id.button_carrinho).setOnClickListener {
            startActivity(Intent(this@HomeActivity, CarrinhoActivity::class.java))
        }
    }

    private fun isUserLoggedIn(): Boolean {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", -1)
        return userId != -1
    }

    private fun popLoginCadastro() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.popup_logincadastro, null)

        val dialogBuilder = android.app.AlertDialog.Builder(this)
            .setView(dialogView)

        val alertDialog = dialogBuilder.create()

        val fecharButton = dialogView.findViewById<Button>(R.id.fechar)
        val loginBtn = dialogView.findViewById<Button>(R.id.button_login)
        val cadastroBtn = dialogView.findViewById<Button>(R.id.button_cadastro)

        fecharButton.setOnClickListener {
            alertDialog.dismiss()
        }

        loginBtn.setOnClickListener {
            startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
        }

        cadastroBtn.setOnClickListener {
            startActivity(Intent(this@HomeActivity, CadastroActivity::class.java))
        }

        alertDialog.show()
    }
}
