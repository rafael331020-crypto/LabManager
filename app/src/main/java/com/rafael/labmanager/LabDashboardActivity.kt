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

class LabDashboardActivity : AppCompatActivity() {
    private val navy = Color.rgb(15, 39, 64)
    private val blue = Color.rgb(24, 111, 183)
    private val green = Color.rgb(39, 139, 91)
    private val orange = Color.rgb(230, 119, 43)
    private val bg = Color.rgb(245, 247, 250)
    private val muted = Color.rgb(96, 110, 126)
    private fun dp(v:Int)= (v * resources.displayMetrics.density).toInt()
    private fun text(s:String,size:Float,color:Int,bold:Boolean=false)=TextView(this).apply{
        this.text=s; textSize=size; setTextColor(color)
        if(bold) setTypeface(null, Typeface.BOLD)
    }
    override fun onCreate(state:Bundle?){ super.onCreate(state); build() }
    private fun build(){
        val root=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(18),dp(20),dp(18),dp(28));setBackgroundColor(bg)}
        val scroll=ScrollView(this).apply{addView(root);setBackgroundColor(bg)}
        setContentView(scroll)

        val header=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(20),dp(20),dp(20),dp(20));setBackgroundColor(navy)}
        header.addView(text("LABMANAGER",13f,Color.WHITE,true))
        header.addView(text("Gestão Laboratorial",27f,Color.WHITE,true).apply{setPadding(0,dp(6),0,0)})
        header.addView(text("LIS • operação integrada",14f,Color.rgb(205,220,235)).apply{setPadding(0,dp(4),0,0)})
        root.addView(header,LinearLayout.LayoutParams(-1,-2).apply{setMargins(0,0,0,dp(16))})

        root.addView(text("Visão geral",20f,navy,true).apply{setPadding(dp(2),0,0,dp(8))})
        stat(root,"ATENDIMENTO","Pacientes e requisições",blue){openLis()}
        stat(root,"AMOSTRAS","Coleta • triagem • rastreio",orange){openLis()}
        stat(root,"ÁREA TÉCNICA","Resultados • conferência • liberação",green){openLis()}
        stat(root,"GESTÃO","Estoque • qualidade • financeiro",navy){openLis()}

        root.addView(text("Fluxo laboratorial",20f,navy,true).apply{setPadding(dp(2),dp(20),0,dp(8))})
        val flow=MaterialCardView(this).apply{radius=dp(14).toFloat();cardElevation=dp(2).toFloat();setCardBackgroundColor(Color.WHITE);setContentPadding(dp(16),dp(15),dp(16),dp(15))}
        val flowBox=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL}
        listOf("1  Atendimento → pedido","2  Coleta → identificação","3  Triagem → processamento","4  Resultado → conferência","5  Liberação → rastreabilidade").forEachIndexed{ i,s ->
            flowBox.addView(text(s,14f,if(i==4)green else navy,i==4).apply{setPadding(0,dp(5),0,dp(5))})
        }
        flow.addView(flowBox);root.addView(flow,LinearLayout.LayoutParams(-1,-2).apply{setMargins(0,0,0,dp(16))})

        val open=MaterialButton(this).apply{text="ABRIR SISTEMA LIS";isAllCaps=false;textSize=15f;setTextColor(Color.WHITE);backgroundTintList=android.content.res.ColorStateList.valueOf(blue);cornerRadius=dp(12);minimumHeight=dp(54);setOnClickListener{openLis()}}
        root.addView(open,LinearLayout.LayoutParams(-1,dp(56)).apply{setMargins(0,dp(4),0,dp(8))})
        root.addView(text("Protótipo local • dados armazenados no dispositivo",12f,muted).apply{gravity=Gravity.CENTER})
    }
    private fun stat(root:LinearLayout,title:String,subtitle:String,color:Int,onClick:()->Unit){
        val card=MaterialCardView(this).apply{radius=dp(14).toFloat();cardElevation=dp(2).toFloat();setCardBackgroundColor(Color.WHITE);isClickable=true;setOnClickListener{onClick()};setContentPadding(dp(16),dp(14),dp(16),dp(14))}
        val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL}
        box.addView(text(title,15f,color,true));box.addView(text(subtitle,13f,muted).apply{setPadding(0,dp(5),0,0)})
        card.addView(box);root.addView(card,LinearLayout.LayoutParams(-1,-2).apply{setMargins(0,dp(4),0,dp(4))})
    }
    private fun openLis(){startActivity(Intent(this,LisActivity::class.java))}
}
