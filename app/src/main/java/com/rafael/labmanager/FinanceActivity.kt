package com.rafael.labmanager

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONArray
import org.json.JSONObject
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class FinanceActivity : AppCompatActivity() {
    private val prefs by lazy { getSharedPreferences("labmanager_finance", Context.MODE_PRIVATE) }
    private val navy=Color.rgb(15,39,64); private val blue=Color.rgb(24,111,183); private val green=Color.rgb(39,139,91)
    private val red=Color.rgb(190,65,65); private val orange=Color.rgb(230,119,43); private val bg=Color.rgb(245,247,250); private val muted=Color.rgb(96,110,126)
    private fun dp(v:Int)=(v*resources.displayMetrics.density).toInt()
    private fun money(v:Double)=NumberFormat.getCurrencyInstance(Locale("pt","BR")).format(v)
    private fun now()=SimpleDateFormat("dd/MM/yyyy HH:mm",Locale("pt","BR")).format(Date())
    private fun entries():MutableList<JSONObject>{val a=JSONArray(prefs.getString("entries","[]"));return MutableList(a.length()){a.getJSONObject(it)}}
    private fun save(l:List<JSONObject>){val a=JSONArray();l.forEach{a.put(it)};prefs.edit().putString("entries",a.toString()).apply()}
    private fun tv(s:String,size:Float,c:Int,b:Boolean=false)=TextView(this).apply{text=s;textSize=size;setTextColor(c);if(b)setTypeface(null,Typeface.BOLD)}
    override fun onCreate(b:Bundle?){super.onCreate(b);home()}
    private lateinit var root:LinearLayout
    private fun page(title:String,sub:String=""){root=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(18),dp(16),dp(18),dp(28));setBackgroundColor(bg)};val scroll=ScrollView(this).apply{addView(root);setBackgroundColor(bg)};setContentView(scroll);root.addView(tv(title,25f,navy,true));if(sub.isNotBlank())root.addView(tv(sub,13f,muted).apply{setPadding(0,dp(4),0,dp(12))})}
    private fun card(title:String,body:String,c:Int=blue){val x=MaterialCardView(this).apply{radius=dp(12).toFloat();cardElevation=dp(2).toFloat();setCardBackgroundColor(Color.WHITE);setContentPadding(dp(14),dp(12),dp(14),dp(12))};val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL};box.addView(tv(title,13f,c,true));box.addView(tv(body,20f,navy,true).apply{setPadding(0,dp(5),0,0)});x.addView(box);root.addView(x,LinearLayout.LayoutParams(-1,-2).apply{setMargins(0,dp(4),0,dp(4))})}
    private fun button(s:String,c:Int=blue,action:()->Unit){val b=MaterialButton(this).apply{text=s;isAllCaps=false;textSize=14f;setTextColor(Color.WHITE);backgroundTintList=android.content.res.ColorStateList.valueOf(c);cornerRadius=dp(10);minimumHeight=dp(50);setOnClickListener{action()}};root.addView(b,LinearLayout.LayoutParams(-1,dp(52)).apply{setMargins(0,dp(5),0,dp(5))})}
    private fun secondary(s:String,action:()->Unit){val b=MaterialButton(this).apply{text=s;isAllCaps=false;textSize=13f;setTextColor(navy);backgroundTintList=android.content.res.ColorStateList.valueOf(Color.WHITE);strokeWidth=dp(1);strokeColor=android.content.res.ColorStateList.valueOf(Color.rgb(205,214,224));cornerRadius=dp(10);setOnClickListener{action()}};root.addView(b,LinearLayout.LayoutParams(-1,dp(46)).apply{setMargins(0,dp(4),0,dp(4))})}
    private fun field(h:String,v:String=""):EditText{val l=TextInputLayout(this).apply{hint=h;boxBackgroundMode=TextInputLayout.BOX_BACKGROUND_OUTLINE};val e=TextInputEditText(this).apply{setText(v);textSize=15f};l.addView(e);root.addView(l,LinearLayout.LayoutParams(-1,-2).apply{setMargins(0,dp(4),0,dp(4))});return e}
    private fun home(){page("Financeiro","Gestão financeira do laboratório • visão operacional");val l=entries();val rec=l.filter{it.optString("type")=="RECEITA"}.sumOf{it.optDouble("value")};val pay=l.filter{it.optString("type")=="DESPESA"}.sumOf{it.optDouble("value")};val open=l.count{it.optString("status")=="PENDENTE"};card("RECEITAS",money(rec),green);card("DESPESAS",money(pay),red);card("SALDO",money(rec-pay),if(rec-pay>=0)green else red);card("PENDÊNCIAS","$open lançamentos pendentes",orange);root.addView(tv("Movimentação",18f,navy,true).apply{setPadding(0,dp(18),0,dp(7))});button("Novo lançamento"){form()};button("Contas a receber"){listScreen("RECEITA")};button("Contas a pagar"){listScreen("DESPESA")};button("Fluxo de caixa"){cashFlow()};button("Faturamento e convênios"){billing()};secondary("Voltar à Central do LIS"){finish()}}
    private fun form(){page("Novo lançamento","Registre uma entrada ou saída financeira");val desc=field("Descrição");val value=field("Valor","0,00");val type=field("Tipo","RECEITA");val due=field("Vencimento");val payer=field("Cliente / fornecedor");val category=field("Categoria","Laboratório");val status=field("Status","PENDENTE");button("Salvar lançamento",green){val v=value.text.toString().replace(".","").replace(",",".").toDoubleOrNull();if(desc.text.toString().isBlank()||v==null||v<=0){Toast.makeText(this,"Descrição e valor são obrigatórios",Toast.LENGTH_SHORT).show();return@button};val l=entries();l.add(JSONObject().apply{put("id","FIN-${UUID.randomUUID().toString().take(8).uppercase()}");put("description",desc.text.toString());put("value",v);put("type",type.text.toString().uppercase());put("due",due.text.toString());put("party",payer.text.toString());put("category",category.text.toString());put("status",status.text.toString().uppercase());put("created",now())});save(l);home()};secondary("Cancelar"){home()}}
    private fun listScreen(type:String){page(if(type=="RECEITA")"Contas a receber" else "Contas a pagar","Lançamentos financeiros");val l=entries().filter{it.optString("type")==type};if(l.isEmpty())root.addView(tv("Nenhum lançamento cadastrado.",14f,muted));l.forEach{o->val status=o.optString("status");val c=if(status=="PAGO"||status=="RECEBIDO")green else if(status=="VENCIDO")red else orange;val x=MaterialCardView(this).apply{radius=dp(11).toFloat();cardElevation=dp(1).toFloat();setCardBackgroundColor(Color.WHITE);setContentPadding(dp(14),dp(11),dp(14),dp(11))};val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL};box.addView(tv(o.optString("description"),15f,navy,true));box.addView(tv("${money(o.optDouble("value"))} • ${o.optString("party","-")}",13f,muted).apply{setPadding(0,dp(4),0,0)});box.addView(tv("Venc.: ${o.optString("due","-")} • $status",12f,c,true).apply{setPadding(0,dp(4),0,0)});x.addView(box);root.addView(x,LinearLayout.LayoutParams(-1,-2).apply{setMargins(0,dp(4),0,dp(4))});secondary("Marcar como ${if(type=="RECEITA")"RECEBIDO" else "PAGO"}"){o.put("status",if(type=="RECEITA")"RECEBIDO" else "PAGO");save(entries());listScreen(type)}};secondary("Novo lançamento"){form()};secondary("Voltar"){home()}}
    private fun cashFlow(){page("Fluxo de caixa","Resumo das movimentações registradas");val l=entries();if(l.isEmpty())root.addView(tv("Sem movimentações.",14f,muted));l.sortedByDescending{it.optString("created")}.forEach{o->val sign=if(o.optString("type")=="RECEITA")"+" else "-";root.addView(tv("${o.optString("created")}  •  ${o.optString("description")}  $sign${money(o.optDouble("value"))}",13f,if(sign=="+")green else red).apply{setPadding(0,dp(8),0,dp(8))})};secondary("Voltar"){home()}}
    private fun billing(){page("Faturamento","Convênios • particular • produção");card("Produção registrada","${entries().size} lançamentos",blue);card("Convênios","Controle de faturamento e recebíveis",navy);card("Glosas","Acompanhar pendências e ajustes",orange);button("Novo faturamento"){form()};secondary("Voltar"){home()}}
}
