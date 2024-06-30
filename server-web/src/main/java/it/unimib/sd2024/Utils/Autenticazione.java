package it.unimib.sd2024.Utils;

import java.io.IOException;

import it.unimib.sd2024.Database.Database;
import it.unimib.sd2024.Database.DatabaseResponse;
import it.unimib.sd2024.QueryBuilder.QueryBuilder;
import it.unimib.sd2024.QueryBuilder.V1.Filter;

public class Autenticazione {
    public static boolean checkAuthentication(String token) throws InterruptedException, IOException {
        if(token == null) return false;
        DatabaseResponse risposta = new Database().ExecuteQuery(QueryBuilder.V1().FIND().setCollection("tokens").filter(new Filter().add("token", token)));
        return (risposta.getDetectedDocumentsCount() == 1);
    }
}