package it.unimib.sd2024;

public class Carta {
    // attributi privati
    private String numero;
    private String scadenza;
    private String cvv;
    private String nome;
    private String cognome;

    // costruttori
    public Carta(String numero, String scadenza, String cvv, String nome, String cognome){
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
}