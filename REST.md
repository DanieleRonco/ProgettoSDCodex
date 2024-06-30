# Progetto Sistemi Distribuiti 2023-2024 - API REST

## `/users/register`
### POST
**Descrizione**: consente la registrazione di un Utente.

**Parametri**: nessun parametro.

**Header**: `Content-Type` con il valore `application/json`, in aggiunta a quelli già impostati automaticamente.

**Body Richiesta**: rappresentazione in formato JSON di un Utente, con i seguenti campi: `nome`, `cognome`, `email`, `password`.

**Risposta**: nessun contenuto.

**Codici di Stato Restituiti**:

    201 Created, quando un Utente viene registrato correttamente.

    400 Bad Request, quando l'email specificata è già stata registrata.



## `/users/login`
### POST
**Descrizione**: consente ad un Utente di effettuare l'accesso, ottenendo un token di sessione.

**Parametri**: nessun parametro.

**Header**: `Content-Type` con il valore `application/json`, in aggiunta a quelli già impostati automaticamente.

**Body Richiesta**: rappresentazione in formato JSON di un Utente, con solo i seguenti campi: `email`, `password`.

**Risposta**: token di sessione.

**Codici di Stato Restituiti**:
    
    200 Ok, quando l'accesso è avvenuto correttamente e viene restituito il token di sessione.
    
    400 Bad Request, quando l'email specificata non è registrata oppure la password specificata non è corretta.



## `/order/myOrders`
### GET
**Descrizione**: consente di ottenere l'elenco degli ordini di un Utente.

**Parametri**: nessun parametro.

**Header**: `Authorization` per il token di sessione, in aggiunta a quelli già impostati automaticamente.

**Body Richiesta**: nessun contenuto.

**Risposta**: array JSON contenente l'elenco degli ordini di un Utente, con solo i seguenti campi: `nome`, `TLD`, `data`, `oggetto`, `quota`.

**Codici di Stato Restituiti**:
    
    200 Ok, quando viene restituito l'elenco degli ordini di un Utente.
    
    401 Unauthorized, quando l'Utente non ha effettuato l'accesso.



## `/available/{nome}/{TLD}`
### GET
**Descrizione**: consente di verificare se un Dominio è disponibile, altrimenti restituisce le informazioni dell'Utente possessore del Dominio e la data di scadenza della Registrazione del Dominio.

**Parametri**: `nome` che rappresenta il nome del Dominio, `TLD` che rappresenta il Top Level Domain del Dominio.

**Header**: `Authorization` per il token di sessione, in aggiunta a quelli già impostati automaticamente.

**Body Richiesta**: nessun contenuto.

**Risposta**: Se il Dominio è disponibile, nessun contenuto. Se il Dominio non è disponibile, rappresentazione in formato JSON di un Utente, con solo i seguenti campi: `nome`, `cognome`, `email`, e `data di scadenza` della Registrazione del Dominio.

**Codici di Stato Restituiti**:
    
    200 Ok, quando viene restituita la disponibilità del Dominio.

    400 Bad Request, quando viene indicato un Dominio (nome o TLD) non valido.
    
    401 Unauthorized, quando l'Utente non ha effettuato l'accesso.



## `/registered`
### GET
**Descrizione**: consente di ottenere l'elenco di tutti i Domini registrati.

**Parametri**: nessun parametro.

**Header**: `Authorization` per il token di sessione, in aggiunta a quelli già impostati automaticamente.

**Body Richiesta**: nessun contenuto.

**Risposta**: array JSON contenente l'elenco di tutti i Domini registrati, con solo i seguenti campi: `nome`, `TLD`, `data di registrazione`, `data di scadenza`.

**Codici di Stato Restituiti**:

    200 Ok, quando viene restituito l'elenco di tutti i Domini registrati.
    
    401 Unauthorized, quando l'Utente non ha effettuato l'accesso.



## `/register/{nome}/{TLD}`
### POST
**Descrizione**: consente la registrazione di un Dominio da parte di un Utente.

**Parametri**: `nome` che rappresenta il nome del Dominio, `TLD` che rappresenta il Top Level Domain del Dominio.

**Header**: `Content-Type` con il valore `application/json` e `Authorization` per il token di sessione, in aggiunta a quelli già impostati automaticamente.

**Body Richiesta**: rappresentazione in formato JSON di `tempo`, che rappresenta la durata della registrazione del Dominio, `quantita`, che rappresenta la quota pagata per la registrazione del Dominio, e delle informazioni di una Carta, quali `numero`, `scadenza`, `cvv`, `nome intestatario`, `cognome intestatario`.

**Risposta**: nessun contenuto.

**Codici di Stato Restituiti**:
    
    200 Ok, quando il Dominio viene registrato correttamente.
    
    400 Bad Request, quando viene indicato un Dominio (nome e TLD) non valido oppure quando durata e quantita non sono validi.
    
    401 Unauthorized, quando l'Utente non ha effettuato l'accesso.
    
    409 Conflict, quando il Dominio è già in fase di acquisto.



## `/renewal/{nome}/{TLD}`
### POST
**Descrizione**: consente il rinnovo di un Dominio da parte di un Utente.

**Parametri**: `nome` che rappresenta il nome del Dominio, `TLD` che rappresenta il Top Level Domain del Dominio.

**Header**: `Content-Type` con il valore `application/json` e `Authorization` per il token di sessione, in aggiunta a quelli già impostati automaticamente.

**Body Richiesta**: rappresentazione in formato JSON di `tempo`, che rappresenta il tempo di estensione della registrazione del Dominio, `quantita`, che rappresenta la quota pagata per il rinnovo del Dominio, e delle informazioni di una Carta, quali `numero`, `scadenza`, `cvv`, `nome intestatario`, `cognome intestatario`.

**Risposta**: nessun contenuto.

**Codici di Stato Restituiti**:
    
    200 Ok, quando il Dominio viene rinnovato correttamente.
    
    400 Bad Request, quando viene indicato un Dominio (nome e TLD) non valido oppure quando aggiunta e quantita non sono validi.
    
    401 Unauthorized, quando l'Utente non ha effettuato l'accesso.