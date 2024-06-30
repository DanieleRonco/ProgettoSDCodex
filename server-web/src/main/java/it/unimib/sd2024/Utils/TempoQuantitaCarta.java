package it.unimib.sd2024.Utils;

import it.unimib.sd2024.Models.Carta;

public class TempoQuantitaCarta {
    // attributi privati
    private int tempo;
    private float quantita;
    private Carta carta;

    // costruttori
    public TempoQuantitaCarta(int tempo, float quantita, Carta carta){
        this.setTempo(tempo);
        this.setQuantita(quantita);
        this.setCarta(carta);
    }

    // getter
    public int getTempo(){
        return this.tempo;
    }
    public float getQuantita(){
        return this.quantita;
    }
    public Carta getCarta(){
        return this.carta;
    }

    // setter
    public void setTempo(int tempo){
        this.tempo = tempo;
    }
    public void setQuantita(float quantita){
        this.quantita = quantita;
    }
    public void setCarta(Carta carta){
        this.carta = carta;
    }
}