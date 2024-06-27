package it.unimib.sd2024.Controller;

import it.unimib.sd2024.Logger.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Controller {
    private Logger log;
    public Controller(Logger log) {
        this.log = log;
    }

    public String handleRequest(String request) {
        return "Hello, World!";
    }
}
