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
 * Rappresenta la risorsa "example" in "http://localhost:8080/users".
*/
@Path("users")
public class UtenteResources {
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
     * Implementazione di GET "/example/register".
     * @throws IOException 
     * @throws InterruptedException 
    */
    @Path("/register")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerUser(Utente utente) throws InterruptedException, IOException {
        String nome = utente.getNome();
        String cognome = utente.getCognome();
        String email = utente.getEmail();
        String password = utente.getPassword();

        // si verifica se l'utente è già registrato (= email già presente)
        Query richiesta = new Query(queryOperation.FIND, "users", "{\"email\":\"" + email + "\"}");
        DatabaseResponse risposta = comunicazioneDatabase.ExecuteQuery(richiesta);

        if(risposta.getDetectedDocumentsCount() == 0){
            // email non presente
            Query inserimento = new Query(queryOperation.INSERT, "users", "{\"nome\":\"" + nome + "\", \"cognome\":\"" + cognome + "\", \"email\":\"" + email + "\", \"password\":\"" + password + "\"}");
            risposta = comunicazioneDatabase.ExecuteQuery(inserimento);
            if((risposta.isErrorResponse() == false) & (risposta.getAffectedDocumentsCount() == 1))
                return Response.status(201).build();
            else
                return Response.status(500).build();
        } else {
            // email presente
            return Response.status(400).entity("email gia' registrata").build();
        }
    }
}