package it.unimib.sd2024.Resources.httpModels;

import jakarta.json.bind.annotation.JsonbTransient;

public class HTTPOrdine {
    // attributi privati
    private String dominioNome;
    private String dominioTLD;
    @JsonbTransient
    private String utenteEmail;
    @JsonbTransient
    private String numeroCarta;
    private String data;
    private String oggetto;
    private float quota;

    // costruttori
    public HTTPOrdine(String dominioNome, String dominioTLD, String utenteEmail, String numeroCarta, String data, String oggetto, float quota){
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
    public float getQuota(){
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
    public void setQuota(float quota){
        this.quota = quota;
    }
}