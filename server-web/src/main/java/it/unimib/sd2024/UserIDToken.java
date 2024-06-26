package it.unimib.sd2024;

public class UserIDToken {
    // attributi privati
    private String UserID;
    private String token;

    // costruttori
    public UserIDToken(String UserID, String token){
        setUserID(UserID);
        setToken(token);
    }

    // getter
    public String getUserID(){
        return this.UserID;
    }
    public String getToken(){
        return this.token;
    }

    // setter
    public void setUserID(String UserID){
        this.UserID = UserID;
    }
    public void setToken(String token){
        this.token = token;
    }
}