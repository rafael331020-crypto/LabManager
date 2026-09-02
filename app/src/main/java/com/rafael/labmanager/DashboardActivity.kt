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
    private val navy = Color.rgb(17, 43, 70)
    private val blue = Color.rgb(25, 111, 185)
    private val orange = Color.rgb(239, 125, 45)
    private val bg = Color.rgb(246, 248, 251)
    private val text = Color.rgb(32, 43, 55)
    private val muted = Color.rgb(102, 116, 132)

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
    private fun tv(s: String, size: Float, color: Int, bold: Boolean = false) = TextView(this).apply {
        text = s; textSize = size; setTextColor(color)
        if (bold) setTypeface(null, Typeface.BOLD)
    }

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        window.statusBarColor = navy
        window.navigationBarColor = bg
        dashboard()
    }

    private fun dashboard() {
        val scroll = ScrollView(this).apply { setBackgroundColor(bg) }
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(16), dp(18), dp(30))
        }
        scroll.addView(root)

        val header = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        val brand = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; layoutParams = LinearLayout.LayoutParams(0, -2, 1f) }
        brand.addView(tv("LabManager", 28f, navy, true))
        brand.addView(tv("LIS • Gestão laboratorial", 13f, muted))
        header.addView(brand)
        val online = tv("● ONLINE", 12f, Color.WHITE, true).apply {
            setPadding(dp(11), dp(7), dp(11), dp(7)); setBackgroundColor(Color.rgb(41, 143, 91))
        }
        header.addView(online)
        root.addView(header)

        val hero = MaterialCardView(this).apply {
            radius = dp(20).toFloat(); cardElevation = dp(4).toFloat(); setCardBackgroundColor(navy)
            setContentPadding(dp(21), dp(20), dp(21), dp(20))
        }
        val hb = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        hb.addView(tv("Central de operações", 23f, Color.WHITE, true))
        hb.addView(tv("Acompanhe o fluxo do laboratório do atendimento à liberação do resultado.", 14f, Color.WHITE).apply { setPadding(0, dp(7), 0, 0) })
        val open = MaterialButton(this).apply {
            text = "Abrir LIS"; isAllCaps = false; textSize = 15f; setTextColor(navy)
            backgroundTintList = android.content.res.ColorStateList.valueOf(Color.WHITE)
            cornerRadius = dp(12); setOnClickListener { startActivity(Intent(this@DashboardActivity, MainActivity::class.java)) }
        }
        hb.addView(open, LinearLayout.LayoutParams(-1, dp(50)).apply { setMargins(0, dp(17), 0, 0) })
        hero.addView(hb)
        root.addView(hero, LinearLayout.LayoutParams(-1, -2).apply { setMargins(0, dp(17), 0, dp(16)) })

        root.addView(tv("Resumo operacional", 19f, navy, true).apply { setPadding(dp(2), dp(4), 0, dp(8)) })
        val grid = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        rowStats(grid, "ATENDIMENTO", "Pacientes", "Cadastro e histórico", blue, "EXAMES", "Solicitações", "Fluxo e resultados", orange)
        rowStats(grid, "AMOSTRAS", "Rastreabilidade", "Coleta • triagem • processamento", Color.rgb(71, 119, 170), "ESTOQUE", "Materiais", "Lotes • validade • consumo", Color.rgb(86, 137, 99))
        root.addView(grid)

        root.addView(tv("Módulos do LIS", 19f, navy, true).apply { setPadding(dp(2), dp(20), 0, dp(8)) })
        val modules = listOf(
            Triple("01", "Atendimento e pacientes", "Cadastro, busca, histórico e atendimento"),
            Triple("02", "Requisições e exames", "Solicitação, materiais, status e resultados"),
            Triple("03", "Coleta e triagem", "Amostras, rastreabilidade e recoleta"),
            Triple("04", "Área técnica", "Processamento, conferência e liberação"),
            Triple("05", "Estoque e compras", "Reagentes, materiais, lotes e validade"),
            Triple("06", "Qualidade", "Não conformidades, documentos e indicadores"),
            Triple("07", "Financeiro e faturamento", "Contas, convênios e faturamento"),
            Triple("08", "BI e relatórios", "Produção, pendências e indicadores")
        )
        modules.forEach { module(root, it.first, it.second, it.third) }

        val footer = tv("LabManager 1.0 • arquitetura preparada para expansão", 12f, muted).apply {
            gravity = Gravity.CENTER; setPadding(0, dp(20), 0, 0)
        }
        root.addView(footer)
        setContentView(scroll)
    }

    private fun rowStats(parent: LinearLayout, k1: String, t1: String, d1: String, c1: Int, k2: String, t2: String, d2: String, c2: Int) {
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        stat(row, k1, t1, d1, c1, true); stat(row, k2, t2, d2, c2, false)
        parent.addView(row)
    }

    private fun stat(parent: LinearLayout, kicker: String, title: String, desc: String, accent: Int, left: Boolean) {
        val card = MaterialCardView(this).apply {
            radius = dp(15).toFloat(); cardElevation = dp(1).toFloat(); setCardBackgroundColor(Color.WHITE)
            setContentPadding(dp(14), dp(14), dp(14), dp(14))
        }
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        box.addView(tv(kicker, 10f, accent, true))
        box.addView(tv(title, 16f, text, true).apply { setPadding(0, dp(4), 0, 0) })
        box.addView(tv(desc, 12f, muted).apply { setPadding(0, dp(3), 0, 0) })
        card.addView(box)
        parent.addView(card, LinearLayout.LayoutParams(0, dp(105), 1f).apply { setMargins(if (left) 0 else dp(5), dp(4), if (left) dp(5) else 0, dp(4)) })
    }

    private fun module(parent: LinearLayout, number: String, title: String, desc: String) {
        val card = MaterialCardView(this).apply {
            radius = dp(16).toFloat(); cardElevation = dp(2).toFloat(); setCardBackgroundColor(Color.WHITE)
            isClickable = true; setOnClickListener { startActivity(Intent(this@DashboardActivity, MainActivity::class.java)) }
            setContentPadding(dp(15), dp(13), dp(15), dp(13))
        }
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        row.addView(tv(number, 12f, orange, true).apply { setPadding(dp(1), 0, dp(13), 0) })
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; layoutParams = LinearLayout.LayoutParams(0, -2, 1f) }
        box.addView(tv(title, 15f, text, true))
        box.addView(tv(desc, 12.5f, muted).apply { setPadding(0, dp(3), 0, 0) })
        row.addView(box)
        row.addView(tv("›", 26f, muted))
        card.addView(row)
        parent.addView(card, LinearLayout.LayoutParams(-1, -2).apply { setMargins(0, dp(4), 0, dp(4)) })
    }
}
