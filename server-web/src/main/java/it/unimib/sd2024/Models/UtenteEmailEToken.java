package it.unimib.sd2024.Models;

public class UtenteEmailEToken {
    // attributi privati
    private String userEmail;
    private String token;

    // costruttori
    public UtenteEmailEToken(String userEmail, String token){
        setUserEmail(userEmail);
        setToken(token);
    }

    // getter
    public String getUserEmail(){
        return this.userEmail;
    }
    public String getToken(){
        return this.token;
    }

    // setter
    public void setUserEmail(String userEmail){
        this.userEmail = userEmail;
    }
    public void setToken(String token){
        this.token = token;
    }
}