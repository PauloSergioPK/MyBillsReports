package com.psandsephirita.mybillsreport.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.psandsephirita.mybillsreport.R
import com.psandsephirita.mybillsreport.model.Despesa
import kotlinx.android.synthetic.main.adapter_despesas.view.*
import kotlinx.android.synthetic.main.adapter_despesas.view.*


class AdapterDespesas(private val myDataset: ArrayList<Despesa>) :
        RecyclerView.Adapter<AdapterDespesas.MyViewHolder>() {

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        fun bind(despesa: Despesa) {
            itemView.textDescricaoDespesa.text = despesa.descricao
            itemView.textCategoriaDespesa.text = despesa.categoria
            itemView.textValorDespesa.text = "R$ " + despesa.valor.toString()
            if(despesa.pago)
                itemView.textPago.text = "Pago"
            else
                itemView.textPago.text = "Não Pago"
            val dia : String = despesa.data.day.toString()
            val mes : String = despesa.data.month.toString()
            val ano : String = despesa.data.year.toString()
            val data = "$dia/$mes/$ano"
            itemView.textDataDespesa.text = data
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): AdapterDespesas.MyViewHolder {
        val textView = LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_despesas, parent, false)

        return MyViewHolder(textView)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(myDataset[position])
    }

    override fun getItemCount() = myDataset.size

}