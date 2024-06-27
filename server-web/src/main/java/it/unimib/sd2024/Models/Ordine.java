package it.unimib.sd2024.Models;

public class Ordine {
    // attributi privati
    private String dominioNome;
    private String dominioTLD;
    private String utenteEmail;
    private String data;
    private String oggetto;
    private String quota;

    // costruttori
    public Ordine(String dominioNome, String dominioTLD, String utenteEmail, String data, String oggetto, String quota){
        this.setDominioNome(dominioNome);
        this.setDominioTLD(dominioTLD);
        this.setUtenteEmail(utenteEmail);
        this.setData(data);
        this.setOggetto(oggetto);
        this.setQuota(quota);
    }

    // getter
    public String getDominioNome(){
        return this.dominioNome;
    }
    public String getDominioTLD(){
        return this.dominioTLD;
    }
    public String getUtenteEmail(){
        return this.utenteEmail;
    }
    public String getData(){
        return this.data;
    }
    public String getOggetto(){
        return this.oggetto;
    }
    public String getQuota(){
        return this.quota;
    }

    // setter
    public void setDominioNome(String dominioNome){
        this.dominioNome = dominioNome;
    }
    public void setDominioTLD(String dominioTLD){
        this.dominioTLD = dominioTLD;
    }
    public void setUtenteEmail(String utenteEmail){
        this.utenteEmail = utenteEmail;
    }
    public void setData(String data){
        this.data = data;
    }
    public void setOggetto(String oggetto){
        this.oggetto = oggetto;
    }
    public void setQuota(String quota){
        this.quota = quota;
    }
}