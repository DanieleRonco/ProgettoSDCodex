package it.unimib.sd2024.Utils;

import java.util.Set;
import java.util.regex.Pattern;
import java.util.HashSet;

public class ValidazioneDominio {
    
    // si definisce una regex per un nome di dominio valido
    private static final Pattern PATTERN_NOME = Pattern.compile(
            "^[a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?$"
    );

    // si definisce un set di TLD validi
    private static final Set<String> TLD_VALIDI = new HashSet<>();

    static {
        // si popola il set con TLD validi
        String[] tlds = { "com", "org", "net", "edu", "gov", "mil", "int" };
        for (String tld : tlds) {
            TLD_VALIDI.add(tld);
        }
    }

    public static boolean isValidDomain(String nome, String tld) {
        return isValidDomainName(nome) && isValidTld(tld);
    }

    private static boolean isValidDomainName(String nome) {
        if (nome == null || nome.length() > 63) {
            return false;
        }
        return PATTERN_NOME.matcher(nome).matches();
    }

    private static boolean isValidTld(String tld) {
        if (tld == null || tld.length() < 2 || tld.length() > 6) {
            return false;
        }
        return TLD_VALIDI.contains(tld.toLowerCase());
    }
}