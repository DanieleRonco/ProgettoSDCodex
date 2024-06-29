# Progetto Sistemi Distribuiti 2023-2024 - TCP

introduzione
il protocollo si basa sull'invio di stringhe (protocollo testuale) tra un server web, che opera come client, ed un database, che opera come server.
DOMANDA: ti sei ispirato ad un protocollo nello specifico?

server web (client)
il server web instaura una connessione con il database tramite una socket sulla porta 3030.
1 il server manda al database una stringa che rappresenta il comando da eseguire nel server sul database.
2 il server ricede dal database una stringa contenente la risposta alla richiesta precedente.

database (server)
il database rimane in ascolto sulla porta 3030 al fine di soddisfare le richieste del server
1. riceve una stringa rappresentante un comando costituito da diversi argomenti

richiesta
la richiesta viene composta lato server per mezzo di un QeuryBuilder

operazioni
create - creazione
delete - ... così per tutte lo so, vedi se manca qualcosa
drop
find
insert
ping - ping verso il database
update

CREATE
DOMANDA: consente la creazione di un documento o di una collezione?
DOMANDA: struttura?

DELETE
DOMANDA: consente la eliminazione di un documento o di una collezione?
DOMANDA: struttura?

DROP
DOMANDA: cosa consente? eliminazione di una collezione?
DOMANDA: struttura?

FIND
consente di ottenere i documenti che soddisfano parametri della ricerca
find
collection
{query}

INSERT
DOMANDA: cosa consente? di inserire un documento in una collezione? sappi che l'ho utilizzata così
DOMANDA: struttura?

PING
consente di determinare lo stato della connesione con il database
DOMANDA: struttura?

UPDATE
consente di aggiornare un Documento in una Collezione
struttura
update
collection
{query}
{"chiave":"nuovo valore", "chiave2":"nuovo valore"} = {aggiornamento} che si struttura in quella maniera

operazioni logiche: come vengono tradotte all'interno delle query. 
DOMANDA: in quale punto deve essere posta l'operazione logica? fammi un esempio. immagino tra "chiave" e "valore".
: -> ==
!: -> !=
>: -> >
>=: -> >=

keywords: parole chiave ammesse
false, true, null, date, OR

OR logico - come viene tradotto l'or logico
L'or logico giene tradotto nella seguente maniera

esempio di struttura:
un esempio di struttura è il seguente. Ispirandosi al formato JSON, prevede coppie chiave-valore. la chiave è sempre una stringa, mentre il valore può essere
una stringa, un intero, un valore booleano true o false, null, una data. Le virgole ',' rappresentano AND logici mentre l'OR logico viene tradotto nella seguente maniera 'OR["chiave":"valore", ...]'
{"chiave":"valore", "chiave":123, "chiave": false|true, "chiave":null, "chiave"!:null, "chiave":date("10-20-23"), OR:["chiave":"valore", ...]}

DOMANDA
volevo dire che il server può creare query utilizzando un query builder e il database le può parsare. Non avendolo fatto, non so come mettere giù la frase. Te che sai come fare, butta giù due righe, poi vedo io di sistemare la forma e tutto. è solo l'idea ecco.

risposta
la risposta del database è una stringa che, una volta parsata, prevede sempre la seguente struttura:
    isError indica se si è verificato un errore nell'esecuzione della richiesta
    
    message 
    DOMANDA: cosa rappresenta?
    
    affectedDocumentsCount numero di documenti interessati dall'operazione 
    
    detectedDocumentsCount numero di documenti rilevati, che soddifano la condizione di ricerca espressa
    
    retrievedDocuments array di documenti restituiti

----------------------------------------------------------------------------------------------------------------------------------------------------------------------

Documentare qui il protocollo su socket TCP che espone il database.

Come scritto anche nel documento di consegna del progetto, si ha completa libertà su come implementare il protoccolo e i comandi del database. Alcuni suggerimenti sono:

1. Progettare un protocollo testuale (tipo HTTP), è più semplice da implementare anche se meno efficiente.
2. Dare un'occhiata al protocollo di [Redis](https://redis.io/docs/reference/protocol-spec/). Si può prendere ispirazione anche solo in alcuni punti.

Di solito il protoccolo e i comandi del database sono due cose diverse. Tuttavia per il progetto, per evitare troppa complessità, si può documentare insieme il protocollo e i comandi implementati nel database.

La documentazione può variare molto in base al tipo di protocollo che si vuole costruire:

* Se è un protocollo testuale simile a quello di Redis, è necessario indicare il formato delle richieste e delle risposte, sia dei comandi sia dei dati.

* Se è un protocollo binario, è necessario specificare bene il formato di ogni pacchetto per le richieste e per le risposte, come vengono codificati i comandi e i dati.