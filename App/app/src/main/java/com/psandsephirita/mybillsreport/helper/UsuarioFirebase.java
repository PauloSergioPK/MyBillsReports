package com.psandsephirita.mybillsreport.helper;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.psandsephirita.mybillsreport.config.ConfiguracaoFirebase;
import com.psandsephirita.mybillsreport.model.Usuario;


public class UsuarioFirebase {

    public static String getIdentificadorUsuario(){
        FirebaseAuth usuario = ConfiguracaoFirebase.getFireBaseAuth();
        String email = usuario.getCurrentUser().getEmail();
        String idUser = Base64Custom.codificarBase64(email);
        return idUser;
    }

    public static FirebaseUser getUsuarioAtual(){
        FirebaseAuth usuario = ConfiguracaoFirebase.getFireBaseAuth();
        return usuario.getCurrentUser();
    }

    public static boolean atualizarFotoUsuario(Uri url){
        try{
            FirebaseUser user = getUsuarioAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(url)
                    .build();
            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful()){
                        Log.d("Perfil","Erro ao atualizar foto de perfil");
                    }
                }
            });
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static boolean atualizarNomeUsuario(String nome){
        try{
            FirebaseUser user = getUsuarioAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(nome)
                    .build();
            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful()){
                        Log.d("Perfil","Erro ao atualizar nome de perfil");
                    }
                }
            });
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static Usuario getDadosUsuarioLogado(){
        FirebaseUser user = getUsuarioAtual();
        Usuario usuario = new Usuario();
        usuario.setEmail(user.getEmail());
        usuario.setNome(user.getDisplayName());
        if(user.getPhotoUrl() == null){
            usuario.setUrlFoto("");
        }
        else{
            usuario.setUrlFoto(user.getPhotoUrl().toString());
        }
        return usuario;
    }
}
