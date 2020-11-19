package com.psandsephirita.mybillsreport.model

import java.io.Serializable
import java.math.BigDecimal
import java.util.*

class Despesa : Serializable{
    var id : String = ""
    var valor : Double = 0.0
    lateinit var categoria : String
    lateinit var descricao : String
    lateinit var data : Date
    var pago : Boolean = false

    constructor(){

    }

    constructor(valor : Double,categoria : String, descricao : String, data : Date, pago : Boolean){
        this.valor = valor
        this.descricao = descricao
        this.data = data
        this.pago = pago
        this.categoria = categoria
    }

}