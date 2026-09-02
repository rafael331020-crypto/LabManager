package com.rafael.labmanager

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*

/** Modern LIS control center. The operational screens are implemented in LisActivity. */
class LisDashboardActivity : AppCompatActivity() {
    private val prefs by lazy { getSharedPreferences("labmanager_lis_v2", MODE_PRIVATE) }
    private val navy = Color.rgb(15, 39, 62)
    private val blue = Color.rgb(25, 108, 177)
    private val orange = Color.rgb(235, 119, 39)
    private val green = Color.rgb(43, 137, 87)
    private val red = Color.rgb(192, 65, 65)
    private val bg = Color.rgb(244, 247, 250)
    private val muted = Color.rgb(91, 108, 125)
    private fun count(key: String) = JSONArray(prefs.getString(key, "[]")).length()
    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
    private fun text(s:String,size:Float,color:Int,bold:Boolean=false)=TextView(this).apply{ text=s; textSize=size; setTextColor(color); if(bold)setTypeface(null,Typeface.BOLD) }

    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); render() }

    private fun render() {
        val scroll=ScrollView(this).apply{setBackgroundColor(bg)}
        val root=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(18),dp(16),dp(18),dp(30))}
        scroll.addView(root); setContentView(scroll)

        val head=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(20),dp(18),dp(20),dp(20));setBackgroundColor(navy)}
        head.addView(text("LabManager",29f,Color.WHITE,true))
        head.addView(text("LIS • Centro de Operações",14f,Color.rgb(205,220,232),false).apply{setPadding(0,dp(3),0,dp(12))})
        val online=TextView(this).apply{text="● SISTEMA ONLINE";textSize=12f;setTextColor(Color.rgb(160,225,184));setTypeface(null,Typeface.BOLD)}
        head.addView(online)
        root.addView(head,LinearLayout.LayoutParams(-1,-2).apply{setMargins(0,0,0,dp(16))})

        root.addView(text("Visão geral",21f,navy,true))
        root.addView(text(SimpleDateFormat("EEEE, dd/MM/yyyy • HH:mm",Locale("pt","BR")).format(Date()).replaceFirstChar{it.uppercase()},13f,muted).apply{setPadding(0,dp(4),0,dp(12))})
        val stats=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL}
        stats.addView(stat("PACIENTES",count("patients").toString(),blue,"Cadastros ativos"))
        stats.addView(stat("PEDIDOS",count("orders").toString(),orange,"Requisições registradas"))
        stats.addView(stat("AMOSTRAS",count("samples").toString(),green,"Rastreabilidade"))
        stats.addView(stat("RESULTADOS",count("results").toString(),red,"Área técnica"))
        root.addView(stats)

        root.addView(text("Fluxo laboratorial",21f,navy,true).apply{setPadding(0,dp(18),0,dp(8))})
        val flow=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL}
        module(flow,"01  ATENDIMENTO","Paciente • convênio • requisição","Recepção e cadastro"){openLis()}
        module(flow,"02  COLETA E TRIAGEM","Identificação • tubos • recebimento • rejeição","Pré-analítico"){openLis()}
        module(flow,"03  ÁREA TÉCNICA","Lista de trabalho • resultados • conferência","Analítico"){openLis()}
        module(flow,"04  LIBERAÇÃO","Revisão • críticos • liberação em lote","Pós-analítico"){openLis()}
        root.addView(flow)

        root.addView(text("Gestão e qualidade",21f,navy,true).apply{setPadding(0,dp(18),0,dp(8))})
        module(root,"ESTOQUE","Lotes • validade • mínimo • consumo","Suprimentos"){openLis()}
        module(root,"QUALIDADE","CIQ • ocorrências • não conformidades • ações","Qualidade"){openLis()}
        module(root,"FINANCEIRO","Faturamento • contas • fluxo de caixa","Gestão"){openLis()}
        module(root,"BI E RASTREABILIDADE","Indicadores • auditoria • histórico","Gestão"){openLis()}

        val b=MaterialButton(this).apply{text="ABRIR SISTEMA OPERACIONAL";isAllCaps=false;textSize=15f;setTextColor(Color.WHITE);backgroundTintList=android.content.res.ColorStateList.valueOf(blue);cornerRadius=dp(12);minimumHeight=dp(54);setOnClickListener{openLis()}}
        root.addView(b,LinearLayout.LayoutParams(-1,dp(56)).apply{setMargins(0,dp(18),0,dp(6))})
        root.addView(text("LabManager • versão LIS 2.0",12f,muted).apply{gravity=Gravity.CENTER;setPadding(0,dp(8),0,0)})
    }

    private fun stat(title:String,value:String,color:Int,desc:String):View{
        val c=MaterialCardView(this).apply{radius=dp(14).toFloat();cardElevation=dp(1).toFloat();setCardBackgroundColor(Color.WHITE);setContentPadding(dp(15),dp(12),dp(15),dp(12))}
        val row=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;gravity=Gravity.CENTER_VERTICAL}
        val left=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;layoutParams=LinearLayout.LayoutParams(0,-2,1f)}
        left.addView(text(title,11f,color,true));left.addView(text(value,25f,navy,true));left.addView(text(desc,11f,muted))
        row.addView(left);row.addView(text("›",30f,color,true));c.addView(row)
        return c
    }
    private fun module(parent:LinearLayout,title:String,body:String,tag:String,action:()->Unit){
        val c=MaterialCardView(this).apply{radius=dp(13).toFloat();cardElevation=dp(1).toFloat();setCardBackgroundColor(Color.WHITE);isClickable=true;setOnClickListener{action()};setContentPadding(dp(16),dp(14),dp(16),dp(14))}
        val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL}
        box.addView(text(title,15f,navy,true));box.addView(text(body,13f,Color.rgb(48,65,82)).apply{setPadding(0,dp(5),0,dp(2))});box.addView(text(tag.uppercase(),10f,muted,true))
        c.addView(box);parent.addView(c,LinearLayout.LayoutParams(-1,-2).apply{setMargins(0,dp(4),0,dp(4))})
    }
    private fun openLis(){startActivity(Intent(this,LisActivity::class.java))}
}
