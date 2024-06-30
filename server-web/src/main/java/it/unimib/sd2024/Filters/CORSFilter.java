package it.unimib.sd2024.Filters;

import java.io.IOException;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CORSFilter implements ContainerResponseFilter {
    @Override
    public void filter(final ContainerRequestContext requestContext,
                       final ContainerResponseContext response) throws IOException {
        //Allow-Origin indica quali origini possono fare richieste al server, indica alle CORS
        //i valori consentiti dall'header "Origin", in questo caso é stato scelto di permettere il passagio
        //ad ogni origine siccome il programma seguente é un API e riteniamo corretto permettere a diversi
        //sistemi di usare le nostre funzionalitá
        response.getHeaders().add("Access-Control-Allow-Origin", "*");

        //Allow-Methods indica quali metodi HTTP possono essere utilizzati in una richiesta,
        //in questo caso i metodi consentiti sono GET, POST, PUT, DELETE, OPTIONS, HEAD
        //GET POST PUT E DELETE sono obbligatori per il corretto utilizzo del sistema in quanto vi sono delle servlet
        //associate a questi metodi
        //OPTIONS é obbligatorio per permettere la preflight del browser sul controllo delle CORS
        //HEAD non é obbligatorio ma non causa problemi permettere questo metodo
        response.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");

        //Allow-Headers indica quali headers possono essere utilizzati in una richiesta, eventuali altri headers
        //in questo caso é stato permesso il passaggio di ogni tipo di header
        response.getHeaders().add("Access-Control-Allow-Headers", "*");
    }
}