package it.unimib.sd2024.Resources.httpModels;

public class HTTPCarta {
    // attributi privati
    private String numero;
    private String scadenza;
    private String cvv;
    private String nome;
    private String cognome;

    // costruttori
    public HTTPCarta(String numero, String scadenza, String cvv, String nome, String cognome){
        setNumero(numero);
        setScadenza(scadenza);
        setCVV(cvv);
        setNome(nome);
        setCognome(cognome);
    }

    // getter
    public String getNumero(){
        return this.numero;
    }
    public String getScadenza(){
        return this.scadenza;
    }
    public String getCVV(){
        return this.cvv;
    }
    public String getNome(){
        return this.nome;
    }
    public String getCognome(){
        return this.cognome;
    }

    // setter
    public void setNumero(String numero){
        this.numero = numero;
    }
    public void setScadenza(String scadenza){
        this.scadenza = scadenza;
    }
    public void setCVV(String cvv){
        this.cvv = cvv;
    }
    public void setNome(String nome){
        this.nome = nome;
    }
    public void setCognome(String cognome){
        this.cognome = cognome;
    }

    public static class HTTPUtenteExpirationDate {
        // attributi privati
        private HTTPUtente utente;
        private String expirationDate;

        // costruttori
        public HTTPUtenteExpirationDate(HTTPUtente utente, String expirationDate){
            this.setUtente(utente);
            this.setExpirationDate(expirationDate);
        }

        // getter
        public HTTPUtente getUtente(){
            return this.utente;
        }
        public String getExpirationDate(){
            return this.expirationDate;
        }

        // setter
        public void setUtente(HTTPUtente utente){
            this.utente = utente;
        }
        public void setExpirationDate(String expirationDate){
            this.expirationDate = expirationDate;
        }
    }
}