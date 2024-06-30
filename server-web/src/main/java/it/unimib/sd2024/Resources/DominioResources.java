package it.unimib.sd2024.Resources;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import it.unimib.sd2024.Database.Database;
import it.unimib.sd2024.Database.DatabaseResponse;
import it.unimib.sd2024.QueryBuilder.QueryBuilder;
import it.unimib.sd2024.QueryBuilder.V1.Filter;
import it.unimib.sd2024.QueryBuilder.V1.UpdateDefinition;
import it.unimib.sd2024.Models.*;
import it.unimib.sd2024.Utils.*;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
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
        // 0. si verifica se il dominio è valido
        // 0. si verifica l'autenticazione
        // 1. si verifica se nome+TLD sono presenti in 'domains'
        //  1.1 presente, si verifica lo stato
        //    1.1.1 se 'active' o 'acquiring', non è disponibile
        //    1.1.2 se 'expired', è disponibile
        //  1.2 non presente, è disponibile
        // -----------------------------------------------------

        if(!ValidazioneDominio.isValidDomain(nome, TLD))
            return Response.status(400).entity("dominio non valido").build();

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
                            // si possiede Utente e Registrazione, si crea il JSON
                            return Response.status(200).entity(jsonb.toJson(new UtenteEExpirationDate(utente, registrazione.getExpirationDate()))).build();
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
     * Implementazione di GET "/domain/registered"
     * @throws IOException 
     * @throws InterruptedException 
     */
    @Path("/registered")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRegistered(@HeaderParam("Bearer") String token) throws InterruptedException, IOException {
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
                Registrazione[] registrazioni = new Registrazione[rispostaDomini.getRetrievedDocuments().length];
                for(int i = 0; i < registrazioni.length; i++)
                    registrazioni[i] = jsonb.fromJson(rispostaDomini.getRetrievedDocuments()[i], Registrazione.class);
                return Response.status(200).entity(jsonb.toJson(registrazioni)).build();
            } else {
                // errore
                return Response.status(500).build();
            }
        } else {
            // non autenticato
            return Response.status(401).build();
        }
    }

    /**
     * Implementazione di POST "/domain/register/{nome}/{TLD}"
     * @throws IOException 
     * @throws InterruptedException 
     */
    @Path("/register/{nome}/{TLD}")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postRegister(@HeaderParam("Bearer") String token, @PathParam("nome") String nome, @PathParam("TLD") String TLD, TempoQuantitaCarta tqc) throws InterruptedException, IOException {
        // informazioni su 'dominio', 'durata' e 'quantita' in chiaro nell'header
        // informazioni su 'carta' non in chiaro nell'header ma nel body
        // 0. si verifica se il dominio è valido
        // 0. parametri non validi
        //    durata minimo 1 massimo 10 anni
        //    quantita positiva
        // 0. autenticato
        // 1. si ottiene l'email
        // 2. si verifica se il dominio è presente (si 'lock' la registrazione)
        //  2.1 presente, si verifica se lo stato è 'acquiring'
        //    2.1.1 'acquiring', impossibile completare la registrazione
        //    2.1.2 non 'acquiring', si aggiorna lo stato ad 'acquiring'
        //  2.2 non presente, si crea il dominio e si imposta lo stato ad 'acquiring'
        // 3. si aggiunge l'ordine
        // 4. si verifica se la carta è già presente
        //  4.1 non presente, si aggiunge
        // 5. si aggiunge la registrazione
        // 6. si aggiorna lo stato a 'active'
        // --------------------------------------------------------------------------

        if(!ValidazioneDominio.isValidDomain(nome, TLD))
            return Response.status(400).entity("dominio non valido").build();

        if(((tqc.getTempo() < 1) || (tqc.getTempo() > 10)) || (tqc.getQuantita() <= 0))
            return Response.status(400).entity("parametri non validi").build(); // parametri non validi
        String registrationDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String expirationDate = LocalDateTime.now().plusYears(tqc.getTempo()).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        
        if(!Autenticazione.checkAuthentication(token))
            return Response.status(401).build(); // non autenticato
        
        // si ottiene l'email
        DatabaseResponse rispostaEmail = comunicazioneDatabase.ExecuteQuery(QueryBuilder.V1().FIND().filter(new Filter().add("token", token)));
        if(rispostaEmail.isErrorResponse())
            return Response.status(500).build(); // errore
        UtenteEmailEToken emailEToken = jsonb.fromJson(rispostaEmail.getRetrievedDocuments()[0], UtenteEmailEToken.class);

        // si verifica se il dominio è presente (si 'lock' la registrazione)
        DatabaseResponse rispostaStato = comunicazioneDatabase.ExecuteQuery(QueryBuilder.V1().FIND().filter(new Filter().add("nome", nome).add("TLD", TLD)));
        if(rispostaStato.isErrorResponse())
            return Response.status(500).build(); // errore
        if(rispostaStato.getDetectedDocumentsCount() == 1){
            // presente, si verifica se lo stato è 'acquiring'
            Dominio presente = jsonb.fromJson(rispostaStato.getRetrievedDocuments()[0], Dominio.class);
            if(presente.getStato().equals("acquiring"))
                return Response.status(409).entity("impossibile completare l'operazione, dominio già in fase di acquisto").build();
            else {
                // si aggiorna lo stato ad 'acquiring'
                DatabaseResponse rispostaAggiornaStato = comunicazioneDatabase.ExecuteQuery(QueryBuilder.V1().UPDATE().setCollection("domains").filter(new Filter().add("nome", nome).add("TLD", TLD)).updateOn(new UpdateDefinition().add("stato", "acquiring")));
                if(rispostaAggiornaStato.isErrorResponse())
                    return Response.status(500).build();
            }
        } else {
            // non presente, si crea il dominio e si imposta lo stato ad 'acquiring'
            Dominio daAggiungere = new Dominio(nome, TLD, "acquiring");
            DatabaseResponse rispostaAggiuntaDominio = comunicazioneDatabase.ExecuteQuery(QueryBuilder.V1().INSERT().setCollection("domains").insert(daAggiungere));
            if(rispostaAggiuntaDominio.isErrorResponse())
                return Response.status(500).build(); // errore
        }

        // si aggiunge l'ordine
        DatabaseResponse rispostaAggiuntaOrdine = comunicazioneDatabase.ExecuteQuery(QueryBuilder.V1().INSERT().setCollection("orders").insert(new Ordine(nome, TLD, emailEToken.getUserEmail(), tqc.getCarta().getNumero(), registrationDate, "registration", tqc.getQuantita())));
        if(rispostaAggiuntaOrdine.isErrorResponse())
            return Response.status(500).build(); // errore
        
        // si verifica se la carta è già presente
        DatabaseResponse rispostaCartaPresente = comunicazioneDatabase.ExecuteQuery(QueryBuilder.V1().FIND().setCollection("cards").filter(new Filter().add("numero", tqc.getCarta().getNumero())));
        if(rispostaCartaPresente.isErrorResponse())
            return Response.status(500).build(); // errore
        if(rispostaCartaPresente.getDetectedDocumentsCount() == 0){
            // non presente, si aggiunge
            DatabaseResponse rispostaAggiuntaCarta = comunicazioneDatabase.ExecuteQuery(QueryBuilder.V1().INSERT().setCollection("cards").insert(tqc.getCarta()));
            if(rispostaAggiuntaCarta.isErrorResponse())
                return Response.status(500).build(); // errore
        }

        // si aggiunge la registrazione
        DatabaseResponse rispostaAggiuntaRegistrazione = comunicazioneDatabase.ExecuteQuery(QueryBuilder.V1().INSERT().setCollection("registered").insert(new Registrazione(nome, TLD, emailEToken.getUserEmail(), registrationDate, expirationDate)));
        if(rispostaAggiuntaRegistrazione.isErrorResponse())
            return Response.status(500).build();

        // si aggiorna lo stato a 'active'
        DatabaseResponse rispostaAggiornamentoActive = comunicazioneDatabase.ExecuteQuery(QueryBuilder.V1().UPDATE().setCollection("domains").filter(new Filter().add("nome", nome).add("TLD", TLD)).updateOn(new UpdateDefinition().add("stato", "acquiring")));
        if(rispostaAggiornamentoActive.isErrorResponse())
            return Response.status(500).build(); // errore
        
        return Response.status(200).build();
    }

    /**
     * Implementazione di POST "domain/renewal/{nome}/{TLD}"
     * @throws IOException 
     * @throws InterruptedException 
     */
    @Path("/renewal/{nome}/{TLD}")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postRenewal(@HeaderParam("Bearer") String token, @PathParam("nome") String nome, @PathParam("TLD") String TLD, TempoQuantitaCarta tqc) throws InterruptedException, IOException {
        // 0. si verifica se il dominio è valido
        // 0. autenticato
        // 1. si ottiene l'email dell'utente
        // 2. si ottiene il dominio
        // 3. si verifica se la somma porteberre ad un errore
        // 4. si verifica se il dominio è associato all'utente
        //  4.1 associato, si aggiorna 'registered', si crea l'ordine in 'orders', eventualmente si inserisce la carta in 'cards'
        //  4.2 non associato, non autorizzato
        // ----------------------------------------------------------------------------------------------------------------------

        if(!ValidazioneDominio.isValidDomain(nome, TLD))
            return Response.status(400).entity("dominio non valido").build();

        // autenticato
        if(!Autenticazione.checkAuthentication(token))
            return Response.status(401).build(); // non autenticato
        
        // si ottiene l'email dell'utente
        DatabaseResponse rispostaEmail = comunicazioneDatabase.ExecuteQuery(QueryBuilder.V1().FIND().setCollection("tokens").filter(new Filter().add("token", token)));
        if(rispostaEmail.isErrorResponse())
            return Response.status(500).build(); // errore
        UtenteEmailEToken emailEToken = jsonb.fromJson(rispostaEmail.getRetrievedDocuments()[0], UtenteEmailEToken.class);

        // si ottiene il dominio tramite registrazione
        DatabaseResponse rispostaDominio = comunicazioneDatabase.ExecuteQuery(QueryBuilder.V1().FIND().setCollection("registered").filter(new Filter().add("nome", nome).add("TLD", TLD).add("email", emailEToken.getUserEmail())));
        if(rispostaDominio.isErrorResponse())
            return Response.status(500).build(); // errore
        if(rispostaDominio.getDetectedDocumentsCount() != 1)
            return Response.status(400).entity("dominio non registrato").build(); // dominio non registrato
        Registrazione registrazione = jsonb.fromJson(rispostaDominio.getRetrievedDocuments()[0], Registrazione.class);

        // si verifica se la somma porterebbe ad un errore        
        LocalDateTime extendedDate = LocalDateTime.parse(registrazione.getExpirationDate()).plusYears(tqc.getTempo());
        LocalDateTime tenYearsDate = LocalDateTime.now().plusYears(10);
        if(extendedDate.isAfter(tenYearsDate))
            return Response.status(400).entity("si eccede il tempo massimo di registrazione (10 anni dalla data corrente)").build();

        // si verifica se il dominio è associato all'utente
        if(!emailEToken.getUserEmail().equals(registrazione.getUtenteEmail()))
            return Response.status(401).entity("non sei il proprietario del dominio").build();
        
        // si aggiorna 'registered'
        DatabaseResponse rispostaAggiornamentoData = comunicazioneDatabase.ExecuteQuery(QueryBuilder.V1().UPDATE().setCollection("registered").filter(new Filter().add("name", nome).add("TLD", TLD).add("email", emailEToken.getUserEmail())).updateOn(new UpdateDefinition().add("expirationDate", extendedDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))));
        if(rispostaAggiornamentoData.isErrorResponse())
            return Response.status(500).build(); // errore
        
        // si crea l'ordine in 'orders'
        DatabaseResponse rispostaAggiuntaOrdine = comunicazioneDatabase.ExecuteQuery(QueryBuilder.V1().INSERT().setCollection("orders").insert(new Ordine(nome, TLD, emailEToken.getUserEmail(), tqc.getCarta().getNumero(), registrazione.getRegistrationDate(), "renewal", tqc.getQuantita())));
        if(rispostaAggiuntaOrdine.isErrorResponse())
            return Response.status(500).build(); // errore
        
        // eventualmente si inserisce la carta in 'cards'
        DatabaseResponse rispostaCartaPresente = comunicazioneDatabase.ExecuteQuery(QueryBuilder.V1().FIND().setCollection("cards").filter(new Filter().add("numero", tqc.getCarta().getNumero())));
        if(rispostaCartaPresente.isErrorResponse())
            return Response.status(500).build(); // errore
        if(rispostaCartaPresente.getDetectedDocumentsCount() == 0){
            // non presente, si aggiunge
            DatabaseResponse rispostaAggiuntaCarta = comunicazioneDatabase.ExecuteQuery(QueryBuilder.V1().INSERT().setCollection("cards").insert(tqc.getCarta()));
            if(rispostaAggiuntaCarta.isErrorResponse())
                return Response.status(500).build(); // errore
        }

        // rinnovo avvenuto correttamente
        return Response.status(200).build();
    }
}