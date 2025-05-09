#include "richiesta.h"

// Funzione per inizializzare la coda
void inizializzaCoda(coda_t *coda) {
    coda->front = 0;
    coda->rear = -1;
    coda->size = 0;
}

// Funzione per verificare se la coda è piena
bool isCodaPiena(coda_t *coda) {
    return coda->size == MAX_QUEUE_SIZE;
}

// Funzione per verificare se la coda è vuota
bool isCodaVuota(coda_t *coda) {
    return coda->size == 0;
}

// Funzione per inserire un elemento nella coda
bool enqueue(coda_t *coda, richiesta_t richiesta) {
    if (isCodaPiena(coda)) {
        printf("Errore: la coda è piena!\n");
        return false;
    }
    coda->rear = (coda->rear + 1) % MAX_QUEUE_SIZE;
    coda->queue[coda->rear] = richiesta;
    coda->size++;
    return true;
}

// Funzione per rimuovere un elemento dalla coda
bool dequeue(coda_t *coda, richiesta_t *richiesta) {
    if (isCodaVuota(coda)) {
        printf("Errore: la coda è vuota!\n");
        return false;
    }
    *richiesta = coda->queue[coda->front];
    coda->front = (coda->front + 1) % MAX_QUEUE_SIZE;
    coda->size--;
    return true;
}

char *richiestaParser(char *buffer, partita_t *partite, int socketGiocatore, coda_t *richieste) {
    char nomeFunzione[50]={0}; 
    char attributi[150]={0};
    
    
    //char attr1[50], attr2[50];

    // Dividere la stringa in parti (ignora l'entità)
    if (sscanf(buffer, "%*[^:]:%49[^:]:%99[^\n]", nomeFunzione, attributi) < 1) {
        return "Formato input non valido\n";
    }

    if(strcmp(nomeFunzione, "putSendRequest") == 0) return putSendRequest(partite, attributi, socketGiocatore, richieste);
    else return "Comando non riconosciuto\n\0";
}

char *putSendRequest(partita_t *partite, char *attributi, int socketGiocatore, coda_t *richieste) {
    int idPartita;
    char nomeGiocatore[50];
    char nomeCreatore[50];

    // Parsing con ',' come separatore e un terzo attributo
    if (sscanf(attributi, "%d,%49[^,],%49s", &idPartita, nomeGiocatore, nomeCreatore) != 3) {
        return "Formato input non valido\n";
    }
    
    if (idPartita < 0 || idPartita >= MAX_PARTITE) {
        return "ID partita non valido\n";
    }

    if (strcmp(partite[idPartita].stato, "in_attesa") != 0) {
        return "Partita non disponibile per richieste\n";
    }

    //richiesta_t nuovaRichiesta = {idPartita, nomeCreatore, nomeGiocatore, partite[idPartita].socketCreatore, socketGiocatore}; //inserire le socket correttamente    
    richiesta_t nuovaRichiesta;
    nuovaRichiesta.idPartita = idPartita;
    strcpy(nuovaRichiesta.nomeCreatore, nomeCreatore);
    strcpy(nuovaRichiesta.nomeGiocatore, nomeGiocatore);
    nuovaRichiesta.socketCreatore = partite[idPartita].socketCreatore;
    nuovaRichiesta.socketGiocatore = socketGiocatore;
    enqueue(richieste, nuovaRichiesta);
    notificaProprietario(partite[idPartita].socketCreatore, nomeGiocatore);

    char *response = malloc(256);

    sprintf(response, "Richiesta inviata al proprietario della partita %d\n", idPartita);

    return response;
}

void notificaProprietario(int socketProprietario, char* nomeGiocatore) {
    char messaggio[256]; // Usa un array locale
    
    sprintf(messaggio, "Il giocatore %s ha chiesto di partecipare alla tua partita", nomeGiocatore);
    send(socketProprietario, messaggio, strlen(messaggio), 0);
}