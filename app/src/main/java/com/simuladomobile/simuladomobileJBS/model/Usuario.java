package com.simuladomobile.simuladomobileJBS.model;
import java.io.Serializable;

public class Usuario implements Serializable {
    private String nome;
    private String email;
    private String senha;
    private Boolean isAdmin;

    public Usuario() {}

    public Usuario(String nome, String email, String senha, Boolean isAdmin) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.isAdmin = isAdmin;
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

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

}
