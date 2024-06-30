package it.unimib.sd2024.Resources;

import java.io.IOException;

import it.unimib.sd2024.Database.Database;
import it.unimib.sd2024.Database.DatabaseResponse;
import it.unimib.sd2024.Models.*;
import it.unimib.sd2024.QueryBuilder.QueryBuilder;
import it.unimib.sd2024.QueryBuilder.V1.Filter;
import it.unimib.sd2024.QueryBuilder.V1.UpdateDefinition;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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
            var response = comunicazioneDatabase.ExecuteQuery(QueryBuilder.V1().CREATE("users"));

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
        // 1. si verifica se l'email è già registrata (errore - email già registrata)
        // 2. si aggiunge l'utente (errore)
        // --------------------------------------------------------------------------

        DatabaseResponse rispostaEmail = new Database().ExecuteQuery(QueryBuilder.V1().FIND().setCollection("users").filter(new Filter().add("email", utente.getEmail())));
        if(rispostaEmail.isErrorResponse())
            return Response.status(500).build(); // errore
        if(rispostaEmail.getDetectedDocumentsCount() == 1)
            return Response.status(400).entity("email già registrata").build(); // email già registrata
        
        // MessageDigest digest = MessageDigest.getInstance("SHA-256");
        // byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
        // utente.setPassword(utente.getPassword())

        DatabaseResponse rispostaInserimento = new Database().ExecuteQuery(QueryBuilder.V1().INSERT().setCollection("users").insert(utente));
        if(rispostaInserimento.isErrorResponse())
            return Response.status(500).build(); // errore
        return Response.status(201).entity("registrazione effettuata").build(); // registrazione effettuata
    }

    /**
     * Implementazione di POST "/users/login".
    */
    @Path("/login")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response loginUser(Utente utente) throws InterruptedException, IOException {
        // accesso
        // 1. si verifica che l'email sia corretta
        //  1.1 nessun errore + presente, si verifica che la password sia corretta
        //    1.1.1 nessun errore + presente, si genera il token, si aggiorna 'tokens', si restituisce il token
        //    1.1.2 errore
        //  1.2 errore
        // ------------------------------------------------------------------------------------------

        DatabaseResponse rispostaEmail = comunicazioneDatabase.ExecuteQuery(QueryBuilder.V1().FIND().setCollection("users").filter(new Filter().add("email", utente.getEmail())));
        if(rispostaEmail.isErrorResponse())
            return Response.status(500).build(); // errore
        if(rispostaEmail.getDetectedDocumentsCount() != 1)
            return Response.status(400).entity("email non registrata").build(); // email non registrata
        
        DatabaseResponse rispostaPassword = comunicazioneDatabase.ExecuteQuery(QueryBuilder.V1().FIND().setCollection("users").filter(new Filter().add("password", utente.getPassword())));
        if(rispostaPassword.isErrorResponse())
            return Response.status(500).build(); // errore
        if(rispostaPassword.getDetectedDocumentsCount() != 1)
            return Response.status(400).entity("password non corretta").build(); //password non corretta
        
        // si genera il token, si aggiorna 'tokens', si restituisce il token
        String token = UUID.randomUUID().toString();
        DatabaseResponse rispostaAggiornamento = comunicazioneDatabase.ExecuteQuery(QueryBuilder.V1().UPDATE().setCollection("tokens").filter(new Filter().add("email", utente.getEmail())).updateOn(new UpdateDefinition().add("token", token)));
        if(rispostaAggiornamento.isErrorResponse())
            return Response.status(500).build(); // errore
        return Response.status(200).entity(token).build();
    }
}