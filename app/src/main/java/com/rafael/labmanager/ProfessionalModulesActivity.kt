package com.rafael.labmanager

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class ProfessionalModulesActivity : AppCompatActivity() {
    private val navy = Color.rgb(20, 47, 78)
    private val blue = Color.rgb(31, 111, 184)
    private val green = Color.rgb(48, 133, 87)
    private val orange = Color.rgb(224, 125, 45)
    private val bg = Color.rgb(244, 247, 250)
    private val muted = Color.rgb(91, 106, 122)
    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
    private fun tv(s: String, size: Float, color: Int, bold: Boolean = false) = TextView(this).apply { text=s; textSize=size; setTextColor(color); if(bold)setTypeface(null,Typeface.BOLD) }
    override fun onCreate(state: Bundle?) { super.onCreate(state); render() }
    private fun render() {
        val root=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(16),dp(14),dp(16),dp(24));setBackgroundColor(bg)}
        val scroll=ScrollView(this).apply{addView(root);setBackgroundColor(bg)};setContentView(scroll)
        val top=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;gravity=Gravity.CENTER_VERTICAL;setPadding(dp(16),dp(14),dp(16),dp(14));setBackgroundColor(navy)}
        val title=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL};title.addView(tv("LABMANAGER",13f,Color.WHITE,true));title.addView(tv("Centro de gestão",21f,Color.WHITE,true).apply{setPadding(0,dp(3),0,0)});top.addView(title,LinearLayout.LayoutParams(0,-2,1f));top.addView(tv("ONLINE",11f,Color.rgb(185,235,205),true));root.addView(top,LinearLayout.LayoutParams(-1,-2).apply{setMargins(0,0,0,dp(14))})
        root.addView(tv("Módulos",20f,navy,true).apply{setPadding(dp(2),0,0,dp(8))})
        module(root,"ATENDIMENTO","Pacientes • pedidos • convênios",blue){openLis()}
        module(root,"AMOSTRAS","Coleta • triagem • rejeição • recoleta",orange){openLis()}
        module(root,"ÁREA TÉCNICA","Resultados • conferência • liberação",green){openLis()}
        module(root,"ESTOQUE","Itens • lotes • validade • mínimo",blue){openLis()}
        module(root,"QUALIDADE","CIQ • não conformidades • auditoria",green){openLis()}
        module(root,"FINANCEIRO","Faturamento • contas • fluxo de caixa",orange){startActivity(Intent(this,FinanceActivity::class.java))}
        root.addView(tv("Visão operacional",20f,navy,true).apply{setPadding(dp(2),dp(18),0,dp(8))})
        val table=MaterialCardView(this).apply{radius=dp(10).toFloat();cardElevation=dp(2).toFloat();setCardBackgroundColor(Color.WHITE);setContentPadding(dp(14),dp(10),dp(14),dp(10))}
        val rows=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL};row(rows,"ATENDIMENTOS","Hoje","—");row(rows,"AMOSTRAS","Em processamento","—");row(rows,"RESULTADOS","Aguardando liberação","—");row(rows,"ESTOQUE","Abaixo do mínimo","—");row(rows,"FINANCEIRO","Pendências","—");table.addView(rows);root.addView(table,LinearLayout.LayoutParams(-1,-2).apply{setMargins(0,0,0,dp(14))})
        val actions=MaterialButton(this).apply{text="ABRIR OPERAÇÃO LIS";isAllCaps=false;textSize=15f;setTextColor(Color.WHITE);backgroundTintList=android.content.res.ColorStateList.valueOf(blue);cornerRadius=dp(10);minimumHeight=dp(52);setOnClickListener{openLis()}};root.addView(actions,LinearLayout.LayoutParams(-1,dp(54)).apply{setMargins(0,0,0,dp(8))});root.addView(tv("Interface inspirada em sistemas LIS empresariais • protótipo local",11.5f,muted).apply{gravity=Gravity.CENTER})
    }
    private fun module(root:LinearLayout,title:String,sub:String,accent:Int,click:()->Unit){val card=MaterialCardView(this).apply{radius=dp(10).toFloat();cardElevation=dp(1).toFloat();setCardBackgroundColor(Color.WHITE);isClickable=true;setOnClickListener{click()};setContentPadding(dp(14),dp(11),dp(14),dp(11))};val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL};box.addView(tv(title,14f,accent,true));box.addView(tv(sub,12.5f,muted).apply{setPadding(0,dp(4),0,0)});card.addView(box);root.addView(card,LinearLayout.LayoutParams(-1,-2).apply{setMargins(0,dp(3),0,dp(3))})}
    private fun row(root:LinearLayout,a:String,b:String,value:String){val line=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;gravity=Gravity.CENTER_VERTICAL;setPadding(0,dp(8),0,dp(8))};line.addView(tv(a,12f,navy,true),LinearLayout.LayoutParams(0,-2,1f));line.addView(tv(b,11.5f,muted),LinearLayout.LayoutParams(0,-2,1.2f));line.addView(tv(value,13f,navy,true).apply{gravity=Gravity.END});root.addView(line)}
    private fun openLis(){startActivity(Intent(this,LisActivity::class.java))}
}
