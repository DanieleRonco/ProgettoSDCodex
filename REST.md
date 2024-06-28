# Progetto Sistemi Distribuiti 2023-2024 - API REST

## `/users/register`
### POST
**Descrizione**:
    consente la registrazione di un Utente.
**Parametri**:
    nessun parametro.
**Header**:
    nessun header rilevante, in aggiunta a quelli già impostati automaticamente.
**Body Richiesta**:
    rappresentazione in formato JSON di un Utente, con i seguenti campi: `nome`, `cognome`, `email`, `password`.
**Risposta**:
    nessun contenuto.
**Codici di Stato Restituiti**:
    201 Created
    400 Bad Request.

## `/users/login`
### POST
**Descrizione**:
    consente ad un Utente di effettuare l'accesso, ottenendo un token di sessione.
**Parametri**:
    nessun parametro.
**Header**:
    nessun header rilevante, in aggiunta a quelli già impostati automaticamente.
**Body Richiesta**:
    rappresentazione in formato JSON di un Utente, con i seguenti campi: `nome`, `cognome`, `email`, `password`.
**Risposta**:
    token di sessione.
**Codici di Stato Restituiti**:
    200 Ok
    400 Bad Request.

## `/order/myOrders`
### GET
**Descrizione**:
    consente di ottenere l'elenco degli ordini di un Utente.
**Parametri**:
    nessun parametro.
**Header**:
    `Bearer` per il token di sessione, in aggiunta a quelli già impostati automaticamente.
**Body Richiesta**:
    nessun contenuto.
**Risposta**:
    array JSON contenente tutti gli ordini di un Utente, con i seguenti campi: `nome`, `TLD`, `data`, `oggetto`, `quota`.
**Codici di Stato Restituiti**:
    200 Ok
    401 Unauthorized.
200 ok 
401 non autenticato


-------------------------------------------------------------------------------------------------------------------------------------------------------------------------



**Header**: solo gli header importanti. In questo caso nessuno oltre a quelli già impostati automaticamente dal client. Si può evitare di specificare gli hader riguardanti la rappresentazione dei dati (JSON).

**Body richiesta**: cosa ci deve essere nel body della richiesta (se previsto). In questo caso nulla perché non è previsto.

**Risposta**: cosa viene restituito in caso di successo. In questo caso una lista con ogni elemento un contatto con i seguenti campi: `id` (intero), `name` (stringa) e `number` (stringa).



### POST

**Descrizione**: aggiunge un contatto alla rubrica telefonica.

**Parametri**: nessuno.

**Header**: nessuno.

**Body richiesta**: singolo contatto con i campi `name` e `number`.

**Risposta**: body vuoto e la risorsa creata è indicata nell'header `Location`.

**Codici di stato restituiti**:

* 201 Created: successo.
* 400 Bad Request: c'è un errore del client (JSON, campo mancante o altro).