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
        val til = TextInputLayout(this).apply { hint = h; boxBackgroundMode = TextInputLayout.BOX_BACKGROUND_OUTLINE }
        til.shapeAppearanceModel = til.shapeAppearanceModel.toBuilder().setAllCornerSizes(dp(10).toFloat()).build()
        val e = TextInputEditText(this).apply { setText(v); textSize = 15f; minLines = if (h.contains("Observ", true)) 3 else 1 }
        til.addView(e); root.addView(til, LinearLayout.LayoutParams(-1, -2).apply { setMargins(0, dp(5), 0, dp(5)) }); return e
    }

    private fun home() {
        page("LabManager")
        val hero = MaterialCardView(this).apply { radius = dp(18).toFloat(); cardElevation = dp(3).toFloat(); setCardBackgroundColor(navy); setContentPadding(dp(20), dp(20), dp(20), dp(20)) }
        val hb = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        hb.addView(TextView(this).apply { text = "Gestão laboratorial"; textSize = 23f; setTextColor(Color.WHITE); setTypeface(null, Typeface.BOLD) })
        hb.addView(TextView(this).apply { text = "Controle de pacientes, exames, amostras e estoque"; textSize = 14f; setTextColor(Color.WHITE); setPadding(0, dp(6), 0, 0) })
        hero.addView(hb); root.addView(hero, LinearLayout.LayoutParams(-1, -2).apply { setMargins(0, 0, 0, dp(12)) })
        section("Visão geral")
        val patients = data("patients").size; val exams = data("exams").size; val samples = data("samples").size; val inventory = data("inventory").size
        infoCard("Pacientes", patients.toString()); infoCard("Solicitações de exames", exams.toString()); infoCard("Amostras", samples.toString()); infoCard("Itens de estoque", inventory.toString())
        section("Módulos")
        button("Pacientes") { patientsPage() }; button("Solicitações de exames") { examsPage() }; button("Amostras") { samplesPage() }; button("Estoque") { inventoryPage() }; button("Agendamentos") { appointmentsPage() }; button("Consultas") { consultationsPage() }; secondaryButton("Relatórios") { reportsPage() }
    }

    private fun patientsPage() { page("Pacientes"); button("Cadastrar paciente") { patientForm() }; section("Cadastrados"); val l=data("patients"); if(l.isEmpty()) label("Nenhum paciente cadastrado.") else l.forEach { p -> infoCard(p.optString("name","Sem nome"), "Telefone: ${p.optString("phone","-")}\nNascimento: ${p.optString("birth","-")}"); secondaryButton("Abrir ${p.optString("name","Paciente")}") { patientDetail(p.optString("id")) } }; secondaryButton("Voltar") { home() } }

    private fun patientForm(existing: JSONObject? = null) { page(if(existing==null) "Novo paciente" else "Editar paciente"); val name=field("Nome",existing?.optString("name","")?:("")); val phone=field("Telefone",existing?.optString("phone","")?:("")); val birth=field("Data de nascimento",existing?.optString("birth","")?:("")); val cpf=field("CPF",existing?.optString("cpf","")?:("")); val obs=field("Observações",existing?.optString("obs","")?:("")); button("Salvar") { if(name.text.toString().trim().isEmpty()){ Toast.makeText(this,"Informe o nome.",Toast.LENGTH_SHORT).show(); return@button }; val l=data("patients"); val p=existing?:JSONObject().apply{put("id",id())}; p.put("name",name.text.toString().trim()); p.put("phone",phone.text.toString().trim()); p.put("birth",birth.text.toString().trim()); p.put("cpf",cpf.text.toString().trim()); p.put("obs",obs.text.toString().trim()); p.put("updated",now()); if(existing==null) l.add(p); save("patients",l); Toast.makeText(this,"Paciente salvo.",Toast.LENGTH_SHORT).show(); patientsPage() }; secondaryButton("Cancelar") { patientsPage() } }

    private fun patientDetail(pid:String) { val p=data("patients").firstOrNull{it.optString("id")==pid}?:return; page("Paciente"); infoCard("Nome",p.optString("name")); infoCard("Telefone",p.optString("phone","-")); infoCard("Nascimento",p.optString("birth","-")); infoCard("CPF",p.optString("cpf","-")); infoCard("Observações",p.optString("obs","-")); button("Editar") { patientForm(p) }; button("Excluir") { AlertDialog.Builder(this).setTitle("Excluir paciente?").setMessage("Esta ação não pode ser desfeita.").setNegativeButton("Cancelar",null).setPositiveButton("Excluir"){_,_->val l=data("patients"); l.removeAll{it.optString("id")==pid}; save("patients",l); patientsPage()}.show() }; secondaryButton("Voltar") { patientsPage() } }

    private fun examsPage() { page("Solicitações de exames"); button("Nova solicitação") { examForm() }; section("Solicitações"); val l=data("exams"); if(l.isEmpty()) label("Nenhuma solicitação cadastrada.") else l.forEach { e -> infoCard(e.optString("test","Exame"), "Paciente: ${e.optString("patient","-")}\nStatus: ${e.optString("status","Solicitada")}"); secondaryButton("Avançar status") { advance("exams",e.optString("id"),listOf("Solicitada","Recebida","Em processamento","Resultado","Liberada")); examsPage() } }; secondaryButton("Voltar") { home() } }
    private fun examForm() { page("Nova solicitação"); val patient=field("Paciente"); val test=field("Exame solicitado"); val material=field("Material"); button("Salvar solicitação") { val l=data("exams"); l.add(JSONObject().apply{put("id",id());put("patient",patient.text.toString());put("test",test.text.toString());put("material",material.text.toString());put("status","Solicitada");put("created",now())});save("exams",l);examsPage() };secondaryButton("Cancelar"){examsPage()} }

    private fun samplesPage() { page("Amostras"); button("Registrar amostra") { sampleForm() }; section("Amostras registradas"); val l=data("samples"); if(l.isEmpty()) label("Nenhuma amostra registrada.") else l.forEach { s -> infoCard("Amostra ${s.optString("code")}","Paciente: ${s.optString("patient","-")}\nStatus: ${s.optString("status","Coletada")}"); secondaryButton("Avançar status") { advance("samples",s.optString("id"),listOf("Coletada","Recebida","Em processamento","Resultado","Liberada"));samplesPage() } };secondaryButton("Voltar"){home()} }
    private fun sampleForm(){page("Registrar amostra");val code=field("Código da amostra");val patient=field("Paciente");val material=field("Material");button("Registrar"){val l=data("samples");l.add(JSONObject().apply{put("id",id());put("code",code.text.toString());put("patient",patient.text.toString());put("material",material.text.toString());put("status","Coletada");put("created",now())});save("samples",l);samplesPage()};secondaryButton("Cancelar"){samplesPage()} }
    private fun advance(k:String,oid:String,states:List<String>){val l=data(k);l.firstOrNull{it.optString("id")==oid}?.let{val i=states.indexOf(it.optString("status"));it.put("status",states[minOf(i+1,states.lastIndex)]);it.put("updated",now())};save(k,l)}

    private fun inventoryPage(){page("Estoque");button("Adicionar item"){inventoryForm()};section("Itens");val l=data("inventory");if(l.isEmpty())label("Nenhum item cadastrado.")else l.forEach{i->infoCard(i.optString("name","Item"),"Quantidade: ${i.optInt("qty",0)}\nUnidade: ${i.optString("unit","un")}");secondaryButton("Dar baixa") {val x=data("inventory");x.firstOrNull{it.optString("id")==i.optString("id")}?.let{it.put("qty",maxOf(0,it.optInt("qty")-1))};save("inventory",x);inventoryPage()}};secondaryButton("Voltar"){home()}}
    private fun inventoryForm(){page("Novo item");val name=field("Nome do item");val qty=field("Quantidade");val unit=field("Unidade");button("Salvar"){val l=data("inventory");l.add(JSONObject().apply{put("id",id());put("name",name.text.toString());put("qty",qty.text.toString().toIntOrNull()?:0);put("unit",unit.text.toString())});save("inventory",l);inventoryPage()};secondaryButton("Cancelar"){inventoryPage()}}

    private fun appointmentsPage(){page("Agendamentos");button("Novo agendamento"){appointmentForm()};section("Agenda");val l=data("appointments");if(l.isEmpty())label("Nenhum agendamento.")else l.forEach{a->infoCard(a.optString("patient","Paciente"),"Data: ${a.optString("date","-")}\nHorário: ${a.optString("time","-")}\nStatus: ${a.optString("status","Agendado")}");secondaryButton("Cancelar") {val x=data("appointments");x.firstOrNull{it.optString("id")==a.optString("id")}?.put("status","Cancelado");save("appointments",x);appointmentsPage()}};secondaryButton("Voltar"){home()}}
    private fun appointmentForm(){page("Novo agendamento");val patient=field("Paciente");val date=field("Data");val time=field("Horário");val type=field("Tipo de atendimento");button("Agendar"){val l=data("appointments");l.add(JSONObject().apply{put("id",id());put("patient",patient.text.toString());put("date",date.text.toString());put("time",time.text.toString());put("type",type.text.toString());put("status","Agendado")});save("appointments",l);appointmentsPage()};secondaryButton("Cancelar"){appointmentsPage()}}

    private fun consultationsPage(){page("Consultas");button("Nova consulta"){consultationForm()};section("Registros");val l=data("consultations");if(l.isEmpty())label("Nenhuma consulta registrada.")else l.forEach{c->infoCard(c.optString("patient","Paciente"),"Data: ${c.optString("date",now())}\nTipo: ${c.optString("type","Consulta")}\nObservações: ${c.optString("obs","-")}")};secondaryButton("Voltar"){home()}}
    private fun consultationForm(){page("Nova consulta");val patient=field("Paciente");val type=field("Tipo");val obs=field("Observações");button("Salvar consulta"){val l=data("consultations");l.add(JSONObject().apply{put("id",id());put("patient",patient.text.toString());put("type",type.text.toString());put("obs",obs.text.toString());put("date",now())});save("consultations",l);consultationsPage()};secondaryButton("Cancelar"){consultationsPage()}}

    private fun reportsPage(){page("Relatórios");infoCard("Pacientes",data("patients").size.toString());infoCard("Exames",data("exams").size.toString());infoCard("Amostras",data("samples").size.toString());infoCard("Agendamentos",data("appointments").size.toString());infoCard("Consultas",data("consultations").size.toString());val low=data("inventory").count{it.optInt("qty",0)<=5};infoCard("Itens com estoque baixo",low.toString());secondaryButton("Voltar"){home()}}
}
