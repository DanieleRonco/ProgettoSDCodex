package it.unimib.sd2024.Models;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.json.bind.annotation.JsonbProperty;

public class Utente {
    // attributi privati
    @JsonbProperty
    private String nome;
    @JsonbProperty
    private String cognome;
    @JsonbProperty
    private String email;
    @JsonbProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    public Utente(){
    }

    // costruttori
    public Utente(String nome, String cognome, String email, String password){
        setNome(nome);
        setCognome(cognome);
        setEmail(email);
        setPassword(password);
    }

    // getter
    public String getNome(){
        return this.nome;
    }
    public String getCognome(){
        return this.cognome;
    }
    public String getEmail(){
        return this.email;
    }
    public String getPassword(){
        return this.password;
    }

    // setter
    public void setNome(String nome){
        this.nome = nome;
    }
    public void setCognome(String cognome){
        this.cognome = cognome;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public void setPassword(String password){
        this.password = password;
    }
}