package com.projeto1.desingbrabo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class DenuciarSheet : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.barra_denucia, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonReportSpam = view.findViewById<Button>(R.id.buttonReportSpam)
        val buttonReportInappropriate = view.findViewById<Button>(R.id.buttonReportInappropriate)

        buttonReportSpam.setOnClickListener {
            Toast.makeText(context, "Imagem reportada como Spam", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        buttonReportInappropriate.setOnClickListener {
            Toast.makeText(context, "Imagem reportada como Conteúdo Inapropriado", Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }
}