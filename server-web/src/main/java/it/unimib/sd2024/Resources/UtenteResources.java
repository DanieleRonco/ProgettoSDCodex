package it.unimib.sd2024;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import it.unimib.sd2024.Database.Database;
import it.unimib.sd2024.Database.DatabaseResponse;
import it.unimib.sd2024.QueryBuilder.Query;
import it.unimib.sd2024.QueryBuilder.QueryOperationType;
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

// import UUID
import java.util.UUID;

/**
 * Rappresenta la risorsa "users" in "http://localhost:8080/users".
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
     * Implementazione di POST "/users/register".
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
        Query richiesta = new Query(QueryOperationType.FIND, "users", "{\"email\":\"" + email + "\"}");
        DatabaseResponse risposta = comunicazioneDatabase.ExecuteQuery(richiesta);

        if(risposta.getDetectedDocumentsCount() == 0){
            // email non presente
            Query inserimento = new Query(QueryOperationType.INSERT, "users", "{\"nome\":\"" + nome + "\", \"cognome\":\"" + cognome + "\", \"email\":\"" + email + "\", \"password\":\"" + password + "\"}");
            risposta = comunicazioneDatabase.ExecuteQuery(inserimento);
            if((!risposta.isErrorResponse()) && (risposta.getAffectedDocumentsCount() == 1))
                return Response.status(201).build();
            else
                return Response.status(500).build();
        } else {
            // email presente
            return Response.status(400).entity("email gia' registrata").build();
        }
    }

    /**
     * Implementazione di POST "/users/login".
    */
    @Path("/login")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response loginUser(Utente utente) throws InterruptedException, IOException {
        String email = utente.getEmail();
        String password = utente.getPassword();

        // si verifica se le credenziali sono corrette (email esistente e password corretta)
        Query richiesta1 = new Query(QueryOperationType.FIND, "users", "{\"email\":\"" + email + "\"}");
        DatabaseResponse risposta1 = comunicazioneDatabase.ExecuteQuery(richiesta1);
        if(risposta1.getDetectedDocumentsCount() == 1){
            // email presente
            Query richiesta2 = new Query(QueryOperationType.FIND, "users", "{\"email\":\"" + email + "\", \"password\":\"" + password + "\"}");
            DatabaseResponse risposta2 = comunicazioneDatabase.ExecuteQuery(richiesta2);
            if(risposta1.getDetectedDocumentsCount() == 1){
                // password corretta
                // TODO: UPDATE in 'tokens' passando ID e token
                // ID
                var jsonb = JsonbBuilder.create();
                Utente utenteRisposta2;
                try {
                    var utenteRisposta2JSON = jsonb.fromJson(risposta2.getRetrievedDocuments()[0], Utente.class);
                    utenteRisposta2 = new Utente(utenteRisposta2JSON.getID(), 
                                                utenteRisposta2JSON.getNome(), 
                                                utenteRisposta2JSON.getCognome(), 
                                                utenteRisposta2JSON.getEmail(), 
                                                utenteRisposta2JSON.getPassword());
                } catch (JsonbException e) {
                    return Response.status(500).build();
                }
                // token 'String uuid = UUID.randomUUID().toString();'
                Query richiesta3 = new Query(); // <-- TODO: da modificare, per creare la query di UPDATE
                DatabaseResponse risposta3 = comunicazioneDatabase.ExecuteQuery(richiesta3);
                if(!risposta3.isErrorResponse())
                    return Response.status(200).build();
                else
                    return Response.status(500).build();
            } else {
                // password non corretta
                return Response.status(400).entity("password non corretta").build();
            }
        } else {
            // email non presente
            return Response.status(400).entity("email non registrata").build();
        }
    }
}