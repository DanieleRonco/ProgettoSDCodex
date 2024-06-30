package it.unimib.sd2024.Resources;

import java.io.IOException;

import it.unimib.sd2024.Database.Database;
import it.unimib.sd2024.Database.DatabaseResponse;
import it.unimib.sd2024.QueryBuilder.QueryBuilder;
import it.unimib.sd2024.QueryBuilder.V1.Filter;
import it.unimib.sd2024.Models.*;
import it.unimib.sd2024.Resources.httpModels.HTTPToken;
import it.unimib.sd2024.Utils.Autenticazione;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Rappresenta la risorsa "order" in "http://localhost:8080/order".
 */
@Path("order")
public class OrdineResources {
    // Attributi privati statici...
    private static Jsonb jsonb;

    // Inizializzazione statica.
    static {
        try {
            Database comunicazioneDatabase = new Database();
            comunicazioneDatabase.ExecuteQuery(QueryBuilder.V1().CREATE("orders"));
            comunicazioneDatabase.ExecuteQuery(QueryBuilder.V1().CREATE("tokens"));

            jsonb = JsonbBuilder.create();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Implementazione di GET "/order/myOrders"
     * @throws IOException 
     * @throws InterruptedException 
     */
    @Path("/myOrders")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMyOrders(@HeaderParam("Authorization") String token) throws InterruptedException, IOException {
        // ottenere l'elenco degli ordini dell'utente
        // 0. si verifica l'autenticazione
        // 1. si ottiene l'email dell'utente, dato il token
        //  1.1 nessun errore
        //    1.1.1 si ottiene la lista di ordini in base all'email
        //      1.1.1.1 nessun errore
        //      1.1.1.2 errore
        //  1.2 errore

        // verifica autenticazione
        if(Autenticazione.checkAuthentication(token)){
            Database comunicazioneDatabase = new Database();
            // autenticato
            // si ottiene l'email dell'utente, dato il token
            DatabaseResponse rispostaEmail = comunicazioneDatabase.ExecuteQuery(QueryBuilder.V1().FIND().setCollection("tokens").filter(new Filter().add("token", token)));
            if(!rispostaEmail.isErrorResponse()){
                // nessun errore
                HTTPToken emailEToken = jsonb.fromJson(rispostaEmail.getRetrievedDocuments()[0], HTTPToken.class);
                DatabaseResponse rispostaOrdini = comunicazioneDatabase.ExecuteQuery(QueryBuilder.V1().FIND().setCollection("orders").filter(new Filter().add("utenteEmail", emailEToken.getEmail())));
                if(!rispostaOrdini.isErrorResponse()){
                    // nessun errore
                    Ordine[] ordini = new Ordine[rispostaOrdini.getRetrievedDocuments().length];
                    for(int i = 0; i < ordini.length; i++)
                        ordini[i] = jsonb.fromJson(rispostaOrdini.getRetrievedDocuments()[i], Ordine.class);
                    return Response.status(200).entity(jsonb.toJson(ordini)).build();
                } else {
                    // errore
                    return Response.status(500).build();
                }
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