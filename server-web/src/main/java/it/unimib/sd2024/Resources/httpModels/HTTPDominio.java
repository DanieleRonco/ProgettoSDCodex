package it.unimib.sd2024.Resources.httpModels;

public class HTTPDominio {
    // attributi privati
    private String nome;
    private String TLD;
    private String stato;
    
    // costruttori
    public HTTPDominio(String nome, String TLD, String stato){
        setNome(nome);
        setTLD(TLD);
        setStato(stato);
    }

    // getter
    public String getNome(){
        return this.nome;
    }
    public String getTLD(){
        return this.TLD;
    }
    public String getStato(){
        return this.stato;
    }

    // setter
    public void setNome(String nome){
        this.nome = nome;
    }
    public void setTLD(String TLD){
        this.TLD = TLD;
    }
    public void setStato(String stato){
        this.stato = stato;
    }
}