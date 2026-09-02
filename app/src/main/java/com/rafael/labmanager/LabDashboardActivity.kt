package com.rafael.labmanager

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class LabDashboardActivity : AppCompatActivity() {
    private val navy = Color.rgb(18, 45, 72)
    private val blue = Color.rgb(25, 111, 182)
    private val blueLight = Color.rgb(232, 241, 249)
    private val green = Color.rgb(34, 137, 91)
    private val orange = Color.rgb(218, 125, 43)
    private val red = Color.rgb(194, 68, 68)
    private val bg = Color.rgb(244, 246, 249)
    private val border = Color.rgb(220, 225, 232)
    private val textDark = Color.rgb(38, 51, 65)
    private val muted = Color.rgb(105, 116, 128)

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    private fun tv(value: String, size: Float, color: Int, bold: Boolean = false) =
        TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            if (bold) setTypeface(null, Typeface.BOLD)
        }

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        build()
    }

    private fun build() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bg)
        }

        val scroll = ScrollView(this).apply {
            addView(root)
            setBackgroundColor(bg)
        }
        setContentView(scroll)

        buildTopBar(root)
        buildNavigation(root)
        buildContent(root)
    }

    private fun buildTopBar(root: LinearLayout) {
        val top = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(18), dp(13), dp(18), dp(13))
            setBackgroundColor(Color.WHITE)
        }

        val brand = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        brand.addView(tv("LABMANAGER", 16f, navy, true))
        brand.addView(tv("Sistema de Gestão Laboratorial", 11f, muted))
        top.addView(brand, LinearLayout.LayoutParams(0, -2, 1f))

        val online = tv("●  ONLINE", 11f, green, true).apply {
            setPadding(dp(10), dp(7), dp(10), dp(7))
            setBackgroundColor(Color.rgb(235, 247, 240))
        }
        top.addView(online)
        root.addView(top, LinearLayout.LayoutParams(-1, -2))

        val line = View(this).apply { setBackgroundColor(border) }
        root.addView(line, LinearLayout.LayoutParams(-1, dp(1)))
    }

    private fun buildNavigation(root: LinearLayout) {
        val navScroll = HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false
            setBackgroundColor(Color.WHITE)
        }
        val nav = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(dp(12), dp(8), dp(12), dp(8))
        }

        val items = listOf(
            "Painel" to null,
            "Atendimento" to { openLis() },
            "Amostras" to { openLis() },
            "Área Técnica" to { openLis() },
            "Financeiro" to { openLis() },
            "Estoque" to { openLis() },
            "Qualidade" to { openLis() },
            "Relatórios" to { openLis() }
        )

        items.forEachIndexed { index, pair ->
            val button = MaterialButton(this).apply {
                text = pair.first
                isAllCaps = false
                textSize = 12f
                cornerRadius = dp(8)
                minimumHeight = dp(38)
                minWidth = 0
                setPadding(dp(14), 0, dp(14), 0)
                if (index == 0) {
                    backgroundTintList = ColorStateList.valueOf(blue)
                    setTextColor(Color.WHITE)
                } else {
                    backgroundTintList = ColorStateList.valueOf(Color.WHITE)
                    setTextColor(textDark)
                    strokeWidth = dp(1)
                    strokeColor = ColorStateList.valueOf(border)
                }
                pair.second?.let { action -> setOnClickListener { action() } }
            }
            nav.addView(button, LinearLayout.LayoutParams(-2, dp(40)).apply {
                setMargins(if (index == 0) 0 else dp(6), 0, 0, 0)
            })
        }
        navScroll.addView(nav)
        root.addView(navScroll, LinearLayout.LayoutParams(-1, dp(56)))
    }

    private fun buildContent(root: LinearLayout) {
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(18), dp(16), dp(28))
        }
        root.addView(content)

        val titleRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val titleBox = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        titleBox.addView(tv("Painel de controle", 22f, navy, true))
        titleBox.addView(tv("Visão geral da operação laboratorial", 12f, muted).apply {
            setPadding(0, dp(3), 0, 0)
        })
        titleRow.addView(titleBox, LinearLayout.LayoutParams(0, -2, 1f))
        val date = tv("02/09/2026", 12f, muted, true)
        titleRow.addView(date)
        content.addView(titleRow, LinearLayout.LayoutParams(-1, -2).apply {
            setMargins(0, 0, 0, dp(14))
        })

        val kpiRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        kpiRow.addView(kpi("PACIENTES", "1.248", "+12%", blue), weightParams())
        kpiRow.addView(kpi("PEDIDOS", "386", "+8%", green), weightParams())
        kpiRow.addView(kpi("AMOSTRAS", "412", "24 pendentes", orange), weightParams())
        kpiRow.addView(kpi("RESULTADOS", "329", "18 aguardando", red), weightParams())
        content.addView(kpiRow, LinearLayout.LayoutParams(-1, -2).apply {
            setMargins(0, 0, 0, dp(14))
        })

        val grid = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        val firstRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        firstRow.addView(moduleCard("ATENDIMENTO", "Pacientes, convênios e pedidos", blue, "24 hoje") { openLis() }, weightParams())
        firstRow.addView(moduleCard("COLETA E TRIAGEM", "Rastreabilidade das amostras", orange, "17 pendentes") { openLis() }, weightParams())
        grid.addView(firstRow)

        val secondRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        secondRow.addView(moduleCard("ÁREA TÉCNICA", "Bancadas, resultados e liberação", green, "31 em processo") { openLis() }, weightParams())
        secondRow.addView(moduleCard("FINANCEIRO", "Receitas, despesas e faturamento", navy, "R$ 84.320") { openLis() }, weightParams())
        grid.addView(secondRow)
        content.addView(grid, LinearLayout.LayoutParams(-1, -2).apply {
            setMargins(0, 0, 0, dp(16))
        })

        content.addView(sectionTitle("Movimentação financeira"))
        val finance = card()
        val financeBox = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(14), dp(16), dp(14))
        }
        financeBox.addView(tv("Resumo do período", 13f, muted, true))
        financeBox.addView(financeLine("Faturamento", "R$ 126.480,00", green))
        financeBox.addView(financeLine("Contas a receber", "R$ 42.180,00", blue))
        financeBox.addView(financeLine("Contas a pagar", "R$ 18.640,00", red))
        financeBox.addView(financeLine("Saldo projetado", "R$ 107.840,00", navy))
        finance.addView(financeBox)
        content.addView(finance, LinearLayout.LayoutParams(-1, -2).apply {
            setMargins(0, 0, 0, dp(16))
        })

        content.addView(sectionTitle("Pendências operacionais"))
        val pending = card()
        val pendingBox = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(10), dp(16), dp(12))
        }
        pendingBox.addView(tableRow("Coletas aguardando triagem", "17", orange, true))
        pendingBox.addView(tableRow("Resultados aguardando conferência", "12", blue, false))
        pendingBox.addView(tableRow("Resultados críticos", "2", red, false))
        pendingBox.addView(tableRow("Itens abaixo do estoque mínimo", "8", orange, false))
        pendingBox.addView(tableRow("Documentos de qualidade pendentes", "3", navy, false))
        pending.addView(pendingBox)
        content.addView(pending, LinearLayout.LayoutParams(-1, -2).apply {
            setMargins(0, 0, 0, dp(16))
        })

        content.addView(sectionTitle("Acesso rápido"))
        val actions = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        actions.addView(actionButton("+ Novo atendimento") { openLis() }, weightParams())
        actions.addView(actionButton("+ Registrar coleta") { openLis() }, weightParams())
        actions.addView(actionButton("Ver resultados") { openLis() }, weightParams())
        content.addView(actions)

        content.addView(tv("LabManager LIS • protótipo local • arquitetura preparada para evolução", 10f, muted).apply {
            gravity = Gravity.CENTER
            setPadding(0, dp(22), 0, 0)
        })
    }

    private fun weightParams() = LinearLayout.LayoutParams(0, -2, 1f).apply {
        setMargins(dp(4), dp(4), dp(4), dp(4))
    }

    private fun kpi(title: String, value: String, detail: String, accent: Int): View {
        val c = card()
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(10), dp(11), dp(10), dp(11))
        }
        box.addView(tv(title, 9f, muted, true))
        box.addView(tv(value, 20f, navy, true).apply { setPadding(0, dp(5), 0, dp(1)) })
        box.addView(tv(detail, 9f, accent, true))
        c.addView(box)
        return c
    }

    private fun moduleCard(title: String, subtitle: String, accent: Int, detail: String, action: () -> Unit): View {
        val c = card().apply { isClickable = true; setOnClickListener { action() } }
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), dp(13), dp(14), dp(13))
        }
        box.addView(tv(title, 12f, accent, true))
        box.addView(tv(subtitle, 11f, textDark).apply { setPadding(0, dp(5), 0, dp(9)) })
        box.addView(tv(detail, 11f, muted, true))
        c.addView(box)
        return c
    }

    private fun financeLine(label: String, value: String, color: Int): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dp(8), 0, dp(8))
        }
        row.addView(tv(label, 12f, textDark), LinearLayout.LayoutParams(0, -2, 1f))
        row.addView(tv(value, 12f, color, true))
        return row
    }

    private fun tableRow(label: String, value: String, color: Int, header: Boolean): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dp(9), 0, dp(9))
        }
        row.addView(tv(label, if (header) 12f else 11f, textDark, header), LinearLayout.LayoutParams(0, -2, 1f))
        val badge = tv(value, 11f, color, true).apply {
            gravity = Gravity.CENTER
            setPadding(dp(9), dp(4), dp(9), dp(4))
            setBackgroundColor(Color.argb(22, Color.red(color), Color.green(color), Color.blue(color)))
        }
        row.addView(badge)
        return row
    }

    private fun actionButton(label: String, action: () -> Unit): View = MaterialButton(this).apply {
        text = label
        isAllCaps = false
        textSize = 11f
        cornerRadius = dp(8)
        minimumHeight = dp(44)
        backgroundTintList = ColorStateList.valueOf(Color.WHITE)
        setTextColor(blue)
        strokeWidth = dp(1)
        strokeColor = ColorStateList.valueOf(Color.rgb(180, 202, 220))
        setOnClickListener { action() }
    }

    private fun sectionTitle(value: String): View = tv(value, 15f, navy, true).apply {
        setPadding(dp(2), dp(3), 0, dp(7))
    }

    private fun card() = MaterialCardView(this).apply {
        radius = dp(9).toFloat()
        cardElevation = dp(1).toFloat()
        strokeWidth = dp(1)
        strokeColor = border
        setCardBackgroundColor(Color.WHITE)
    }

    private fun openLis() {
        startActivity(Intent(this, LisActivity::class.java))
    }
}
