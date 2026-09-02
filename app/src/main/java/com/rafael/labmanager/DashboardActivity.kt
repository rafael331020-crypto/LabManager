package com.rafael.labmanager

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.google.android.material.button.MaterialButton

class DashboardActivity : AppCompatActivity() {
    private val navy = Color.rgb(14, 39, 67)
    private val blue = Color.rgb(28, 111, 184)
    private val bg = Color.rgb(244, 247, 250)
    private val text = Color.rgb(28, 38, 49)
    private val muted = Color.rgb(103, 116, 130)

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
    private fun tv(s: String, size: Float, color: Int, bold: Boolean = false) = TextView(this).apply {
        text = s; textSize = size; setTextColor(color); if (bold) setTypeface(null, Typeface.BOLD)
    }

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        window.statusBarColor = navy
        window.navigationBarColor = bg

        val scroll = ScrollView(this).apply { setBackgroundColor(bg) }
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(22), dp(20), dp(30))
        }
        scroll.addView(root)

        val top = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        val brand = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; layoutParams = LinearLayout.LayoutParams(0, -2, 1f) }
        brand.addView(tv("LabManager", 29f, navy, true))
        brand.addView(tv("Sistema de gestão laboratorial", 14f, muted))
        top.addView(brand)
        val badge = tv("ONLINE", 11f, Color.WHITE, true).apply {
            gravity = Gravity.CENTER; setPadding(dp(12), dp(7), dp(12), dp(7)); setBackgroundColor(blue)
        }
        top.addView(badge)
        root.addView(top)

        val hero = MaterialCardView(this).apply {
            radius = dp(22).toFloat(); cardElevation = dp(5).toFloat(); setCardBackgroundColor(navy)
            setContentPadding(dp(22), dp(24), dp(22), dp(24))
        }
        val hb = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        hb.addView(tv("Painel do laboratório", 24f, Color.WHITE, true))
        hb.addView(tv("Centralize pacientes, solicitações, amostras, estoque e atendimento em um só lugar.", 14f, Color.WHITE).apply { setPadding(0, dp(8), 0, 0) })
        val enter = MaterialButton(this).apply {
            text = "Abrir sistema"; isAllCaps = false; textSize = 15f; setTextColor(navy)
            backgroundTintList = android.content.res.ColorStateList.valueOf(Color.WHITE)
            cornerRadius = dp(13); setOnClickListener { startActivity(Intent(this@DashboardActivity, MainActivity::class.java)) }
        }
        hb.addView(enter, LinearLayout.LayoutParams(-1, dp(52)).apply { setMargins(0, dp(18), 0, 0) })
        hero.addView(hb)
        root.addView(hero, LinearLayout.LayoutParams(-1, -2).apply { setMargins(0, dp(20), 0, dp(18)) })

        root.addView(tv("Visão geral", 20f, navy, true).apply { setPadding(dp(2), dp(4), 0, dp(10)) })
        val stats = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        stat(stats, "01", "Pacientes", "Cadastro e histórico")
        stat(stats, "02", "Exames", "Solicitações e resultados")
        stat(stats, "03", "Amostras", "Rastreabilidade por etapa")
        stat(stats, "04", "Estoque", "Materiais e controle")
        root.addView(stats)

        root.addView(tv("Recursos", 20f, navy, true).apply { setPadding(dp(2), dp(22), 0, dp(8)) })
        val features = MaterialCardView(this).apply {
            radius = dp(16).toFloat(); cardElevation = dp(2).toFloat(); setCardBackgroundColor(Color.WHITE)
            setContentPadding(dp(18), dp(16), dp(18), dp(16))
        }
        val fb = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        listOf("Cadastro de pacientes", "Fluxo de exames", "Controle de amostras", "Gestão de estoque", "Agendamentos", "Consultas e relatórios").forEachIndexed { i, s ->
            val row = LinearLayout(this).apply { gravity = Gravity.CENTER_VERTICAL; setPadding(0, dp(8), 0, dp(8)) }
            row.addView(tv("${i + 1}", 13f, blue, true).apply { gravity = Gravity.CENTER; setPadding(dp(10), dp(5), dp(10), dp(5)) })
            row.addView(tv(s, 15f, text, true).apply { setPadding(dp(12), 0, 0, 0) })
            fb.addView(row)
        }
        features.addView(fb); root.addView(features)

        root.addView(tv("LabManager • versão 1.0", 12f, muted).apply { gravity = Gravity.CENTER; setPadding(0, dp(22), 0, 0) })
        setContentView(scroll)
    }

    private fun stat(parent: LinearLayout, number: String, title: String, desc: String) {
        val card = MaterialCardView(this).apply {
            radius = dp(15).toFloat(); cardElevation = dp(1).toFloat(); setCardBackgroundColor(Color.WHITE)
            setContentPadding(dp(16), dp(13), dp(16), dp(13))
        }
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        row.addView(tv(number, 13f, blue, true).apply { setPadding(0, 0, dp(16), 0) })
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        box.addView(tv(title, 16f, text, true))
        box.addView(tv(desc, 13f, muted).apply { setPadding(0, dp(3), 0, 0) })
        row.addView(box)
        card.addView(row)
        parent.addView(card, LinearLayout.LayoutParams(-1, -2).apply { setMargins(0, dp(5), 0, dp(5)) })
    }
}
