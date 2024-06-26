package it.unimib.sd2024;

public class Dominio {
    // attributi privati
    private String ID;
    private String nome;
    private String tld;

    // costruttori
    public Dominio(String ID, String nome, String tld){
        setID(ID);
        setNome(nome);
        setTLD(tld);
    }

    // getter
    public String getID(){
        return this.ID;
    }
    public String getNome(){
        return this.nome;
    }
    public String getTLD(){
        return this.tld;
    }

    // setter
    public void setID(String ID){
        this.ID = ID;
    }
    public void setNome(String nome){
        this.nome = nome;
    }
    public void setTLD(String tld){
        this.tld = tld;
    }
}