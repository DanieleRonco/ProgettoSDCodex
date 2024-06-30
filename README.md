# Progetto Sistemi Distribuiti 2023-2024

ProgettoSDCodex del gruppo Codex.

Il progetto prevede lo sviluppo di un'applicazione distribuita per l'acquisto e la gestione di domini Internet. L'applicazione è composta da un Client Web per l'interazione con l'utente tramite API REST, un Server Web per la logica di gestione tramite API REST e socket TCP ed un Database documentale per memorizzare dati di acquisti e domini.

## Componenti del gruppo

* Davide Vanoncini (903214) <d.vanoncini2@campus.unimib.it>
* Daniele Roncoroni (899826) <d.roncoroni@campus.unimib.it>

## Compilazione ed esecuzione

Sia il Server Web sia il Database sono applicazioni Java gestite con Maven. All'interno delle rispettive cartelle si può trovare il file `pom.xml` in cui è presente la configurazione di Maven per il progetto. Si presuppone l'utilizzo della macchina virtuale di laboratorio, per cui nel `pom.xml` è specificato l'uso di Java 21.

Il Server Web e il Database sono dei progetti Java che utilizano Maven per gestire le dipendenze, la compilazione e l'esecuzione.

### Client Web

Per avviare il Client Web è necessario utilizzare l'estensione "Live Preview" su Visual Studio Code, come mostrato durante il laboratorio. Tale estensione espone un server locale con i file contenuti nella cartella `client-web`.

**Attenzione**: è necessario configurare CORS in Google Chrome come mostrato nel laboratorio.

### Server Web

Il Server Web utilizza Jetty e Jersey. Si può avviare eseguendo `mvn jetty:run` all'interno della cartella `server-web`. Espone le API REST all'indirizzo `localhost` alla porta `8080`.

### Database

Il database è una semplice applicazione Java. Si possono utilizzare i seguenti comandi Maven:

* `mvn clean`: per ripulire la cartella dai file temporanei,
* `mvn compile`: per compilare l'applicazione,
* `mvn exec:java`: per avviare l'applicazione (presuppone che la classe principale sia `Main.java`). Si pone in ascolto all'indirizzo `localhost` alla porta `3030`.