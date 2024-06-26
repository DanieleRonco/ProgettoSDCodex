package it.unimib.sd2024;

public class Ordine {
    // attributi privati
    private String IDDominio;
    private String IDUtente;
    private String dataOrdine;
    private String oggetto;

    // costruttori
    public Ordine(String IDDominio, String IDUtente, String dataOrdine, String oggetto){
        setIDDominio(IDDominio);
        setIDUtente(IDUtente);
        setDataOrdine(dataOrdine);
        setOggetto(oggetto);
    }

    // getter
    public String getIDDominio(){
        return this.IDDominio;
    }
    public String getIDUtente(){
        return this.IDUtente;
    }
    public String getDataOrdine(){
        return this.dataOrdine;
    }
    public String getOggetto(){
        return this.oggetto;
    }

    // setter
    public void setIDDominio(String IDDominio){
        this.IDDominio = IDDominio;
    }
    public void setIDUtente(String IDUtente){
        this.IDUtente = IDUtente;
    }
    public void setDataOrdine(String dataOrdine){
        this.dataOrdine = dataOrdine;
    }
    public void setOggetto(String oggetto){
        this.oggetto = oggetto;
    }
}
