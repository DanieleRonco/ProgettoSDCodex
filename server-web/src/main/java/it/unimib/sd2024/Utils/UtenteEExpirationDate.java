package it.unimib.sd2024.Utils;

import it.unimib.sd2024.Models.Utente;

public class UtenteEExpirationDate {
    // attributi privati
    private Utente utente;
    private String expirationDate;

    // costruttori
    public UtenteEExpirationDate(Utente utente, String expirationDate){
        this.setUtente(utente);
        this.setExpirationDate(expirationDate);
    }
    
    // getter
    public Utente getUtente(){
        return this.utente;
    }
    public String getExpirationDate(){
        return this.expirationDate;
    }

    // setter
    public void setUtente(Utente utente){
        this.utente = utente;
    }
    public void setExpirationDate(String expirationDate){
        this.expirationDate = expirationDate;
    }
}