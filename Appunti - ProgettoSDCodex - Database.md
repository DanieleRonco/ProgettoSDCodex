### Documento

Ogni documento possiede un `_id` univoco (UUID).

### Collection

Una collection è un insieme di documenti. Non vi sono vincoli sul tipo di documenti che possono essere memorizzati all'interno della collection, ma a livello logico si tende a mantenere una coerenza tra gli elementi di una collection.

### Definizione del Protocollo

Il protocollo è di tipo testuale in formato JSON, al fine di mantenere una comunicazione semplice e developer friendly.

GET
utenti
OR:["email":"pippo","nome":"ronco franco"],"cognome":"ronco","eta":18

operazioni:
`: -> ==`
`!: -> !=`
`>: -> >`
`>=: -> >=`

keywords:
false, true, null, date, OR

```
{
    "chiave":"valore",
    "chiave":123,
    "chiave": false|true,
    "chiave":null,
    "chiave"!:null,
    "chiave":date("10-20-23"),
    OR:["chiave":"valore", ...]
}
```

```json
{
  "OP": "UPDATE"
}
```

UPDATE
<collection>
{query}
{"chiave":"nuovo valore", "chiave2":"nuovo valore"}