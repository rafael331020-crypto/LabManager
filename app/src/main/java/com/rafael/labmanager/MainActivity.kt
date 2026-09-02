package com.rafael.labmanager

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var root: LinearLayout
    private val prefs by lazy { getSharedPreferences("labmanager", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showDashboard()
    }

    private fun base(title: String): LinearLayout {
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(28, 28, 28, 28)
        }
        root.addView(TextView(this).apply {
            text = title
            textSize = 28f
            setPadding(0, 0, 0, 22)
        })
        return root
    }

    private fun button(label: String, action: () -> Unit): Button = Button(this).apply {
        text = label
        setOnClickListener { action() }
        root.addView(this, LinearLayout.LayoutParams(-1, -2).apply { setMargins(0, 6, 0, 6) })
    }

    private fun info(text: String) {
        root.addView(TextView(this).apply {
            this.text = text
            textSize = 16f
            setPadding(0, 12, 0, 12)
        })
    }

    private fun showDashboard() {
        base("LabManager")
        info("Gestão laboratorial • versão 1.1")
        button("👤 Pacientes") { showPatients() }
        button("🧪 Solicitações de exames") { showRequests() }
        button("🔬 Amostras") { showSamples() }
        button("📦 Estoque e validade") { showInventory() }
        button("📊 Resultados e relatórios") { showReports() }
        info("Os dados desta versão são armazenados localmente no aparelho.")
        setContentView(ScrollView(this).apply { addView(root) })
    }

    private fun showPatients() {
        base("Pacientes")
        button("+ Cadastrar paciente") { addRecord("patients", "Novo paciente") { showPatients() } }
        val data = prefs.getStringSet("patients", emptySet())!!.toList()
        if (data.isEmpty()) info("Nenhum paciente cadastrado.")
        data.forEachIndexed { i, value -> info("${i + 1}. $value") }
        button("← Voltar") { showDashboard() }
        setContentView(ScrollView(this).apply { addView(root) })
    }

    private fun showRequests() {
        base("Solicitações de exames")
        button("+ Nova solicitação") {
            addRecord("requests", "Solicitação de exame") { showRequests() }
        }
        val data = prefs.getStringSet("requests", emptySet())!!.toList()
        if (data.isEmpty()) info("Nenhuma solicitação cadastrada.")
        data.forEachIndexed { i, value -> info("${i + 1}. $value") }
        button("← Voltar") { showDashboard() }
        setContentView(ScrollView(this).apply { addView(root) })
    }

    private fun showSamples() {
        base("Amostras")
        info("Fluxo: Coletada → Recebida → Em processamento → Resultado → Liberada")
        button("+ Registrar amostra") { addRecord("samples", "Amostra") { showSamples() } }
        val data = prefs.getStringSet("samples", emptySet())!!.toList()
        if (data.isEmpty()) info("Nenhuma amostra registrada.")
        data.forEachIndexed { i, value ->
            info("${i + 1}. $value")
            button("Avançar etapa da amostra ${i + 1}") { advanceSample(value) }
        }
        button("← Voltar") { showDashboard() }
        setContentView(ScrollView(this).apply { addView(root) })
    }

    private fun advanceSample(value: String) {
        val stages = listOf("Coletada", "Recebida", "Em processamento", "Resultado", "Liberada")
        val current = stages.indexOfFirst { value.endsWith("|$it") }.let { if (it < 0) 0 else it }
        val next = if (current < stages.lastIndex) current + 1 else current
        val set = prefs.getStringSet("samples", emptySet())!!.toMutableSet()
        set.remove(value)
        val baseName = value.substringBefore("|")
        set.add("$baseName|${stages[next]}")
        prefs.edit().putStringSet("samples", set).apply()
        showSamples()
    }

    private fun showInventory() {
        base("Estoque e validade")
        button("+ Cadastrar item") { addRecord("inventory", "Item de estoque") { showInventory() } }
        val data = prefs.getStringSet("inventory", emptySet())!!.toList()
        if (data.isEmpty()) info("Nenhum item no estoque.")
        data.forEachIndexed { i, value -> info("${i + 1}. $value") }
        info("Dica: registre nome, lote, validade e quantidade para controle interno.")
        button("← Voltar") { showDashboard() }
        setContentView(ScrollView(this).apply { addView(root) })
    }

    private fun showReports() {
        base("Resultados e relatórios")
        val patients = prefs.getStringSet("patients", emptySet())!!.size
        val requests = prefs.getStringSet("requests", emptySet())!!.size
        val samples = prefs.getStringSet("samples", emptySet())!!.size
        val inventory = prefs.getStringSet("inventory", emptySet())!!.size
        info("Resumo do laboratório")
        info("Pacientes: $patients\nSolicitações: $requests\nAmostras: $samples\nItens de estoque: $inventory")
        button("← Voltar") { showDashboard() }
        setContentView(ScrollView(this).apply { addView(root) })
    }

    private fun addRecord(key: String, title: String, afterSave: () -> Unit) {
        val input = EditText(this).apply {
            hint = when (key) {
                "patients" -> "Nome completo / CPF (sem dados sensíveis desnecessários)"
                "requests" -> "Paciente + exame solicitado"
                "samples" -> "Identificação da amostra"
                else -> "Nome + lote + validade + quantidade"
            }
            minLines = 2
            gravity = Gravity.TOP
        }
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(input)
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Salvar") { _, _ ->
                val value = input.text.toString().trim()
                if (value.isNotEmpty()) {
                    val set = prefs.getStringSet(key, emptySet())!!.toMutableSet()
                    val finalValue = if (key == "samples") "$value|Coletada" else value
                    set.add(finalValue)
                    prefs.edit().putStringSet(key, set).apply()
                }
                afterSave()
            }.show()
    }
}
