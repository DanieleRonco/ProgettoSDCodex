package it.unimib.sd2024;

public class Utente {
    // attributi privati
    private String nome;
    private String cognome;
    private String email;

    // costruttori
    public Utente(String nome, String cognome, String email){
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
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
}