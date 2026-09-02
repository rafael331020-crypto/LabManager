package com.rafael.labmanager

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private val db by lazy { getSharedPreferences("labmanager_db", Context.MODE_PRIVATE) }
    private lateinit var root: LinearLayout
    private val navy = Color.rgb(20, 47, 78)
    private val blue = Color.rgb(31, 111, 184)
    private val bg = Color.rgb(246, 248, 251)
    private val darkText = Color.rgb(34, 43, 54)
    private val muted = Color.rgb(100, 112, 126)

    private fun data(k: String): MutableList<JSONObject> { val a = JSONArray(db.getString(k, "[]")); return MutableList(a.length()) { a.getJSONObject(it) } }
    private fun save(k: String, l: List<JSONObject>) { val a = JSONArray(); l.forEach { a.put(it) }; db.edit().putString(k, a.toString()).apply() }
    private fun id() = UUID.randomUUID().toString()
    private fun now() = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR")).format(Date())
    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    override fun onCreate(b: Bundle?) { super.onCreate(b); home() }

    private fun page(t: String) {
        root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(20), dp(18), dp(20), dp(28)); setBackgroundColor(bg) }
        val scroll = ScrollView(this).apply { setBackgroundColor(bg); addView(root) }
        val header = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(0, 0, 0, dp(18)) }
        val title = TextView(this).apply { text = t; textSize = 27f; setTextColor(navy); setTypeface(null, Typeface.BOLD) }
        header.addView(title)
        root.addView(header)
        setContentView(scroll)
    }

    private fun button(t: String, f: () -> Unit) {
        val b = MaterialButton(this).apply {
            text = t; textSize = 14f; isAllCaps = false; setTextColor(Color.WHITE); setOnClickListener { f() }
            setBackgroundColor(blue); cornerRadius = dp(12); setPadding(dp(16), dp(5), dp(16), dp(5))
            minimumHeight = dp(50)
        }
        root.addView(b, LinearLayout.LayoutParams(-1, dp(52)).apply { setMargins(0, dp(7), 0, dp(7)) })
    }

    private fun secondaryButton(t: String, f: () -> Unit) {
        val b = MaterialButton(this).apply { text = t; textSize = 14f; isAllCaps = false; setTextColor(navy); strokeWidth = dp(1); strokeColor = android.content.res.ColorStateList.valueOf(Color.rgb(205, 214, 224)); setOnClickListener { f() }; cornerRadius = dp(12); backgroundTintList = android.content.res.ColorStateList.valueOf(Color.WHITE) }
        root.addView(b, LinearLayout.LayoutParams(-1, dp(50)).apply { setMargins(0, dp(6), 0, dp(6)) })
    }

    private fun label(t: String) {
        val v = TextView(this).apply { text = t; textSize = 15f; setTextColor(darkText); setPadding(dp(4), dp(10), dp(4), dp(10)) }
        root.addView(v)
    }

    private fun infoCard(title: String, body: String) {
        val c = MaterialCardView(this).apply { radius = dp(14).toFloat(); cardElevation = dp(2).toFloat(); setCardBackgroundColor(Color.WHITE); setContentPadding(dp(16), dp(14), dp(16), dp(14)) }
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        box.addView(TextView(this).apply { text = title; textSize = 13f; setTextColor(muted); setTypeface(null, Typeface.BOLD) })
        box.addView(TextView(this).apply { text = body; textSize = 16f; setTextColor(darkText); setPadding(0, dp(5), 0, 0) })
        c.addView(box); root.addView(c, LinearLayout.LayoutParams(-1, -2).apply { setMargins(0, dp(6), 0, dp(6)) })
    }

    private fun section(t: String) { root.addView(TextView(this).apply { text = t; textSize = 18f; setTextColor(navy); setTypeface(null, Typeface.BOLD); setPadding(dp(2), dp(18), dp(2), dp(5)) }) }

    private fun field(h: String, v: String = ""): EditText {
        val til = TextInputLayout(this).apply { hint = h; boxBackgroundMode = TextInputLayout.BOX_BACKGROUND_OUTLINE; boxCornerRadiusTopStart = dp(10).toFloat(); boxCornerRadiusTopEnd = dp(10).toFloat(); boxCornerRadiusBottomStart = dp(10).toFloat(); boxCornerRadiusBottomEnd = dp(10).toFloat() }
        val e = TextInputEditText(this).apply { setText(v); textSize = 15f; minLines = if (h.contains("Observ", true)) 3 else 1 }
        til.addView(e); root.addView(til, LinearLayout.LayoutParams(-1, -2).apply { setMargins(0, dp(5), 0, dp(5)) }); return e
    }

    private fun home() {
        page("LabManager")
        val head = MaterialCardView(this).apply { radius = dp(18).toFloat(); cardElevation = dp(3).toFloat(); setCardBackgroundColor(navy); setContentPadding(dp(20), dp(18), dp(20), dp(18)) }
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        box.addView(TextView(this).apply { text = "Gestão Laboratorial"; textSize = 23f; setTextColor(Color.WHITE); setTypeface(null, Typeface.BOLD) })
        box.addView(TextView(this).apply { text = "Controle de pacientes, exames, amostras e estoque"; textSize = 14f; setTextColor(Color.rgb(220, 232, 245)); setPadding(0, dp(5), 0, 0) })
        head.addView(box); root.addView(head, LinearLayout.LayoutParams(-1, -2).apply { setMargins(0, 0, 0, dp(12)) })
        section("Visão geral")
        infoCard("PACIENTES", "${data("patients").size} cadastrados")
        infoCard("EXAMES", "${data("requests").size} solicitações")
        infoCard("AMOSTRAS", "${data("samples").size} registradas")
        infoCard("ESTOQUE", "${data("stock").size} itens")
        section("Módulos")
        button("👤  Pacientes") { patients() }
        button("🧾  Solicitações de exames") { requests() }
        button("🔬  Amostras") { samples() }
        button("📦  Estoque e validade") { stock() }
        button("📊  Resultados e relatórios") { reports() }
        button("📅  Agendamentos") { appointments() }
        button("🩺  Consultas") { consultations() }
        label("LabManager • versão 1.0")
    }

    private fun patients() { page("Pacientes"); button("＋  Novo cadastro") { patientForm(null) }; val l = data("patients"); if (l.isEmpty()) label("Nenhum paciente cadastrado."); l.forEach { p -> infoCard(p.optString("name"), "${p.optString("phone")}  •  ${p.optString("birth")}"); secondaryButton("Visualizar") { details(p) }; secondaryButton("Editar") { patientForm(p) }; secondaryButton("Excluir") { delete("patients", p.optString("id")) { patients() } } }; secondaryButton("←  Voltar") { home() } }
    private fun patientForm(old: JSONObject?) { page(if (old == null) "Novo paciente" else "Editar paciente"); section("Dados pessoais"); val n = field("Nome completo", old?.optString("name") ?: ""); val ph = field("Telefone", old?.optString("phone") ?: ""); val b = field("Data de nascimento", old?.optString("birth") ?: ""); val cpf = field("CPF (opcional)", old?.optString("cpf") ?: ""); section("Endereço e observações"); val addr = field("Endereço", old?.optString("address") ?: ""); val notes = field("Observações", old?.optString("notes") ?: ""); button("Salvar paciente") { if (n.text.isBlank() || ph.text.isBlank() || b.text.isBlank()) { toast("Preencha nome, telefone e nascimento"); return@button }; val p = old ?: JSONObject().put("id", id()); p.put("name", n.text.toString()).put("phone", ph.text.toString()).put("birth", b.text.toString()).put("cpf", cpf.text.toString()).put("address", addr.text.toString()).put("notes", notes.text.toString()); val l = data("patients"); l.removeAll { it.optString("id") == p.optString("id") }; l.add(p); save("patients", l); patients() }; secondaryButton("Cancelar") { patients() } }
    private fun details(p: JSONObject) { page("Ficha do paciente"); infoCard("Paciente", p.optString("name")); infoCard("Contato", "${p.optString("phone")}  •  Nascimento: ${p.optString("birth")}"); infoCard("Documentos", "CPF: ${p.optString("cpf").ifBlank { "—" }}"); infoCard("Endereço", p.optString("address").ifBlank { "—" }); infoCard("Observações", p.optString("notes").ifBlank { "—" }); infoCard("Histórico", "Solicitações: ${data("requests").count { it.optString("patientId") == p.optString("id") }}  •  Amostras: ${data("samples").count { it.optString("patientId") == p.optString("id") }}"); secondaryButton("←  Voltar") { patients() } }
    private fun requests() { page("Solicitações de exames"); button("＋  Nova solicitação") { requestForm() }; val l = data("requests"); if (l.isEmpty()) label("Nenhuma solicitação."); l.forEach { r -> infoCard(r.optString("patientName"), "${r.optString("exams")}\n${r.optString("date")}  •  ${r.optString("status")}"); button("Avançar status") { val s = listOf("Solicitada", "Recebida", "Em processamento", "Resultado", "Liberada"); r.put("status", s[(s.indexOf(r.optString("status")) + 1).coerceAtMost(4)]); save("requests", l); requests() } }; secondaryButton("←  Voltar") { home() } }
    private fun requestForm() { val ps = data("patients"); if (ps.isEmpty()) { toast("Cadastre um paciente primeiro"); return }; page("Nova solicitação"); section("Paciente"); val sp = Spinner(this).apply { adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, ps.map { it.optString("name") }) }; root.addView(sp); val e = field("Exames (Hemograma, Glicemia...)"); val o = field("Observações"); button("Salvar solicitação") { val p = ps[sp.selectedItemPosition]; val r = JSONObject().put("id", id()).put("patientId", p.optString("id")).put("patientName", p.optString("name")).put("exams", e.text.toString()).put("observations", o.text.toString()).put("date", now()).put("status", "Solicitada"); val l = data("requests"); l.add(r); save("requests", l); requests() }; secondaryButton("Cancelar") { requests() } }
    private fun samples() { page("Amostras"); button("＋  Registrar amostra") { sampleForm() }; val l = data("samples"); if (l.isEmpty()) label("Nenhuma amostra."); l.forEach { s -> infoCard(s.optString("code"), "${s.optString("patientName")}\n${s.optString("material")}  •  ${s.optString("status")}"); button("Avançar etapa") { val st = listOf("Coletada", "Recebida", "Em processamento", "Resultado", "Liberada"); s.put("status", st[(st.indexOf(s.optString("status")) + 1).coerceAtMost(4)]); save("samples", l); samples() } }; secondaryButton("←  Voltar") { home() } }
    private fun sampleForm() { val ps = data("patients"); if (ps.isEmpty()) { toast("Cadastre um paciente primeiro"); return }; page("Nova amostra"); section("Identificação"); val sp = Spinner(this).apply { adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, ps.map { it.optString("name") }) }; root.addView(sp); val c = field("Código da amostra"); val m = field("Material"); button("Salvar amostra") { val p = ps[sp.selectedItemPosition]; val s = JSONObject().put("id", id()).put("code", c.text.toString()).put("material", m.text.toString()).put("patientId", p.optString("id")).put("patientName", p.optString("name")).put("status", "Coletada").put("date", now()); val l = data("samples"); l.add(s); save("samples", l); samples() }; secondaryButton("Cancelar") { samples() } }
    private fun stock() { page("Estoque e validade"); button("＋  Novo item") { stockForm() }; val l = data("stock"); if (l.isEmpty()) label("Nenhum item."); l.forEach { s -> infoCard(s.optString("name"), "Lote ${s.optString("lot")}  •  Validade: ${s.optString("expiry")}\nQuantidade: ${s.optInt("qty")}  •  Mínimo: ${s.optInt("min")}"); button("Dar baixa  −1") { s.put("qty", (s.optInt("qty") - 1).coerceAtLeast(0)); save("stock", l); stock() } }; secondaryButton("←  Voltar") { home() } }
    private fun stockForm() { page("Novo item"); section("Dados do estoque"); val n = field("Nome"); val lot = field("Lote"); val ex = field("Validade (MM/AAAA)"); val q = field("Quantidade"); val mn = field("Estoque mínimo"); button("Salvar item") { val s = JSONObject().put("id", id()).put("name", n.text.toString()).put("lot", lot.text.toString()).put("expiry", ex.text.toString()).put("qty", q.text.toString().toIntOrNull() ?: 0).put("min", mn.text.toString().toIntOrNull() ?: 0); val l = data("stock"); l.add(s); save("stock", l); stock() }; secondaryButton("Cancelar") { stock() } }
    private fun reports() { page("Resultados e relatórios"); section("Indicadores"); infoCard("Pacientes", data("patients").size.toString()); infoCard("Solicitações", data("requests").size.toString()); infoCard("Amostras", data("samples").size.toString()); infoCard("Resultados liberados", data("requests").count { it.optString("status") == "Liberada" }.toString()); infoCard("Amostras liberadas", data("samples").count { it.optString("status") == "Liberada" }.toString()); infoCard("Atenção no estoque", data("stock").count { it.optInt("qty") <= it.optInt("min") }.toString()); secondaryButton("←  Voltar") { home() } }
    private fun appointments() { page("Agendamentos"); button("＋  Novo agendamento") { appointmentForm() }; val l = data("appointments"); if (l.isEmpty()) label("Nenhum agendamento."); l.forEach { a -> infoCard(a.optString("patientName"), "${a.optString("date")}\n${a.optString("type")}  •  ${a.optString("status")}"); secondaryButton("Cancelar") { a.put("status", "Cancelado"); save("appointments", l); appointments() } }; secondaryButton("←  Voltar") { home() } }
    private fun appointmentForm() { val ps = data("patients"); if (ps.isEmpty()) { toast("Cadastre um paciente primeiro"); return }; page("Novo agendamento"); section("Agendamento"); val sp = Spinner(this).apply { adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, ps.map { it.optString("name") }) }; root.addView(sp); val d = field("Data e hora"); val type = Spinner(this).apply { adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, listOf("Coleta", "Retorno", "Consulta")) }; root.addView(type); button("Salvar agendamento") { val p = ps[sp.selectedItemPosition]; val a = JSONObject().put("id", id()).put("patientId", p.optString("id")).put("patientName", p.optString("name")).put("date", d.text.toString()).put("type", type.selectedItem.toString()).put("status", "Agendado"); val l = data("appointments"); l.add(a); save("appointments", l); appointments() }; secondaryButton("Cancelar") { appointments() } }
    private fun consultations() { page("Consultas"); button("＋  Nova consulta") { consultationForm() }; val l = data("consultations"); if (l.isEmpty()) label("Nenhuma consulta."); l.forEach { c -> infoCard(c.optString("patientName"), "${c.optString("date")}\n${c.optString("notes")}") }; secondaryButton("←  Voltar") { home() } }
    private fun consultationForm() { val ps = data("patients"); if (ps.isEmpty()) { toast("Cadastre um paciente primeiro"); return }; page("Nova consulta"); section("Registro clínico"); val sp = Spinner(this).apply { adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, ps.map { it.optString("name") }) }; root.addView(sp); val n = field("Anotações"); button("Salvar consulta") { val p = ps[sp.selectedItemPosition]; val c = JSONObject().put("id", id()).put("patientId", p.optString("id")).put("patientName", p.optString("name")).put("date", now()).put("notes", n.text.toString()); val l = data("consultations"); l.add(c); save("consultations", l); consultations() }; secondaryButton("Cancelar") { consultations() } }
    private fun delete(k: String, i: String, done: () -> Unit) { AlertDialog.Builder(this).setTitle("Excluir registro?").setMessage("Esta ação não poderá ser desfeita.").setNegativeButton("Cancelar", null).setPositiveButton("Excluir") { _, _ -> val l = data(k); l.removeAll { it.optString("id") == i }; save(k, l); done() }.show() }
    private fun toast(s: String) = Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
}
