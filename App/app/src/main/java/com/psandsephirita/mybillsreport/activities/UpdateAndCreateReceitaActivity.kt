package com.psandsephirita.mybillsreport.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import com.psandsephirita.mybillsreport.R
import com.psandsephirita.mybillsreport.model.Receita
import kotlinx.android.synthetic.main.activity_update_and_create_receita.*
import java.util.*

class UpdateAndCreateReceitaActivity : AppCompatActivity() {

    lateinit var option : Spinner
    var categoriaEscolhida = ""
    lateinit var mDataSetListener : DatePickerDialog.OnDateSetListener
    var ano : Int = -1
    var mes : Int = -1
    var dia : Int = -1
    var idReceita : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_and_create_receita)
        option = spinnerReceitas
        var options = arrayOf("Salário","Renda extra","Renda de Investimentos","Aluguéis","Outros")
        option.adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,options)

        option.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                categoriaEscolhida = options.get(p2)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

        mDataSetListener = DatePickerDialog.OnDateSetListener(){ datePicker: DatePicker, i: Int, i1: Int, i2: Int ->
            mes = i1 + 1
            ano = i
            dia = i2
            Log.i("teste","$dia/$mes/$ano")
        }

        var dados = intent.extras
        if(dados != null){
            var receita = dados.getSerializable("updateReceita") as Receita
            if(receita != null){
                editTextDescricaoReceita.setText(receita.descricao)
                editTextValorReceita.setText(receita.valor.toString())
                if(receita.recebido)
                    checkBoxReceita.isChecked = true
                ano = receita.data.year
                mes = receita.data.month
                dia = receita.data.day
            }
            categoriaEscolhida = receita.categoria
            idReceita = receita.id

        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun abrirCalendario(view : View){
        if(ano != -1 && dia != -1 && mes != -1){
            var dialog = DatePickerDialog(
                    this,
                    android.R.style.ThemeOverlay_Material_Dialog,
                    mDataSetListener,
                    ano,mes-1,dia
            )
            dialog.show()
        }
        else{
            var cal = Calendar.getInstance()
            var anoAtual = cal.get(Calendar.YEAR)
            var mesAtual = cal.get(Calendar.MONTH)
            var diaAtual = cal.get(Calendar.DAY_OF_MONTH)

            var dialog = DatePickerDialog(
                    this,
                            android.R.style.ThemeOverlay_Material_Dialog,
                    mDataSetListener,
                    anoAtual,mesAtual,diaAtual
            )
            dialog.show()
        }
    }

    fun confirmarUpdateOuAdd(view : View){
        if(ano == -1 && mes == -1 && dia == -1)
            Toast.makeText(this,"Selecione uma data válida",Toast.LENGTH_SHORT).show()
        else if(!validarDados())
            Toast.makeText(this,"Dados inválidos",Toast.LENGTH_SHORT).show()
        else{ //deu tudo certo e vamo confirmar
            var valor = editTextValorReceita.text.toString().toDouble()
            var recebido = false
            if(checkBoxReceita.isChecked)
                recebido = true
            var receita = Receita(valor,categoriaEscolhida,editTextDescricaoReceita.text.toString(),Date(ano,mes,dia),recebido)
            receita.id = idReceita
            var result = Intent()
            result.putExtra("novaReceita",receita)
            setResult(RESULT_OK,result)
            finish()

        }
    }

    fun validarDados() : Boolean{
        if(editTextDescricaoReceita.text.isEmpty() || editTextValorReceita.text.isEmpty()){
            return false
        }
        return true
    }
}