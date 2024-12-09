package com.projeto1.desingbrabo

import android.os.Bundle
import android.widget.Button
import android.widget.ScrollView
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity

class TermosCondicoesActivity : AppCompatActivity() {
    private lateinit var backButton: Button
    private lateinit var termosSwitch: Switch
    private lateinit var scrollViewTermos: ScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tela_termos_condicoes)

        // Inicializando as views
        backButton = findViewById(R.id.button_voltar)
        termosSwitch = findViewById(R.id.custom_switch)
        scrollViewTermos = findViewById(R.id.scrollview_termos)


        backButton.setOnClickListener {
            finish()
        }

        termosSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // O usuário aceitou os termos, então podemos habilitar a confirmação
            } else {
                // discutir o que acontece se desmarcar os termos e condições
                termosSwitch.thumbDrawable = getDrawable(R.drawable.track)
                termosSwitch.trackDrawable = getDrawable(R.drawable.fundo_switch_desativadp)
            }
        }
    }
}