package it.unimib.sd2024;

public class Dominio {
    // attributi privati
    private String nome;
    private String tld;
    private String expirationDate;

    // costruttori
    public Dominio(String nome, String tld, String expirationDate){
        setNome(nome);
        setTLD(tld);
        setExpirationDate(expirationDate);
    }

    // getter
    public String getNome(){
        return this.nome;
    }
    public String getTLD(){
        return this.tld;
    }
    public String getExpirationDate(){
        return this.expirationDate;
    }

    // setter
    public void setNome(String nome){
        this.nome = nome;
    }
    public void setTLD(String tld){
        this.tld = tld;
    }
    public void setExpirationDate(String expirationDate){
        this.expirationDate = expirationDate;
    }
}