package it.unimib.sd2024;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import it.unimib.sd2024.Database.Database;
import it.unimib.sd2024.Database.DatabaseResponse;
import it.unimib.sd2024.QueryBuilder.Query;
import it.unimib.sd2024.QueryBuilder.queryOperation;
import it.unimib.sd2024.UserIDToken;
import jakarta.json.JsonException;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * Rappresenta la risorsa "order" in "http://localhost:8080/order".
 */
@Path("order")
public class OrdineResources {
    // Attributi privati statici...
    private static Database comunicazioneDatabase;

    // Inizializzazione statica.
    static {
        try {
            Database comunicazioneDatabase = new Database();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Implementazione di GET "/order/list".
     * @throws IOException 
     * @throws InterruptedException 
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrdersList() throws InterruptedException, IOException {
        // 1. si prende 'token' dall'header
        // 2. si verifica in 'tokens' se è presente il token specificato
        //  2.1 utente autenticato, si ottiene l''ID' dell'utente, si richiedono tutti gli ordini dell'utente
        //    2.1.1 no errore, si restituisce il JSON restituito andando a recuperare il nome del dominio
        //    2.1.2 errore
        //  2.2 utente non autenticato

        // --- TODO: ottenere il token dall'header --- 
        String token = "";
        
        Query richiesta1 = new Query(queryOperation.FIND, "tokens", "{\"token\":\""+ token + "\"}");
        DatabaseResponse risposta1 = comunicazioneDatabase.ExecuteQuery(richiesta1);
        if(risposta1.getDetectedDocumentsCount() == 1){
            // utente autenticato
            // TODO: ottenere l'ID
            // mi aspetto "{"UserID":"...", "token":"..."}"
            var jsonb = JsonbBuilder.create();
            UserIDToken idToken;
            try {
                // TODO: sostituire all'ID del dominio il suo nome, quindi per ogni documento richiedere 
                // il nome del dominio e comporre la risposta
                var idTokenRisposta1JSON = jsonb.fromJson(risposta1.getRetrievedDocuments()[0], UserIDToken.class);
                idToken = new UserIDToken(idTokenRisposta1JSON.getUserID(), idTokenRisposta1JSON.getToken());
                } catch (JsonbException e) {
                    return Response.status(500).build();
                }

            // l'ID dell'Utente lo ho indicato come 'UserID'
            Query richiesta2 = new Query(queryOperation.FIND, "orders", "{\"UserID\":\"" + idToken.getUserID() + "\"");
            DatabaseResponse risposta2 = comunicazioneDatabase.ExecuteQuery(richiesta2);
            if(!risposta2.isErrorResponse()){
                String returnJSON = String.join("\n", risposta2.getRetrievedDocuments());
                return Response.status(200).entity(returnJSON).build();
            } else {
                return Response.status(500).build();
            }
        } else {
            // utente non autenticato
            return Response.status(401).entity("utente non autenticato").build();
        }
    }
}