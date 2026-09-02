package com.rafael.labmanager

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.flow.collectLatest

class EnterpriseDashboardActivity : AppCompatActivity() {
    private val navy = Color.rgb(20, 47, 78)
    private val blue = Color.rgb(31, 111, 184)
    private val green = Color.rgb(45, 135, 91)
    private val orange = Color.rgb(224, 125, 45)
    private val red = Color.rgb(190, 65, 65)
    private val bg = Color.rgb(244, 247, 250)
    private val line = Color.rgb(220, 226, 233)
    private val muted = Color.rgb(91, 106, 122)
    private val repository get() = (application as LabManagerApp).repository
    private lateinit var drawer: DrawerLayout
    private lateinit var content: LinearLayout
    private var patientsCount = 0
    private var ordersCount = 0
    private var samplesCount = 0
    private var resultsCount = 0
    private var stockLowCount = 0

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    private fun tv(text: String, size: Float, color: Int, bold: Boolean = false) = TextView(this).apply {
        this.text = text
        textSize = size
        setTextColor(color)
        if (bold) setTypeface(null, Typeface.BOLD)
    }

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        buildShell()
        observeData()
    }

    private fun buildShell() {
        drawer = DrawerLayout(this)
        drawer.setBackgroundColor(bg)
        val main = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setBackgroundColor(bg) }
        drawer.addView(main, DrawerLayout.LayoutParams(-1, -1))
        drawer.addView(buildDrawer(), DrawerLayout.LayoutParams(dp(292), -1).apply { gravity = Gravity.START })
        setContentView(drawer)

        val top = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(10), dp(8), dp(14), dp(8))
            setBackgroundColor(Color.WHITE)
            elevation = dp(2).toFloat()
        }
        val menu = MaterialButton(this).apply {
            text = "☰"
            textSize = 22f
            isAllCaps = false
            setTextColor(navy)
            backgroundTintList = android.content.res.ColorStateList.valueOf(Color.TRANSPARENT)
            setOnClickListener { drawer.openDrawer(GravityCompat.START) }
        }
        top.addView(menu, LinearLayout.LayoutParams(dp(52), dp(50)))
        val brand = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        brand.addView(tv("LABMANAGER", 12f, navy, true))
        brand.addView(tv("Gestão Laboratorial", 16f, navy, true).apply { setPadding(0, dp(2), 0, 0) })
        top.addView(brand, LinearLayout.LayoutParams(0, -2, 1f))
        top.addView(tv("● ONLINE", 11f, green, true))
        main.addView(top)

        val scroll = ScrollView(this).apply { setBackgroundColor(bg); isFillViewport = true }
        content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(18), dp(18), dp(28))
        }
        scroll.addView(content)
        main.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        renderHome()
    }

    private fun buildDrawer(): View {
        val panel = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(24), dp(18), dp(18))
            setBackgroundColor(Color.WHITE)
        }
        panel.addView(tv("LABMANAGER", 15f, blue, true))
        panel.addView(tv("CENTRAL DO LIS", 11f, muted, true).apply { setPadding(0, dp(4), 0, dp(22)) })
        drawerItem(panel, "⌂  Visão geral") { drawer.closeDrawer(GravityCompat.START); renderHome() }
        panel.addView(tv("OPERAÇÃO", 10f, muted, true).apply { setPadding(dp(4), dp(18), 0, dp(6)) })
        drawerItem(panel, "▣  Atendimento") { open(LisActivity::class.java) }
        drawerItem(panel, "◉  Amostras e triagem") { open(LisActivity::class.java) }
        drawerItem(panel, "▤  Área técnica") { open(LisActivity::class.java) }
        panel.addView(tv("GESTÃO", 10f, muted, true).apply { setPadding(dp(4), dp(18), 0, dp(6)) })
        drawerItem(panel, "▦  Estoque e materiais") { open(StockActivity::class.java) }
        drawerItem(panel, "✓  Qualidade") { open(LisActivity::class.java) }
        drawerItem(panel, "$  Financeiro") { open(FinanceActivity::class.java) }
        drawerItem(panel, "▥  Relatórios e auditoria") { open(LisActivity::class.java) }
        panel.addView(tv("ADMINISTRAÇÃO", 10f, muted, true).apply { setPadding(dp(4), dp(18), 0, dp(6)) })
        drawerItem(panel, "⚙  Configurações") { drawer.closeDrawer(GravityCompat.START) }
        val spacer = View(this)
        panel.addView(spacer, LinearLayout.LayoutParams(1, 0, 1f))
        panel.addView(tv("LIS local • versão 3.0", 11f, muted).apply { setPadding(dp(4), dp(12), 0, 0) })
        return panel
    }

    private fun drawerItem(parent: LinearLayout, label: String, action: () -> Unit) {
        val b = MaterialButton(this).apply {
            text = label
            isAllCaps = false
            textSize = 14f
            gravity = Gravity.START or Gravity.CENTER_VERTICAL
            setTextColor(navy)
            backgroundTintList = android.content.res.ColorStateList.valueOf(Color.TRANSPARENT)
            cornerRadius = dp(8)
            setPadding(dp(12), 0, dp(8), 0)
            setOnClickListener { action() }
        }
        parent.addView(b, LinearLayout.LayoutParams(-1, dp(46)))
    }

    private fun renderHome() {
        content.removeAllViews()
        content.addView(tv("Visão geral", 23f, navy, true))
        content.addView(tv("Painel operacional do laboratório", 13f, muted).apply { setPadding(0, dp(3), 0, dp(16)) })

        val toolbar = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        toolbar.addView(tv("Hoje  •  operação integrada", 12f, muted), LinearLayout.LayoutParams(0, -2, 1f))
        val refresh = MaterialButton(this).apply {
            text = "Atualizar"
            isAllCaps = false
            textSize = 12f
            setTextColor(blue)
            backgroundTintList = android.content.res.ColorStateList.valueOf(Color.WHITE)
            strokeWidth = dp(1)
            strokeColor = android.content.res.ColorStateList.valueOf(line)
            cornerRadius = dp(7)
            setOnClickListener { renderHome() }
        }
        toolbar.addView(refresh, LinearLayout.LayoutParams(dp(105), dp(40)))
        content.addView(toolbar, LinearLayout.LayoutParams(-1, dp(42)).apply { setMargins(0, 0, 0, dp(10)) })

        val metrics = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        metricRow(metrics, "PACIENTES", patientsCount.toString(), "Cadastros", blue, "REQUISIÇÕES", ordersCount.toString(), "Pedidos", orange)
        metricRow(metrics, "AMOSTRAS", samplesCount.toString(), "Rastreabilidade", green, "RESULTADOS", resultsCount.toString(), "Registros", navy)
        content.addView(metrics)

        sectionTitle("Acompanhamento operacional")
        val table = MaterialCardView(this).apply { radius = dp(10).toFloat(); cardElevation = dp(1).toFloat(); setCardBackgroundColor(Color.WHITE); setContentPadding(dp(14), dp(8), dp(14), dp(8)) }
        val rows = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        tableRow(rows, "ATENDIMENTO", "Pacientes e pedidos", "ATIVO", green)
        divider(rows)
        tableRow(rows, "COLETA / TRIAGEM", "Amostras rastreáveis", "OPERACIONAL", blue)
        divider(rows)
        tableRow(rows, "ÁREA TÉCNICA", "Resultados e conferência", "EM FLUXO", orange)
        divider(rows)
        tableRow(rows, "ESTOQUE", "Itens abaixo do mínimo", stockLowCount.toString(), if (stockLowCount > 0) red else green)
        divider(rows)
        tableRow(rows, "FINANCEIRO", "Contas e faturamento", "GESTÃO", navy)
        table.addView(rows)
        content.addView(table, LinearLayout.LayoutParams(-1, -2).apply { setMargins(0, 0, 0, dp(14)) })

        sectionTitle("Ações rápidas")
        quickAction("+ NOVO ATENDIMENTO", "Cadastrar paciente e iniciar requisição", blue) { open(LisActivity::class.java) }
        quickAction("+ REGISTRAR AMOSTRA", "Coleta, identificação e triagem", orange) { open(LisActivity::class.java) }
        quickAction("ÁREA TÉCNICA", "Digitação, conferência e liberação", green) { open(LisActivity::class.java) }
        quickAction("ESTOQUE", "Lotes, validade, entrada e saída", navy) { open(StockActivity::class.java) }
        content.addView(tv("Fluxo: Atendimento → Coleta → Triagem → Processamento → Resultado → Liberação", 11.5f, muted).apply { setPadding(0, dp(18), 0, 0) })
    }

    private fun metricRow(parent: LinearLayout, t1: String, v1: String, s1: String, c1: Int, t2: String, v2: String, s2: String, c2: Int) {
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        row.addView(metric(t1, v1, s1, c1), LinearLayout.LayoutParams(0, dp(104), 1f).apply { setMargins(0, 0, dp(5), dp(5)) })
        row.addView(metric(t2, v2, s2, c2), LinearLayout.LayoutParams(0, dp(104), 1f).apply { setMargins(dp(5), 0, 0, dp(5)) })
        parent.addView(row)
    }

    private fun metric(title: String, value: String, subtitle: String, accent: Int): View {
        val card = MaterialCardView(this).apply { radius = dp(9).toFloat(); cardElevation = dp(1).toFloat(); setCardBackgroundColor(Color.WHITE); setStrokeColor(line); strokeWidth = dp(1); setContentPadding(dp(14), dp(11), dp(14), dp(10)) }
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        box.addView(tv(title, 10.5f, muted, true))
        box.addView(tv(value, 26f, accent, true).apply { setPadding(0, dp(4), 0, 0) })
        box.addView(tv(subtitle, 11f, muted))
        card.addView(box)
        return card
    }

    private fun sectionTitle(title: String) { content.addView(tv(title, 17f, navy, true).apply { setPadding(dp(1), dp(12), 0, dp(8)) }) }

    private fun tableRow(parent: LinearLayout, a: String, b: String, status: String, color: Int) {
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL; setPadding(0, dp(9), 0, dp(9)) }
        val left = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        left.addView(tv(a, 11.5f, navy, true)); left.addView(tv(b, 11f, muted).apply { setPadding(0, dp(3), 0, 0) })
        row.addView(left, LinearLayout.LayoutParams(0, -2, 1f))
        row.addView(tv(status, 10.5f, color, true).apply { gravity = Gravity.END })
        parent.addView(row)
    }

    private fun divider(parent: LinearLayout) { parent.addView(View(this).apply { setBackgroundColor(line) }, LinearLayout.LayoutParams(-1, 1)) }

    private fun quickAction(title: String, subtitle: String, color: Int, action: () -> Unit) {
        val card = MaterialCardView(this).apply { radius = dp(9).toFloat(); cardElevation = dp(1).toFloat(); setCardBackgroundColor(Color.WHITE); setStrokeColor(line); strokeWidth = dp(1); isClickable = true; setOnClickListener { action() }; setContentPadding(dp(14), dp(11), dp(14), dp(11)) }
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        box.addView(tv(title, 12.5f, color, true)); box.addView(tv(subtitle, 11.5f, muted).apply { setPadding(0, dp(4), 0, 0) })
        card.addView(box)
        content.addView(card, LinearLayout.LayoutParams(-1, -2).apply { setMargins(0, dp(3), 0, dp(3)) })
    }

    private fun open(clazz: Class<*>) {
        drawer.closeDrawer(GravityCompat.START)
        startActivity(Intent(this, clazz))
    }

    private fun observeData() {
        lifecycleScope.launchWhenStarted {
            repository.patients().collectLatest { patientsCount = it.size; renderHome() }
        }
        lifecycleScope.launchWhenStarted {
            repository.orders().collectLatest { ordersCount = it.size; renderHome() }
        }
        lifecycleScope.launchWhenStarted {
            repository.samples().collectLatest { samplesCount = it.size; renderHome() }
        }
        lifecycleScope.launchWhenStarted {
            repository.results().collectLatest { resultsCount = it.size; renderHome() }
        }
        lifecycleScope.launchWhenStarted {
            repository.stock().collectLatest { list -> stockLowCount = list.count { it.quantity <= it.minimumStock }; renderHome() }
        }
    }
}
