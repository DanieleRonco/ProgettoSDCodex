package it.unimib.sd2024.Models;

public class UtenteERegistrazione {
    public Utente utente;
    public Registrazione regisrazione;

    public UtenteERegistrazione(Utente utente, Registrazione registrazione){
        this.utente = utente;
        this.regisrazione = registrazione;
    }
}