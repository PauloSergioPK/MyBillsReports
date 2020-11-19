package com.psandsephirita.mybillsreport.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.psandsephirita.mybillsreport.R
import com.psandsephirita.mybillsreport.model.Receita
import kotlinx.android.synthetic.main.adapter_receitas.view.*


class AdapterReceitas(private val myDataset: ArrayList<Receita>) :
        RecyclerView.Adapter<AdapterReceitas.MyViewHolder>() {

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        fun bind(receita: Receita) {
            itemView.textDescricaoReceita.text = receita.descricao
            itemView.textCategoriaReceita.text = receita.categoria
            itemView.textValorReceita.text = "R$ " + receita.valor.toString()
            if(receita.recebido)
                itemView.textRecebido.text = "Recebido"
            else
                itemView.textRecebido.text = "Não Recebido"
            val dia : String = receita.data.day.toString()
            val mes : String = receita.data.month.toString()
            val ano : String = receita.data.year.toString()
            val data = "$dia/$mes/$ano"
            itemView.textDataReceita.text = data
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): AdapterReceitas.MyViewHolder {
        val textView = LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_receitas, parent, false)

        return MyViewHolder(textView)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(myDataset[position])
    }

    override fun getItemCount() = myDataset.size

}