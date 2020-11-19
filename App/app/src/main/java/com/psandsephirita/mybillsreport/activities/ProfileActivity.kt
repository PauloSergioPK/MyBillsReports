package com.psandsephirita.mybillsreport.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.psandsephirita.mybillsreport.R
import com.psandsephirita.mybillsreport.helper.UsuarioFirebase
import com.psandsephirita.mybillsreport.model.Usuario
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    lateinit var user : Usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        user = UsuarioFirebase.getDadosUsuarioLogado()
        textViewEmailProfile.text = user.email
        //textViewNomeProfile.text = user.nome

    }
}