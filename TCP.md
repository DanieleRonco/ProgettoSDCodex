# Progetto Sistemi Distribuiti 2023-2024 - TCP

introduzione
il protocollo si basa sull'invio di stringhe tra un server web, che opera come client, ed un database, che opera come server.
a che modello fa riferimento?

server web (client)
il server web instaura una connessione con il database tramite una socket sulla porta 3030.
il server manda al database una stringa che rappresenta il comando da eseguire nel server sul database.
il server ricede dal database una stringa contenente la risposta alla richiesta precedente.

database (server)
il database rimane in ascolto sulla porta 3030 al fine di soddisfare le richieste del server

richiesta
la richiesta viene composta lato server per mezzo di un QeuryBuilder

operazioni
create - creazione
delete - ... così per tutte lo so, vedi se manca qualcosa
drop
find
insert
ping
update

operazioni logiche: come vengono tradotte
: -> ==
!: -> !=
>: -> >
>=: -> >=

keywords: parole chiave
false, true, null, date, OR

esempio di struttura:
{"chiave":"valore", "chiave":123, "chiave": false|true, "chiave":null, "chiave"!:null, "chiave":date("10-20-23"), OR:["chiave":"valore", ...]}

struttura aggiornamento
update
collection
{query}
{"chiave":"nuovo valore", "chiave2":"nuovo valore"}

dimmi se altre operazioni hanno strtture diverse


risposta
la risposta, parsata da stringa, è strututrata nella seguente maniera
    isError indica se si è verificato un errore nell'esecuzione della richiesta
    message ?
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