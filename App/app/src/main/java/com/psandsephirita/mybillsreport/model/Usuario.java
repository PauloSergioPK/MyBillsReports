package com.psandsephirita.mybillsreport.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.psandsephirita.mybillsreport.config.ConfiguracaoFirebase;
import com.psandsephirita.mybillsreport.helper.UsuarioFirebase;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Usuario implements Serializable {

    private String id;
    private String nome;
    private String email;
    private String senha;
    private String  urlFoto;
    private ArrayList<Receita> receitas;
    private ArrayList<Despesa> despesas;

    public Usuario(){

    }

    public Usuario(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.urlFoto = "";
    }

    public void salvar(){
        DatabaseReference reference = ConfiguracaoFirebase.getFireBaseDatabase();
        DatabaseReference usuario = reference.child("usuarios").child(getId());
        usuario.setValue(this);
    }

    public void atualizar(){
        String identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        DatabaseReference firebaseDatabase = ConfiguracaoFirebase.getFireBaseDatabase();
        DatabaseReference usuariosRef = firebaseDatabase.child("usuarios").child(identificadorUsuario);
        Map<String,Object> valoresUsuario = converterParaMap();
        usuariosRef.updateChildren(valoresUsuario);
    }

    @Exclude
    public Map<String,Object> converterParaMap(){
        HashMap<String,Object> usuarioMap = new HashMap<>();
        usuarioMap.put("email",getEmail());
        usuarioMap.put("nome",getNome());
        usuarioMap.put("urlFoto",getUrlFoto());
        return usuarioMap;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }


    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<Receita> getReceitas() {
        return receitas;
    }

    public void setReceitas(ArrayList<Receita> receitas) {
        this.receitas = receitas;
    }

    public ArrayList<Despesa> getDespesas() {
        return despesas;
    }

    public void setDespesas(ArrayList<Despesa> despesas) {
        this.despesas = despesas;
    }
}
