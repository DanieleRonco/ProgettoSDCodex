package it.unimib.sd2024.Resources;

import java.io.IOException;

import it.unimib.sd2024.Database.Database;
import it.unimib.sd2024.Database.DatabaseResponse;
import it.unimib.sd2024.QueryBuilder.QueryBuilder;
import it.unimib.sd2024.QueryBuilder.V1.Filter;
import it.unimib.sd2024.Models.*;
import it.unimib.sd2024.Utils.Autenticazione;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Rappresenta la risorsa "domain" in "http://localhost:8080/domain".
 */
@Path("domain")
public class DominioResources {
    // Attributi privati statici...
    private static Database comunicazioneDatabase;
    private static Jsonb jsonb;

    // Inizializzazione statica.
    static {
        try {
            Database comunicazioneDatabase = new Database();
            jsonb = JsonbBuilder.create();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Implementazione di GET "/domain/available/{nome}/{TLD}"
     * @throws IOException 
     * @throws InterruptedException 
     */
    @Path("/available/{nome}/{TLD}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAvailable(@HeaderParam("Bearer") String token, @PathParam("nome") String nome, @PathParam("TLD") String TLD) throws InterruptedException, IOException {
        // verificare se un dominio è disponibile
        // 0. si verifica l'autenticazione
        // 1. si verifica se nome+TLD sono presenti in 'domains'
        //  1.1 presente, si verifica lo stato
        //    1.1.1 se 'active' o 'acquiring', non è disponibile
        //    1.1.2 se 'expired', è disponibile
        //  1.2 non presente, è disponibile
        // -----------------------------------------------------

        if(Autenticazione.checkAuthentication(token)){
            // autenticato
            DatabaseResponse rispostaPresente = comunicazioneDatabase.ExecuteQuery(QueryBuilder.V1().FIND().setCollection("domains").filter(new Filter().add("name", nome).add("TLD", TLD)));
            if(rispostaPresente.getDetectedDocumentsCount() == 1){
                // presente
                // ottengo il dominio (tutto) e ne verifico lo stato
                var dominio = jsonb.fromJson(rispostaPresente.getRetrievedDocuments()[0], Dominio.class);
                if(!dominio.getStato().equals("expired")){
                    // 'active' o 'acquiring' -> non disponibile
    
                    // si restituisce (nome, cognome, email) del proprietario e (data di scadenza)
                    // 1. si ottiene la registrazione
                    // 2. si ottiene la data di scadenza (dalla registrazione)
                    // 3. si ottiene il proprietario
    
                    DatabaseResponse rispostaRegistrazione = comunicazioneDatabase.ExecuteQuery(QueryBuilder.V1().FIND().setCollection("registered").filter(new Filter().add("name", nome).add("TLD", TLD)));
                    if(!rispostaRegistrazione.isErrorResponse()){
                        Registrazione registrazione = jsonb.fromJson(rispostaRegistrazione.getRetrievedDocuments()[0], Registrazione.class);
                        DatabaseResponse rispostaUtente = comunicazioneDatabase.ExecuteQuery(QueryBuilder.V1().FIND().setCollection("users").filter(new Filter().add("email", registrazione.getUtenteEmail())));
                        if(!rispostaUtente.isErrorResponse()){
                            Utente utente = jsonb.fromJson(rispostaUtente.getRetrievedDocuments()[0], Utente.class);
                            return Response.status(200).entity(jsonb.toJson(new UtenteERegistrazione(utente, registrazione))).build();
                        } else {
                            return Response.status(500).build();
                        }
                    } else {
                        return Response.status(500).build();
                    }
                } else {
                    // 'expired' -> disponibile
                    return Response.status(200).entity("dominio disponibile").build();
                }
            } else {
                // non presente, è disponibile
                return Response.status(200).entity("dominio disponibile").build();
            }
        } else {
            // non autenticato
            return Response.status(401).build();
        }
    }

    /**
     * Implementazione di GET "/domain/registeredDomains"
     * @throws IOException 
     * @throws InterruptedException 
     */
    @Path("/registeredDomains")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRegisteredDomains(@HeaderParam("Bearer") String token) throws InterruptedException, IOException {
        // ottenere l'elenco dei domini registrati
        // 0. si verifica l'autenticazione
        // 1. si ottengono tutti i domini registrati
        //  1.1 nessun errore
        //  1.2 errore
        // -----------------------------------------

        if(Autenticazione.checkAuthentication(token)){
            // autenticato
            DatabaseResponse rispostaDomini = comunicazioneDatabase.ExecuteQuery(QueryBuilder.V1().FIND().setCollection("registered"));
            if(!rispostaDomini.isErrorResponse()){
                // nessun errore
                // TODO: verificare cosa ritorna
                return Response.status(200).entity(rispostaDomini.getRetrievedDocuments()).build();
            } else {
                // errore
                return Response.status(500).build();
            }
        } else {
            // non autenticato
            return Response.status(401).build();
        }
    }
}