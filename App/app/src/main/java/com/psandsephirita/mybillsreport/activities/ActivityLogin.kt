package com.psandsephirita.mybillsreport.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.psandsephirita.mybillsreport.R
import com.psandsephirita.mybillsreport.config.ConfiguracaoFirebase
import com.psandsephirita.mybillsreport.model.Usuario
import kotlinx.android.synthetic.main.activity_login.*
import java.lang.Exception

class ActivityLogin : AppCompatActivity() {

    var autenticacao : FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        autenticacao = ConfiguracaoFirebase.getFireBaseAuth()


    }

    fun cadastrar(view: View){
        val intent = Intent(this, CadastroActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
    }

    fun logar(usuario: Usuario){
        autenticacao?.signInWithEmailAndPassword(
            usuario.email,
            usuario.senha)?.addOnCompleteListener(OnCompleteListener {
            if(it.isSuccessful){ //ir para tela principal
                irParaTelaPrincipal()
            } else{
                var excecao : String = ""
                try{
                    throw it.exception!!
                }catch (e : FirebaseAuthInvalidUserException) {
                    excecao = "Usuário não cadastrado."
                }catch (e : FirebaseAuthInvalidCredentialsException){
                    excecao = "E-mail e senha não correspondem à um usuário cadastrado"
                } catch (e : Exception){
                    excecao = "Erro ao fazer o login : " + e.message
                    e.printStackTrace()
                }
                Toast.makeText(this,excecao,Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun validarlogin(view: View){
        var textoEmail = editTextEmailLogin.text.toString()
        var textoSenha = editTextSenhaLogin.text.toString()
        if(!textoEmail.isEmpty() && !textoSenha.isEmpty()){
            var usuario = Usuario()
            usuario.email = textoEmail
            usuario.senha = textoSenha
            logar(usuario)
        }
        else{
            Toast.makeText(this,"Dados incompletos",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()
        var firebaseUser = autenticacao?.currentUser
        if(firebaseUser != null){
            irParaTelaPrincipal()
        }
    }

    fun irParaTelaPrincipal(){
        var intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}