package com.rafael.labmanager

import android.os.Bundle
import android.graphics.Typeface
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(28, 32, 28, 28)
        }
        fun text(value: String, size: Float, bold: Boolean = false): TextView = TextView(this).apply {
            text = value
            textSize = size
            if (bold) setTypeface(typeface, Typeface.BOLD)
            setPadding(0, 12, 0, 12)
        }
        root.addView(text("LabManager", 30f, true))
        root.addView(text("Gestão laboratorial", 18f))
        root.addView(text("Painel", 22f, true))
        root.addView(text("Pacientes", 18f))
        root.addView(text("Solicitações de exames", 18f))
        root.addView(text("Amostras", 18f))
        root.addView(text("Estoque e validade", 18f))
        root.addView(text("Resultados e relatórios", 18f))
        root.addView(text("Versão inicial • pronto para evoluir", 14f).apply {
            gravity = Gravity.CENTER
        })
        setContentView(root)
    }
}
