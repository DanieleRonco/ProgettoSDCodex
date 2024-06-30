# Progetto Sistemi Distribuiti 2023-2024 - TCP

## introduzione

il protocollo si basa sull'invio di stringhe (protocollo testuale) tra un server web, che opera come client, ed un database, che opera come server.
Il protocollo non é ispirato ad altri protocolli esistenti, abbiamo cercato di strutturare in maniera simile le query come mongodb ma non abbiamo approfondito il protocollo di comunicazione di questo DBMS.

### client (server web)

il client instaura una connessione con il database tramite una socket TCP.

#### operazioni

1. il client manda al database una stringa che rappresenta il comando da eseguire sul DBMS.
2. il client riceve dal database una stringa contenente la risposta alla richiesta precedente.

### server (database)

il database, di default rimane in ascolto sulla porta 3030, ma puó essere cambiata usando la flag `--port` o `-p`
inoltre é possibile cambiare il livello di log usando la flag `--log` o `-l`, i valori permessi sono

- `DEBUG`: logga tutte le operazioni con maggiori dettagli ma troppo prolisso per un utilizzo normale
- `INFO`: logga le operazioni principali
- `WARN`: logga solo i warning
- `ERROR`: logga solo gli errori

#### operazioni

1. riceve una stringa rappresentante un comando costituito da diversi argomenti
2. processa il comando
3. invia una stringa di risposta al client

### richiesta

la richiesta viene composta lato client per mezzo di un QueryBuilder

#### struttura

la richiesta é formata dai seguenti campi:

- `version`: versione del protocollo, attualmente é `V1`, permette di rendere il protocollo flessibile a cambiamenti futuri, **campo obbligatorio**
- `operation`: operazione da eseguire, meggiori dettagli nella sezione `operazioni` **campo obbligatorio**
- `collectionName`: nome della collezione su cui eseguire l'operazione **campo obbligatorio solo per le operazioni che modificano delle collezioni**
- `document`: stringa in formato JSON rappresentante il documento su cui eseguire l'operazione **campo obbligatorio solo per l'operazione `insert`**
- `filter`: stringa in formato JSON rappresentante una lista di filtri, maggiori dettagli nella sezione `filtri` **campo opzionale**
- `update`: stringa in formato JSON rappresentante il documento con i valori da aggiornare, maggiori dettagli nella sezione `aggiornamento` **campo obbligatorio solo per l'operazione `update`**

#### operazioni

- `create`: creazione di una collezione
- `delete`: eliminazione di uno o piú documenti all'interno di una collezione
- `drop`: eliminazione di una collezione
- `find`: ricerca di uno o piú documenti all'interno di una collezione che soddisfano dei filtri
- `insert`: inserimento di un documento all'interno di una collezione
- `ping`: ping verso il database (non necessita di parametri, utile per verificare la connessione)
- `update`: aggiornamento di uno o piú documenti all'interno di una collezione

##### CREATE:

Permette la creazione di una collezione
Struttura di una richiesta:

```json
{
  "version": "V1",
  "operation": "CREATE",
  "collectionName": "nomeCollezione"
}
```

##### DELETE:

Permette l'elinimazione di uno o piú documenti all'interno di una collezione

Struttura di una richiesta:

```json
{
  "version": "V1",
  "operation": "DELETE",
  "collectionName": "nomeCollezione",
  "filter": [
    {
      "comparison": "EQUAL",
      "key": "chiave",
      "value": "valore",
      "valueType": "String"
    }
  ]
}
```

##### DROP:

Permette l'eliminazione di una collezione

Struttura di una richiesta:

```json
{
  "version": "V1",
  "operation": "DROP",
  "collectionName": "nomeCollezione"
}
```

##### FIND:

Permette di ottenere i documenti che soddisfano parametri della ricerca (filtri)

Struttura di una richiesta:

```json
{
  "version": "V1",
  "operation": "FIND",
  "collectionName": "nomeCollezione",
  "filter": [
    {
      "comparison": "EQUAL",
      "key": "chiave",
      "value": "valore",
      "valueType": "String"
    },
    {
      "comparison": "EQUAL",
      "key": "chiave",
      "value": "valore",
      "valueType": "String"
    }
  ]
}
```

##### INSERT:

Permette di inserire un documento in una collezione

Struttura di una richiesta:

```json
{
  "version": "V1",
  "operation": "INSERT",
  "collectionName": "nomeCollezione",
  "document": {
    "chiave": "valore",
    "chiave2": "valore"
  }
}
```

##### PING:

Permette di determinare lo stato della connesione con il database

Struttura di una richiesta:

```json
{
  "version": "V1",
  "operation": "PING"
}
```

##### UPDATE:

Permette di aggiornare un Documento in una Collezione

Struttura di una richiesta:

```json
{
  "version": "V1",
  "operation": "UPDATE",
  "collectionName": "nomeCollezione",
  "filter": [
    {
      "comparison": "EQUAL",
      "key": "chiave",
      "value": "valore",
      "valueType": "String"
    }
  ],
  "update": [
    {"key": "chiave", "value": "nuovo valore"},
    {"key": "chiave2", "value": "nuovo valore"}
  ]
}
```

> nota: se una chiave di update non é presente nel documento, verrá creata

#### filtri

I filtri permettono di selezionare dei documenti all'interno di una collezione per permettere di eseguire operazioni su di essi.

##### campi

- `comparison`: tipo di confronto da effettuare, i valori possibili sono:
  - `EQUAL`: uguaglianza
  - `GREATER`: maggiore
  - `GREATER_EQUAL`: maggiore o uguale
  - `LESS`: minore
  - `LESS_EQUAL`: minore o uguale
  - `NOT_EQUAL`: diverso
- `key`: chiave del documento
- `value`: valore da confrontare
- `valueType`: tipo del valore, i valori possibili sono:
  - `String`
  - `Number`
  - `Boolean` (i valori di comparison per i boolean sono `EQAUL` e `NOT_EQUAL`)
  - `Null` (i valori di comparison per i null sono `EQAUL` e `NOT_EQUAL`)

DOMANDA
volevo dire che il server può creare query utilizzando un query builder e il database le può parsare. Non avendolo fatto, non so come mettere giù la frase. Te che sai come fare, butta giù due righe, poi vedo io di sistemare la forma e tutto. è solo l'idea ecco.
RISPOSTA:
Il client puó utilizzare un QueryBuilder per semplificare la creazione di query, il query builder é una classe che permette di definire i vari campi di una query e avere controllo a compile time su alcune operazioni, come la presenza di campi obbligatori o la correttezza dei tipi di dato.
il query builder é poi in grado di convertire la definizione della query in una stringa JSON che rappresenta la query stessa, che puó essere inviata al database per essere processata.

#### aggiornamento

l'aggiornamento permette di modificare i valori di un documento all'interno di una collezione.

##### campi

- `key`: chiave del documento
- `value`: valore da aggiornare
- `valueType`: tipo del valore, i valori possibili sono:
  - `String`
  - `Number`
  - `Boolean`
  - `Null`

se una chiave non é presente nel documento, verrá creata con il valore specificato.

#### risposta

la risposta del database è una stringa che, una volta parsata, prevede sempre la seguente struttura:

- `isError`: indica se si è verificato un errore nell'esecuzione della richiesta
- `errorKind`: tipo di error verificatori, aiuta il client a capire il tipo di errore
- `message`: informazioni aggiuntive sulla risposta, può contenere informazioni sul messaggio di errore o di successo
- `affectedDocumentsCount`: numero di documenti che hanno subito modifiche (update o delete)
- `detectedDocumentsCount`: numero di documenti che soddifano le condizioni dei filtri
- `retrievedDocuments`: array di documenti restituiti in formato JSON

---

Documentare qui il protocollo su socket TCP che espone il database.

Come scritto anche nel documento di consegna del progetto, si ha completa libertà su come implementare il protoccolo e i comandi del database. Alcuni suggerimenti sono:

1. Progettare un protocollo testuale (tipo HTTP), è più semplice da implementare anche se meno efficiente.
2. Dare un'occhiata al protocollo di [Redis](https://redis.io/docs/reference/protocol-spec/). Si può prendere ispirazione anche solo in alcuni punti.

Di solito il protoccolo e i comandi del database sono due cose diverse. Tuttavia per il progetto, per evitare troppa complessità, si può documentare insieme il protocollo e i comandi implementati nel database.

La documentazione può variare molto in base al tipo di protocollo che si vuole costruire:

- Se è un protocollo testuale simile a quello di Redis, è necessario indicare il formato delle richieste e delle risposte, sia dei comandi sia dei dati.

- Se è un protocollo binario, è necessario specificare bene il formato di ogni pacchetto per le richieste e per le risposte, come vengono codificati i comandi e i dati.
