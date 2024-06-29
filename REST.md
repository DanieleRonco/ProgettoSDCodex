# Progetto Sistemi Distribuiti 2023-2024 - API REST

## `/users/register`
### POST
**Descrizione**:
    consente la registrazione di un Utente.
**Parametri**:
    nessun parametro.
**Header**:
    `Content-Type` con il valore `application/json`, in aggiunta a quelli già impostati automaticamente.
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
    `Content-Type` con il valore `application/json`, in aggiunta a quelli già impostati automaticamente.
**Body Richiesta**:
    rappresentazione in formato JSON di un Utente, con solo i seguenti campi: `nome`, `cognome`, `email`.
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
    array JSON contenente l'elenco degli ordini di un Utente, con solo i seguenti campi: `nome`, `TLD`, `data`, `oggetto`, `quota`.
**Codici di Stato Restituiti**:
    200 Ok
    401 Unauthorized.

## `/available/{nome}/{TLD}`
### GET
**Descrizione**:
    consente di verificare se un Dominio è disponibile, altrimenti restituisce le informazioni dell'Utente possessore del Dominio e la data di scadenza della Registrazione del Dominio.
**Parametri**:
    `nome`, rappresenta il nome del Dominio.
    `TLD`, rappresenta il Top Level Domain del Dominio.
**Header**:
    `Bearer` per il token di sessione, in aggiunta a quelli già impostati automaticamente.
**Body Richiesta**:
    nessun contenuto.
**Risposta**:
    se il Dominio è disponibile, nessun contenuto.
    se il Dominio non è disponibile, rappresentazione in formato JSON di un Utente, con solo i seguenti campi: `nome`, `cognome`, `email`, e data di scadenza della Registrazione del Dominio.
**Codici di Stato Restituiti**:
    200 Ok
    401 Unauthorized.

## `/registered`
### GET
**Descrizione**:
    consente di ottenere l'elenco di tutti i Domini registrati.
**Parametri**:
    nessun parametro.
**Header**:
    `Bearer` per il token di sessione, in aggiunta a quelli già impostati automaticamente.
**Body Richiesta**:
    nessun contenuto.
**Risposta**:
    array JSON contenente l'elenco di tutti i Domini registrati, con solo i seguenti campi: `nome`, `TLD`, `data di registrazione`, `data di scadenza`.
**Codici di Stato Restituiti**:
    200 Ok
    401 Unauthorized.

## `/register/{nome}/{TLD}/{durata}/{quantita}`
### GET
**Descrizione**:
    consente la registrazione di un Dominio da parte di un Utente.
**Parametri**:
    `nome`, rappresenta il nome del Dominio.
    `TLD`, rappresenta il Top Level Domain del Dominio.
    `durata`, rappresenta la durata della registrazione del Dominio.
    `quantita`, rappresenta la quota pagata per la registrazione del Dominio.
**Header**:
    `Content-Type` con il valore `application/json`, in aggiunta a quelli già impostati automaticamente.
    `Bearer` per il token di sessione, in aggiunta a quelli già impostati automaticamente.
**Body Richiesta**:
    rappresentazione in formato JSON di una Carta, con i seguenti campi: `numero`, `scadenza`, `cvv`, `nome intestatario`, `cognome intestatario`.
**Risposta**:
    nessun contenuto.
**Codici di Stato Restituiti**:
    200 Ok
    400 Bad Request
    401 Unauthorized
    409 Conflict.

## `/renewal/{nome}/{TLD}/{aggiunta}/{quantita}`
### GET
**Descrizione**:
    consente il rinnovo di un Dominio da parte di un Utente.
**Parametri**:
    `nome`, rappresenta il nome del Dominio.
    `TLD`, rappresenta il Top Level Domain del Dominio.
    `aggiunta`, rappresenta il tempo di estensione della registrazione del Dominio.
    `quantita`, rappresenta la quota pagata per la registrazione del Dominio.
**Header**:
    `Content-Type` con il valore `application/json`, in aggiunta a quelli già impostati automaticamente.
    `Bearer` per il token di sessione, in aggiunta a quelli già impostati automaticamente.
**Body Richiesta**:
    rappresentazione in formato JSON di una Carta, con i seguenti campi: `numero`, `scadenza`, `cvv`, `nome intestatario`, `cognome intestatario`.
**Risposta**:
    nessun contenuto.
**Codici di Stato Restituiti**:
    200 Ok
    400 Bad Request
    401 Unauthorized.