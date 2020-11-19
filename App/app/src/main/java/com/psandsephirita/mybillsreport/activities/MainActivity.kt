package com.psandsephirita.mybillsreport.activities

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toolbar
import androidx.annotation.RequiresApi
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Pie
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.psandsephirita.mybillsreport.R
import com.psandsephirita.mybillsreport.config.ConfiguracaoFirebase
import com.psandsephirita.mybillsreport.helper.UsuarioFirebase
import com.psandsephirita.mybillsreport.model.Despesa
import com.psandsephirita.mybillsreport.model.Receita
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.despesas_por_categoria_layout.*
import kotlinx.android.synthetic.main.economia_mensal_layout.*
import kotlinx.android.synthetic.main.layout_saldo.*
import kotlinx.android.synthetic.main.receitas_por_categoria_layout.*
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    var autenticacao : FirebaseAuth? = null
    var progressoEconomiaMensal : Int = 0
    private lateinit var referenceReceitas : DatabaseReference
    private lateinit var referenceDespesas : DatabaseReference
    private lateinit var receitas : ArrayList<Receita>
    private lateinit var despesas : ArrayList<Despesa>
    private lateinit var valueEventListenerReceitas: ValueEventListener
    private lateinit var valueEventListenerDespesas: ValueEventListener
    var totalReceitas : Double = 0.0
    var totalDespesas : Double = 0.0
    var optionsDespesas = arrayOf("Moradia","Alimentação","Saúde","Educação","Transporte","Lazer","Outros")
    var optionsReservas = arrayOf("Salário","Renda extra","Renda de Investimentos","Aluguéis","Outros")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbarPrincipal)
        supportActionBar?.title = ""
        autenticacao = ConfiguracaoFirebase.getFireBaseAuth()
        referenceReceitas = ConfiguracaoFirebase.getFireBaseDatabase()
                .child("receitas")
                .child(UsuarioFirebase.getIdentificadorUsuario())
        referenceDespesas = ConfiguracaoFirebase.getFireBaseDatabase()
                .child("despesas")
                .child(UsuarioFirebase.getIdentificadorUsuario())
        receitas = ArrayList()
        despesas = ArrayList()

        iniciarGraficoDespesas()
        iniciarGraficoReceitas()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId.equals(R.id.menuSair)){
            deslogarUsuario()
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun deslogarUsuario(){
        try{
            autenticacao?.signOut()
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

    fun updateEconomia(){
        progress_economia_mensal.progress = progressoEconomiaMensal
        textPorcentagemEconomia.text = "$progressoEconomiaMensal%"
    }

    fun irParaTelaPerfil(view: View){
        var intent = Intent(this,ProfileActivity::class.java)
        startActivity(intent)
    }

    fun irParaTelaReceitas(view: View){
        var intent = Intent(this,ReceitasActivity::class.java)
        startActivity(intent)
    }

    fun irParaTelaDespesas(view: View){
        var intent = Intent(this,DespesasActivity::class.java)
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        recuperarReceitasDespesas()
    }

    override fun onStop() {
        super.onStop()
        referenceReceitas.removeEventListener(valueEventListenerReceitas)
        referenceDespesas.removeEventListener(valueEventListenerDespesas)

    }

    fun recuperarReceitasDespesas(){
        valueEventListenerReceitas = referenceReceitas.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                receitas.clear()
                if(snapshot.exists()){
                    for(snap in snapshot.children){
                        Log.i("teste",snap.toString())
                        var aux = snap.getValue(Receita::class.java)
                        if (aux != null) {
                            Log.i("Teste",aux.recebido.toString())
                            receitas.add(aux)
                        }
                    }
                }
                calcularValorReceitas()
                atualizarGraficos()


            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        valueEventListenerDespesas = referenceDespesas.addValueEventListener(object :ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                despesas.clear()
                if(snapshot.exists()){
                    for(snap in snapshot.children){
                        Log.i("teste",snap.toString())
                        var aux = snap.getValue(Despesa::class.java)
                        if (aux != null) {
                            Log.i("Teste",aux.pago.toString())
                            despesas.add(aux)
                        }
                    }
                }
                calcularValorDespesas()
                atualizarGraficos()

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        atualizarGraficos()
    }

    fun calcularValorReceitas(){
        var total : Double = 0.0
        for(receita in receitas){
            total += receita.valor
        }
        textValorReceitasMain.text = "R$ " + total.toString()
        totalReceitas = total
    }

    fun calcularValorDespesas(){
        var total : Double = 0.0
        for(despesa in despesas){
            total += despesa.valor
        }
        textValorDespesasMain.text = "R$ " + total.toString()
        totalDespesas = total
    }

    fun atualizarGraficos(){
        atualizarDespesas()
        atualizarEconomia()
        atualizarReceitas()
        var dif = totalReceitas - totalDespesas
        textSaldoTotal.text = "R$ " + String.format(Locale.US,"%.2f",dif)
        if(dif < 0)
            textSaldoTotal.setTextColor(textValorDespesasMain.textColors)
        else
            textSaldoTotal.setTextColor(resources.getColor(R.color.teal_700))
    }

    fun iniciarGraficoDespesas(){


        var initialData = ArrayList<PieEntry>()
        initialData.add(PieEntry(25.0F,optionsDespesas[0]))
        initialData.add(PieEntry(25.0F,optionsDespesas[1]))
        initialData.add(PieEntry(25.0F,optionsDespesas[2]))
        initialData.add(PieEntry(25.0F,optionsDespesas[3]))
        initialData.add(PieEntry(25.0F,optionsDespesas[4]))
        initialData.add(PieEntry(25.0F,optionsDespesas[5]))
        initialData.add(PieEntry(25.0F,optionsDespesas[6]))

        var pieDataSet = PieDataSet(initialData,"")
        pieDataSet.setColors(ColorTemplate.createColors(ColorTemplate.COLORFUL_COLORS))
        pieDataSet.valueTextColor = Color.BLACK
        pieDataSet.valueTextSize = 16f

        var pieData = PieData(pieDataSet)

        chartPieDespesa.data = pieData
        chartPieDespesa.description.isEnabled = false
        chartPieDespesa.centerText = "dados iniciais"
        chartPieDespesa.animate()

    }

    fun iniciarGraficoReceitas(){
        var initialData = ArrayList<PieEntry>()
        initialData.add(PieEntry(25.0F,optionsDespesas[0]))
        initialData.add(PieEntry(25.0F,optionsDespesas[1]))
        initialData.add(PieEntry(25.0F,optionsDespesas[2]))
        initialData.add(PieEntry(25.0F,optionsDespesas[3]))
        initialData.add(PieEntry(25.0F,optionsDespesas[4]))
        initialData.add(PieEntry(25.0F,optionsDespesas[5]))
        initialData.add(PieEntry(25.0F,optionsDespesas[6]))

        var pieDataSet = PieDataSet(initialData,"")
        pieDataSet.setColors(ColorTemplate.createColors(ColorTemplate.COLORFUL_COLORS))
        pieDataSet.valueTextColor = Color.BLACK
        pieDataSet.valueTextSize = 16f

        var pieData = PieData(pieDataSet)

        chartPieReceita.data = pieData
        chartPieReceita.description.isEnabled = false
        chartPieReceita.centerText = "dados receitas"
        chartPieReceita.animate()
    }

    fun atualizarDespesas(){

        var initialData = ArrayList<PieEntry>()
        for(despesa in despesas){
            initialData.add(PieEntry(despesa.valor.toFloat(),despesa.categoria))
        }

        var pieDataSet = PieDataSet(initialData,"")
        pieDataSet.setColors(ColorTemplate.createColors(ColorTemplate.COLORFUL_COLORS))
        pieDataSet.valueTextColor = Color.WHITE
        pieDataSet.valueTextSize = 16f

        var pieData = PieData(pieDataSet)

        chartPieDespesa.data = pieData
        chartPieDespesa.description.isEnabled = false
        chartPieDespesa.centerText = "despesas"
        chartPieDespesa.animate()
        chartPieDespesa.invalidate()

    }

    fun atualizarEconomia(){
        if(totalReceitas < totalDespesas){
            progress_economia_mensal.progress = 0
            textPorcentagemEconomia.text = "0%"
            textDinheiroEconomizado.text = "R$ 0.00"
        }
        else{
            var dif = totalReceitas-totalDespesas
            textDinheiroEconomizado.text = "R$ " + dif.toString()
            if(totalReceitas >= 1){
                var result = ((dif/totalReceitas)*100).toInt()
                if(result > 100)
                    result = 100
                progress_economia_mensal.progress = result
                textPorcentagemEconomia.text = String.format(Locale.US,"%.2f",(dif/totalReceitas)) + "%"

            }
            else{
                progress_economia_mensal.progress = 0
                textPorcentagemEconomia.text = "0%"
            }
        }
    }


    fun atualizarReceitas(){
        var initialData = ArrayList<PieEntry>()
        for(receita in receitas){
            initialData.add(PieEntry(receita.valor.toFloat(),receita.categoria))
        }

        var pieDataSet = PieDataSet(initialData,"")
        pieDataSet.setColors(ColorTemplate.createColors(ColorTemplate.MATERIAL_COLORS))
        pieDataSet.valueTextColor = Color.WHITE
        pieDataSet.valueTextSize = 16f

        var pieData = PieData(pieDataSet)

        chartPieReceita.data = pieData
        chartPieReceita.description.isEnabled = false
        chartPieReceita.centerText = "receitas"
        chartPieReceita.animate()
        chartPieReceita.invalidate()
    }


}