package it.unimib.sd2024.Resources.httpModels;

import it.unimib.sd2024.Models.Carta;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbTransient;

public class HTTPToken {
    private String token;
    private String email;

    public HTTPToken() {
    }

    public HTTPToken(String token, String email) {
        this.token = token;
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static class HTTPTempoQuantitaCarta {
        // attributi privati
    //    private int tempo;
    //    private float quantita;
    //    @JsonbTransient
    //    private Carta carta;
    //    private String numero;
    //    private String scadenza;
    //    private String cvv;
    //    private String nome;
    //    private String cognome;
    //
    //
    //    // costruttori
    //    public TempoQuantitaCarta(int tempo, float quantita, String numero, String scadenza, String cvv, String nome, String cognome) {
    //        this.setTempo(tempo);
    //        this.setQuantita(quantita);
    //        this.carta = new Carta(numero, scadenza, cvv, nome, cognome);
    //    }

        @JsonbProperty("tempo")
        private int tempo;

        @JsonbProperty("quantita")
        private float quantita;

        @JsonbTransient
        private Carta carta;

        @JsonbProperty("numero")
        private String numero;

        @JsonbProperty("scadenza")
        private String scadenza;

        @JsonbProperty("ccv")
        private String cvv;

        @JsonbProperty("nome")
        private String nome;

        @JsonbProperty("cognome")
        private String cognome;

        // default constructor for JSON-B
        public HTTPTempoQuantitaCarta() {
        }

        // costruttori
        @JsonbCreator
        public HTTPTempoQuantitaCarta(
                @JsonbProperty("tempo") int tempo,
                @JsonbProperty("quantita") float quantita,
                @JsonbProperty("numero") String numero,
                @JsonbProperty("scadenza") String scadenza,
                @JsonbProperty("ccv") String cvv,
                @JsonbProperty("nome") String nome,
                @JsonbProperty("cognome") String cognome
        ) {
            this.setTempo(tempo);
            this.setQuantita(quantita);
            this.carta = new Carta(numero, scadenza, cvv, nome, cognome);
        }


        // getter
        public int getTempo() {
            return this.tempo;
        }

        // setter
        public void setTempo(int tempo) {
            this.tempo = tempo;
        }

        public float getQuantita() {
            return this.quantita;
        }

        public void setQuantita(float quantita) {
            this.quantita = quantita;
        }

        public Carta getCarta() {
            return this.carta;
        }
    //
    //    public void setCarta(Carta carta) {
    //        this.carta = carta;
    //    }

        public void setNumero(String numero) {
            this.carta.setNumero(numero);
        }

        public void setScadenza(String scadenza) {
            this.carta.setScadenza(scadenza);
        }

        public void setCVV(String cvv) {
            this.carta.setCVV(cvv);
        }

        public void setNome(String nome) {
            this.carta.setNome(nome);
        }

        public void setCognome(String cognome) {
            this.carta.setCognome(cognome);
        }
    }
}
