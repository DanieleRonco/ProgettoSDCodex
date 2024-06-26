package it.unimib.sd2024.Controller;

import it.unimib.sd2024.Logger.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Controller {


    private static class Handler extends Thread {
        private Socket client;
        private Logger log;

        public Handler(Socket client, Logger log) {
            this.log = log;
            this.client = client;
        }

        public void run() {
            log.Debug("Connected to " + client.getRemoteSocketAddress());
            try {
                var out = new PrintWriter(client.getOutputStream(), true);
                var in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    if (".".equals(inputLine)) {
                        out.println("bye");
                        break;
                    }
                    out.println(inputLine);
                }

                in.close();
                out.close();
                client.close();
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }
}
