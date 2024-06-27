package it.unimib.sd2024.Models;

public class Registrazione {
    // attributi privati
    private String dominioNome;
    private String dominioTLD;
    private String utenteEmail;
    private String registrationDate;
    private String expirationDate;

    // costruttori
    public Registrazione(String dominioNome, String dominioTLD, String utenteEmail, String registrationDate, String expirationDate){
        setDominioNome(dominioNome);
        setDominioTLD(dominioTLD);
        setUtenteEmail(utenteEmail);
        setRegistrationDate(registrationDate);
        setExpirationDate(expirationDate);
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
    public String getRegistrationDate(){
        return this.registrationDate;
    }
    public String getExpirationDate(){
        return this.expirationDate;
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
    public void setRegistrationDate(String registrationDate){
        this.registrationDate = registrationDate;
    }
    public void setExpirationDate(String expirationDate){
        this.expirationDate = expirationDate;
    }
}