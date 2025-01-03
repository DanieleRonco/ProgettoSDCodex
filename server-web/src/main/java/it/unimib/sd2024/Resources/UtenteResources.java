package it.unimib.sd2024.Resources;

import it.unimib.sd2024.Database.Database;
import it.unimib.sd2024.Database.DatabaseResponse;
import it.unimib.sd2024.Models.Utente;
import it.unimib.sd2024.QueryBuilder.QueryBuilder;
import it.unimib.sd2024.QueryBuilder.V1.Filter;
import it.unimib.sd2024.QueryBuilder.V1.UpdateDefinition;
import it.unimib.sd2024.Resources.httpModels.HTTPToken;
import it.unimib.sd2024.Resources.httpModels.HTTPUtente;
import it.unimib.sd2024.Utils.StringHasher;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * Rappresenta la risorsa "users" in "http://localhost:8080/users".
 */
@Path("users")
public class UtenteResources {
    // Attributi privati statici...

    // Inizializzazione statica.
    static {
        try {
            new Database().ExecuteQuery(QueryBuilder.V1().CREATE("users"));
            new Database().ExecuteQuery(QueryBuilder.V1().CREATE("tokens"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Implementazione di POST "/users/register".
     *
     * @throws IOException
     * @throws InterruptedException
     */
    @Path("/register")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerUser(HTTPUtente utente) throws InterruptedException, IOException, NoSuchAlgorithmException {
        // 1. si verifica se l'email è già registrata (errore - email già registrata)
        // 2. si aggiunge l'utente (errore)
        // --------------------------------------------------------------------------
        var comunicazioneDatabase = new Database();
        DatabaseResponse rispostaEmail = comunicazioneDatabase.ExecuteQuery(QueryBuilder.V1()
                .FIND()
                .setCollection("users")
                .filter(new Filter()
                        .add("email", utente.getEmail())));

        if (rispostaEmail.isErrorResponse())
            return Response.status(500).build(); // errore
        System.out.println("found: " + rispostaEmail.getDetectedDocumentsCount());
        if (rispostaEmail.getDetectedDocumentsCount() == 1)
            return Response.status(400).entity("email già registrata").build(); // email già registrata

        var u = new Utente(utente.getNome(), utente.getCognome(), utente.getEmail(), StringHasher.hashString(utente.getPassword()));

        DatabaseResponse rispostaInserimento = comunicazioneDatabase.ExecuteQuery(QueryBuilder.V1().INSERT().setCollection("users").insert(u));
        if (rispostaInserimento.isErrorResponse())
            return Response.status(500).build(); // errore
        return Response.status(201).entity("registrazione effettuata").build(); // registrazione effettuata
    }

    /**
     * Implementazione di POST "/users/login".
     */
    @Path("/login")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response loginUser(HTTPUtente utente) throws InterruptedException, IOException {
        // accesso
        // 1. si verifica che l'email sia corretta
        //  1.1 nessun errore + presente, si verifica che la password sia corretta
        //    1.1.1 nessun errore + presente, si genera il token, si aggiorna 'tokens', si restituisce il token
        //    1.1.2 errore
        //  1.2 errore
        // ------------------------------------------------------------------------------------------
        var comunicazioneDatabase = new Database();
        DatabaseResponse rispostaEmail = comunicazioneDatabase.ExecuteQuery(QueryBuilder.V1()
                .FIND()
                .setCollection("users")
                .filter(new Filter()
                        .add("email", utente.getEmail())));

        if (rispostaEmail.isErrorResponse())
            return Response.status(500).build(); // errore
        if (rispostaEmail.getDetectedDocumentsCount() != 1)
            return Response.status(400).entity("email non registrata").build(); // email non registrata

        DatabaseResponse rispostaPassword = comunicazioneDatabase.ExecuteQuery(QueryBuilder.V1()
                .FIND()
                .setCollection("users")
                .filter(new Filter()
                        .add("password", StringHasher.hashString(utente.getPassword()))));
        if (rispostaPassword.isErrorResponse())
            return Response.status(500).build(); // errore
        if (rispostaPassword.getDetectedDocumentsCount() != 1)
            return Response.status(400).entity("password non corretta").build(); //password non corretta

        // si genera il token, si aggiorna 'tokens', si restituisce il token
        String token = UUID.randomUUID().toString();
        DatabaseResponse esisteToken = comunicazioneDatabase.ExecuteQuery(QueryBuilder.V1()
                .FIND()
                .setCollection("tokens")
                .filter(new Filter()
                        .add("email", utente.getEmail())));
        if (esisteToken.isErrorResponse())
            return Response.status(500).build(); // errore

        DatabaseResponse rispostaToken = null;
        if (esisteToken.getDetectedDocumentsCount() == 1) {
            rispostaToken = comunicazioneDatabase.ExecuteQuery(QueryBuilder.V1()
                    .UPDATE()
                    .setCollection("tokens")
                    .filter(new Filter()
                            .add("email", utente.getEmail()))
                    .updateOn(new UpdateDefinition()
                            .add("token", token)));
        } else {
            rispostaToken = comunicazioneDatabase.ExecuteQuery(QueryBuilder.V1()
                    .INSERT()
                    .setCollection("tokens")
                    .insert(new HTTPToken(token, utente.getEmail())));
        }
        if (rispostaToken.isErrorResponse())
            return Response.status(500).build(); // errore
        return Response.status(200).entity(token).build();
    }
}