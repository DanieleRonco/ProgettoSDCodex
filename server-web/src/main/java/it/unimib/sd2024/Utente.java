package it.unimib.sd2024;

public class Utente {
    // attributi privati
    private String ID;
    private String nome;
    private String cognome;
    private String email;
    private String password;

    // costruttori
    public Utente(String ID, String nome, String cognome, String email, String password){
        setID(ID);
        setNome(nome);
        setCognome(cognome);
        setEmail(email);
        setPassword(password);
    }

    // getter
    public String getID(){
        return this.ID;
    }
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
    public void setID(String ID){
        this.ID = ID;
    }
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