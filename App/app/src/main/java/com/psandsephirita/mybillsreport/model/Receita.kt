package com.psandsephirita.mybillsreport.model

import com.google.firebase.database.Exclude
import java.io.Serializable
import java.math.BigDecimal
import java.util.*

class Receita : Serializable{
    var id : String = ""
    var valor : Double = 0.0
    lateinit var categoria : String
    lateinit var descricao : String
    lateinit var data : Date
    var recebido : Boolean = false

    constructor(){

    }
    constructor(valor : Double, categoria : String, descricao : String, data : Date, recebido : Boolean){
        this.valor = valor
        this.descricao = descricao
        this.data = data
        this.recebido = recebido
        this.categoria = categoria
    }
}