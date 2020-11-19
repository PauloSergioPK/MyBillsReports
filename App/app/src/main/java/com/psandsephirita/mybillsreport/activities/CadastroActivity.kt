package com.psandsephirita.mybillsreport.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.psandsephirita.mybillsreport.R
import com.psandsephirita.mybillsreport.config.ConfiguracaoFirebase
import com.psandsephirita.mybillsreport.helper.Base64Custom
import com.psandsephirita.mybillsreport.model.Usuario
import kotlinx.android.synthetic.main.activity_cadastro.*
import java.lang.Exception

class CadastroActivity : AppCompatActivity() {

    var autenticacao : FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)
        autenticacao = ConfiguracaoFirebase.getFireBaseAuth()

    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
    }

    fun cadastrarUsuario(usuario: Usuario){
        autenticacao?.createUserWithEmailAndPassword(
            usuario.email,
            usuario.senha
        )?.addOnCompleteListener(this, OnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(this,"Conta cadastrada com sucesso!",Toast.LENGTH_SHORT).show()
                finish()
                try{
                    var idUser = Base64Custom.codificarBase64(usuario.email)
                    usuario.id = idUser
                    usuario.salvar()
                }catch (e : Exception){
                    e.printStackTrace()
                }
            } else{
                var excecao : String = ""
                try{
                    throw it.exception!!
                }catch (e : FirebaseAuthWeakPasswordException) {
                    excecao = "Digite uma senha mais forte!"
                }catch (e : FirebaseAuthInvalidCredentialsException){
                    excecao = "Por favor, digite um e-mail válido"
                }catch (e : FirebaseAuthUserCollisionException){
                    excecao = "Esta conta já foi cadastrada"
                } catch (e : Exception){
                    excecao = "Erro ao cadastrar usuário: " + e.message
                    e.printStackTrace()
                }
                Toast.makeText(this,excecao,Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun validarCadastroUsuario(view: View){
        var textoNome = editTextNomeCadastro.text.toString()
        var textoEmail = editTextEmailCadastro.text.toString()
        var textoSenha = editTextSenhaCadastro.text.toString()
        if(!textoNome.isEmpty() && !textoEmail.isEmpty() && !textoSenha.isEmpty()){
            var usuario = Usuario(textoNome,textoEmail,textoSenha)
            cadastrarUsuario(usuario)
        }
        else{
            Toast.makeText(this,"Dados incompletos",Toast.LENGTH_SHORT).show();
        }
    }
}