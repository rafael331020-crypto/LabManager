package com.rafael.labmanager

import android.app.DatePickerDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.rafael.labmanager.data.model.AuditEventEntity
import com.rafael.labmanager.data.model.StockItemEntity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class StockActivity : AppCompatActivity() {
    private val navy = Color.rgb(20,47,78)
    private val blue = Color.rgb(31,111,184)
    private val green = Color.rgb(48,133,87)
    private val red = Color.rgb(190,65,65)
    private val orange = Color.rgb(224,125,45)
    private val bg = Color.rgb(244,247,250)
    private val muted = Color.rgb(91,106,122)
    private lateinit var list: LinearLayout
    private val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private fun dp(v:Int)=(v*resources.displayMetrics.density).toInt()
    private fun tv(s:String,size:Float,color:Int,bold:Boolean=false)=TextView(this).apply{ text=s;textSize=size;setTextColor(color);if(bold)setTypeface(null,Typeface.BOLD) }

    override fun onCreate(state:Bundle?) { super.onCreate(state); render(); observeStock() }

    private fun render(){
        val scroll=ScrollView(this).apply{setBackgroundColor(bg)}
        val root=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(14),dp(14),dp(14),dp(24))}
        scroll.addView(root);setContentView(scroll)

        val header=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;gravity=Gravity.CENTER_VERTICAL;setPadding(dp(16),dp(14),dp(16),dp(14));setBackgroundColor(navy)}
        val h=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL}
        h.addView(tv("LABMANAGER",12f,Color.WHITE,true));h.addView(tv("Estoque e materiais",20f,Color.WHITE,true).apply{setPadding(0,dp(3),0,0)})
        header.addView(h,LinearLayout.LayoutParams(0,-2,1f));header.addView(tv("OPERACIONAL",10f,Color.rgb(185,235,205),true));root.addView(header,LinearLayout.LayoutParams(-1,-2).apply{setMargins(0,0,0,dp(14))})

        root.addView(tv("Controle de estoque",21f,navy,true).apply{setPadding(dp(2),0,0,dp(3))})
        root.addView(tv("Lotes, validade, estoque mínimo e movimentações",12.5f,muted).apply{setPadding(dp(2),0,0,dp(12))})

        val metrics=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL}
        metric(metrics,"ITENS CADASTRADOS","0",blue,"items")
        metric(metrics,"ABAIXO DO MÍNIMO","0",red,"low")
        metric(metrics,"VALIDADE PRÓXIMA","0",orange,"expiry")
        root.addView(metrics)

        val add=button("+ NOVO MATERIAL",blue){showItemDialog(null)}
        root.addView(add,LinearLayout.LayoutParams(-1,dp(50)).apply{setMargins(0,dp(10),0,dp(12))})
        root.addView(tv("Inventário",18f,navy,true).apply{setPadding(dp(2),dp(6),0,dp(8))})
        list=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL};root.addView(list)
        root.addView(button("VOLTAR PARA GESTÃO",navy){finish()},LinearLayout.LayoutParams(-1,dp(50)).apply{setMargins(0,dp(18),0,0)})
    }

    private fun metric(parent:LinearLayout,title:String,value:String,color:Int,key:String){
        val card=MaterialCardView(this).apply{radius=dp(9).toFloat();cardElevation=dp(1).toFloat();setCardBackgroundColor(Color.WHITE);setContentPadding(dp(14),dp(10),dp(14),dp(10))}
        val row=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;gravity=Gravity.CENTER_VERTICAL}
        row.addView(tv(title,11.5f,muted,true),LinearLayout.LayoutParams(0,-2,1f))
        row.addView(tv(value,22f,color,true).apply{tag=key;gravity=Gravity.END})
        card.addView(row);parent.addView(card,LinearLayout.LayoutParams(-1,-2).apply{setMargins(0,dp(3),0,dp(3))})
    }

    private fun observeStock(){
        lifecycleScope.launch { LabManagerApp.repository.stock().collectLatest { items ->
            val metrics=items
            updateMetric("items",items.size.toString())
            updateMetric("low",items.count{it.quantity<=it.minimumQuantity}.toString())
            updateMetric("expiry",items.count{nearExpiry(it.expiryDate)}.toString())
            list.removeAllViews()
            if(items.isEmpty()) list.addView(tv("Nenhum material cadastrado.",13f,muted).apply{setPadding(dp(4),dp(16),dp(4),dp(16))})
            items.forEach{addItemCard(it)}
        }}
    }

    private fun updateMetric(key:String,value:String){
        val root=findViewById<ScrollView>(android.R.id.content)?.getChildAt(0) ?: return
        updateMetricRecursive(root,key,value)
    }
    private fun updateMetricRecursive(v:View,key:String,value:String){
        if(v.tag==key && v is TextView)v.text=value
        if(v is ViewGroup)for(i in 0 until v.childCount)updateMetricRecursive(v.getChildAt(i),key,value)
    }

    private fun addItemCard(item:StockItemEntity){
        val low=item.quantity<=item.minimumQuantity
        val exp=nearExpiry(item.expiryDate)
        val card=MaterialCardView(this).apply{radius=dp(10).toFloat();cardElevation=dp(2).toFloat();setCardBackgroundColor(Color.WHITE);setContentPadding(dp(14),dp(12),dp(14),dp(12))}
        val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL}
        val top=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;gravity=Gravity.CENTER_VERTICAL}
        top.addView(tv(item.name,15f,navy,true),LinearLayout.LayoutParams(0,-2,1f))
        val status=if(low)"ABAIXO DO MÍNIMO" else if(exp)"VALIDADE PRÓXIMA" else "REGULAR"
        val statusColor=if(low)red else if(exp)orange else green
        top.addView(tv(status,9.5f,statusColor,true).apply{gravity=Gravity.END});box.addView(top)
        box.addView(tv("Lote ${item.lot}  •  Validade ${item.expiryDate}",12f,muted).apply{setPadding(0,dp(5),0,0)})
        box.addView(tv("Saldo: ${item.quantity}  •  Mínimo: ${item.minimumQuantity}  •  Fornecedor: ${item.supplier.ifBlank{"—"}}",12f,muted).apply{setPadding(0,dp(4),0,dp(8) )})
        val actions=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL}
        actions.addView(smallButton("ENTRADA",green){movement(item, true) },LinearLayout.LayoutParams(0,dp(42),1f).apply{setMargins(0,0,dp(5),0)})
        actions.addView(smallButton("SAÍDA",red){movement(item, false)},LinearLayout.LayoutParams(0,dp(42),1f).apply{setMargins(dp(5),0,dp(5),0)})
        actions.addView(smallButton("EDITAR",blue){showItemDialog(item)},LinearLayout.LayoutParams(0,dp(42),1f).apply{setMargins(dp(5),0,0,0)})
        box.addView(actions);card.addView(box);list.addView(card,LinearLayout.LayoutParams(-1,-2).apply{setMargins(0,dp(4),0,dp(4))})
    }

    private fun showItemDialog(existing:StockItemEntity?){
        val layout=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(4),0,dp(4),0)}
        val name=field(layout,"Material / reagente",existing?.name ?: "")
        val lot=field(layout,"Lote",existing?.lot ?: "")
        val expiry=field(layout,"Validade (AAAA-MM-DD)",existing?.expiryDate ?: fmt.format(Date()))
        val qty=field(layout,"Quantidade",existing?.quantity?.toString() ?: "0")
        val min=field(layout,"Estoque mínimo",existing?.minimumQuantity?.toString() ?: "0")
        val supplier=field(layout,"Fornecedor",existing?.supplier ?: "")
        val d=androidx.appcompat.app.AlertDialog.Builder(this).setTitle(if(existing==null)"Novo material" else "Editar material").setView(layout).setNegativeButton("CANCELAR",null).setPositiveButton("SALVAR",null).create()
        d.setOnShowListener{d.getButton(-1).setOnClickListener{
            val n=name.text?.toString()?.trim().orEmpty();val l=lot.text?.toString()?.trim().orEmpty();val e=expiry.text?.toString()?.trim().orEmpty();val q=qty.text?.toString()?.toIntOrNull();val m=min.text?.toString()?.toIntOrNull();val s=supplier.text?.toString()?.trim().orEmpty()
            if(n.isBlank()||l.isBlank()||q==null||m==null||e.isBlank()){Toast.makeText(this,"Preencha os campos obrigatórios.",Toast.LENGTH_SHORT).show();return@setOnClickListener}
            lifecycleScope.launch{LabManagerApp.repository.saveStock(StockItemEntity(existing?.id ?: UUID.randomUUID().toString(),n,l,e,q,m,s,now()));audit(if(existing==null)"CADASTRO_ESTOQUE" else "ALTERACAO_ESTOQUE",existing?.id ?: "novo");Toast.makeText(this@StockActivity,"Material salvo.",Toast.LENGTH_SHORT).show();d.dismiss()}
        }};d.show()
    }

    private fun movement(item:StockItemEntity,incoming:Boolean){
        val input=EditText(this).apply{hint="Quantidade";inputType=2}
        val title=if(incoming)"Entrada de estoque" else "Saída de estoque"
        androidx.appcompat.app.AlertDialog.Builder(this).setTitle(title).setMessage("${item.name} • lote ${item.lot}").setView(input).setNegativeButton("CANCELAR",null).setPositiveButton("CONFIRMAR"){_,_->
            val amount=input.text.toString().toIntOrNull() ?: 0
            if(amount<=0){Toast.makeText(this,"Quantidade inválida.",Toast.LENGTH_SHORT).show();return@setPositiveButton}
            if(!incoming && amount>item.quantity){Toast.makeText(this,"Saldo insuficiente.",Toast.LENGTH_SHORT).show();return@setPositiveButton}
            lifecycleScope.launch{LabManagerApp.repository.saveStock(item.copy(quantity=if(incoming)item.quantity+amount else item.quantity-amount,updatedAt=now()));audit(if(incoming)"ENTRADA_ESTOQUE" else "SAIDA_ESTOQUE",item.id);Toast.makeText(this@StockActivity,"Movimentação registrada.",Toast.LENGTH_SHORT).show()}
        }.show()
    }

    private fun field(parent:LinearLayout,hint:String,value:String):TextInputEditText{val box=TextInputLayout(this).apply{this.hint=hint;boxBackgroundMode=TextInputLayout.BOX_BACKGROUND_OUTLINE;boxStrokeColor=blue};val input=TextInputEditText(this).apply{setText(value);textSize=14f;setPadding(dp(12),dp(8),dp(12),dp(8))};box.addView(input);parent.addView(box,LinearLayout.LayoutParams(-1,dp(62)).apply{setMargins(0,dp(4),0,dp(4))});return input}
    private fun button(text:String,color:Int,click:()->Unit)=MaterialButton(this).apply{this.text=text;isAllCaps=false;setTextColor(Color.WHITE);backgroundTintList=ColorStateList.valueOf(color);cornerRadius=dp(9);setOnClickListener{click()}}
    private fun smallButton(text:String,color:Int,click:()->Unit)=MaterialButton(this).apply{this.text=text;isAllCaps=false;textSize=11f;setTextColor(Color.WHITE);backgroundTintList=ColorStateList.valueOf(color);cornerRadius=dp(7);setOnClickListener{click()}}
    private fun now()=SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault()).format(Date())
    private fun nearExpiry(value:String):Boolean=try{val d=fmt.parse(value) ?: return false;val days=(d.time-Date().time)/(1000L*60*60*24);days in 0..60}catch(_:Exception){false}
    private suspend fun audit(action:String,id:String){LabManagerApp.repository.audit(AuditEventEntity(UUID.randomUUID().toString(),"local-user",action,"STOCK",id,now(),"android"))}
}
