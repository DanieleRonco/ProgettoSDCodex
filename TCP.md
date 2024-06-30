# Progetto Sistemi Distribuiti 2023-2024 - TCP

## Introduzione

Il protocollo si basa sull'invio di stringhe (protocollo testuale) tra un Server Web, che opera come Client, ed un Database, che opera come Server.
Il protocollo non è ispirato ad altri protocolli esistenti. Si è cercato di strutturare le query in maniera simile a MongoDB, ma non si è approfondito ulteriormente il protocollo di comunicazione di questo DBMS.

### Client (Server Web)

Il Client instaura una connessione con il Database tramite una socket TCP.

#### Operazioni

1. Il Client invia al Database una stringa rappresentante il comando da eseguire sul DBMS.
2. Il Client riceve dal Database una stringa contenente la risposta alla richiesta precedente.

### Server (Database)

Normalmente, il Database rimane in ascolto su localhost:3030, che si puó cambiare la porta utilizzando la flag `--port` o `-p` e l'hostname con `--hostname` o `-h`.
Inoltre, è possibile cambiare il livello di log usando la flag `--log` o `-l`, i valori permessi sono:

- `DEBUG`: logga tutte le operazioni con maggiori dettagli, ma troppo prolisso per un utilizzo normale
- `INFO`: logga le operazioni principali
- `WARN`: logga solo i warning
- `ERROR`: logga solo gli errori

#### Operazioni

1. Riceve una stringa rappresentante un comando costituito da diversi argomenti
2. Processa il comando
3. Invia una stringa di risposta al client

### Richiesta

Una richiesta viene composta dal Client per mezzo di un QueryBuilder. Un QueryBuilder è una classe che consente di definire i vari campi di una query e di avere controllo a compile time in merito ad alcune operazioni, come la presenza di campi obbligatori o la correttezza dei tipi di dato.
Inoltre, il QueryBuilder è in grado di convertire la definizione della query in una stringa JSON che rappresenta la query stessa, che può essere inviata al Database per essere processata.

#### Struttura

Una richiesta é formata dai seguenti campi:

- `version`: versione del protocollo, attualmente é `V1`, permette di rendere il protocollo flessibile a cambiamenti futuri, **campo obbligatorio**
- `operation`: operazione da eseguire, meggiori dettagli nella sezione `operazioni` **campo obbligatorio**
- `collectionName`: nome della collezione su cui eseguire l'operazione **campo obbligatorio solo per le operazioni che modificano delle collezioni**
- `document`: stringa in formato JSON rappresentante il documento su cui eseguire l'operazione **campo obbligatorio solo per l'operazione `insert`**
- `filter`: stringa in formato JSON rappresentante una lista di filtri, maggiori dettagli nella sezione `filtri` **campo opzionale**
- `update`: stringa in formato JSON rappresentante il documento con i valori da aggiornare, maggiori dettagli nella sezione `aggiornamento` **campo obbligatorio solo per l'operazione `update`**

#### Operazioni

- `create`: creazione di una collezione
- `delete`: eliminazione di uno o piú documenti all'interno di una collezione
- `drop`: eliminazione di una collezione
- `find`: ricerca di uno o piú documenti all'interno di una collezione che soddisfano dei filtri
- `insert`: inserimento di un documento all'interno di una collezione
- `ping`: ping verso il database (non necessita di parametri, utile per verificare la connessione)
- `update`: aggiornamento di uno o piú documenti all'interno di una collezione

##### CREATE:

Permette la creazione di una collezione. Struttura di una richiesta:

```json
{
  "version": "V1",
  "operation": "CREATE",
  "collectionName": "nomeCollezione"
}
```

##### DELETE:

Permette l'eliminazione di uno o piú documenti all'interno di una collezione. Struttura di una richiesta:

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

Permette l'eliminazione di una collezione. Struttura di una richiesta:

```json
{
  "version": "V1",
  "operation": "DROP",
  "collectionName": "nomeCollezione"
}
```

##### FIND:

Permette di ottenere i documenti che soddisfano parametri della ricerca (filtri). Struttura di una richiesta:

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

Permette di inserire un documento in una collezione. Struttura di una richiesta:

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

Permette di determinare lo stato della connesione con il Database. Struttura di una richiesta:

```json
{
  "version": "V1",
  "operation": "PING"
}
```

##### UPDATE:

Permette di aggiornare un documento in una collezione. Struttura di una richiesta:

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
    {"key": "chiave", "value": "nuovo valore", "valueType": "String"},
    {"key": "chiave2", "value": "nuovo valore", "valueType": "String"}
  ]
}
```

> nota: se una chiave di update non é presente nel documento, verrá creata.

#### Filtri

I filtri consentono di selezionare dei documenti all'interno di una collezione al fine di eseguire operazioni su di essi.

##### Campi

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

#### Aggiornamento

L'aggiornamento consente di modificare i valori di un documento all'interno di una collezione.

##### Campi

- `key`: chiave del documento
- `value`: valore da aggiornare
- `valueType`: tipo del valore, i valori possibili sono:
  - `String`
  - `Number`
  - `Boolean`
  - `Null`

> nota: se una chiave non é presente nel documento, verrá creata con il valore specificato.

#### Risposta

Una risposta del Database è una stringa che, una volta parsata, prevede sempre la seguente struttura:

- `isError`: indica se si è verificato un errore nell'esecuzione della richiesta
- `errorKind`: tipo di error verificatori, aiuta il client a capire il tipo di errore
- `message`: informazioni aggiuntive sulla risposta, può contenere informazioni sul messaggio di errore o di successo
- `affectedDocumentsCount`: numero di documenti che hanno subito modifiche (update o delete)
- `detectedDocumentsCount`: numero di documenti che soddifano le condizioni dei filtri
- `retrievedDocuments`: array di documenti restituiti in formato JSON
