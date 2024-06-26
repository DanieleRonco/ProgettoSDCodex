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
import it.unimib.sd2024.Database.Database;
import it.unimib.sd2024.Database.DatabaseResponse;
import it.unimib.sd2024.QueryBuilder.Query;
import it.unimib.sd2024.QueryBuilder.queryOperation;
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
 * Rappresenta la risorsa "domain" in "http://localhost:8080/domain".
*/
@Path("domain")
public class DominioResources {
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
     * Implementazione di GET "/domain/registeredDomains".
     * @throws IOException 
     * @throws InterruptedException 
    */
    @Path("/registeredDomain")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRegisteredDomains() throws InterruptedException, IOException {
        // 1. si chiede l'elenco dei domini registrati in 'purchases'
        //  1.1 nessun errore, si restituisce la lista di acquisti richiedendo per ogni dominio il nome
        //  1.2 errore
        
        Query richiesta1 = new Query(queryOperation.FIND, "purchases", "");
        DatabaseResponse risposta1 = comunicazioneDatabase.ExecuteQuery(richiesta1);
        if(!risposta1.isErrorResponse()){
            // nessun errore
            // TODO: richiedere per ogni dominio il nome
            String returnJSON = String.join("\n", risposta1.getRetrievedDocuments());
            return Response.status(200).entity(returnJSON).build();
        } else {
            // errore
            return Response.status(500).build();
        }
    }
}