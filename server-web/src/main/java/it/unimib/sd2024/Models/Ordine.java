package it.unimib.sd2024.Models;


public class Ordine {
    // attributi privati
    private String dominioNome;
    private String dominioTLD;
    private String utenteEmail;
    private String numeroCarta;
    private String data;
    private String oggetto;
    private Double quota;

    public Ordine(){}

    // costruttori
    public Ordine(String dominioNome, String dominioTLD, String utenteEmail, String numeroCarta, String data, String oggetto, Double quota){
        this.setDominioNome(dominioNome);
        this.setDominioTLD(dominioTLD);
        this.setUtenteEmail(utenteEmail);
        this.setNumeroCarta(numeroCarta);
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
    public String getNumeroCarta(){
        return this.numeroCarta;
    }
    public String getData(){
        return this.data;
    }
    public String getOggetto(){
        return this.oggetto;
    }
    public Double getQuota(){
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
    public void setNumeroCarta(String numeroCarta){
        this.numeroCarta = numeroCarta;
    }
    public void setData(String data){
        this.data = data;
    }
    public void setOggetto(String oggetto){
        this.oggetto = oggetto;
    }
    public void setQuota(Double quota){
        this.quota = quota;
    }
}