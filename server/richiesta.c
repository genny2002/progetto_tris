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
    char *response;
    
    
    //char attr1[50], attr2[50];

    // Dividere la stringa in parti (ignora l'entità)
    if (sscanf(buffer, "%*[^:]:%49[^:]:%99[^\n]", nomeFunzione, attributi) < 1) {
        return "Formato input non valido\n";
    }

    if(strcmp(nomeFunzione, "putSendRequest") == 0) response=putSendRequest(partite, attributi, socketGiocatore, richieste);
    else if (strcmp(nomeFunzione, "deleteRifiutaRichiesta") == 0) response = deleteRifiutaRichiesta(attributi, richieste);
    else return "Comando non riconosciuto\n";

    // Invio del messaggio al client
    /*send(socketGiocatore, response, strlen(response), 0);
    printf("Message sent: %s alla socket %d\n", response, socketGiocatore);

    // Chiusura dei socket
    close(socketGiocatore);*/

    return response;
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
    nuovaRichiesta.idRichiesta = nuovaRichiesta.socketCreatore*10+nuovaRichiesta.socketGiocatore; // ID della richiesta
    enqueue(richieste, nuovaRichiesta);
    notificaProprietario(nuovaRichiesta.socketCreatore, nomeGiocatore, nuovaRichiesta.idRichiesta);
    //togliere la richiesta dalla coda (fare l'estrazione dalla coda)
    char *response = malloc(256);

    sprintf(response, "Richiesta inviata al proprietario della partita %d\n", idPartita);
    send(socketGiocatore, response, strlen(response), 0);

    return response;
}

void notificaProprietario(int socketProprietario, char* nomeGiocatore, int idRichiesta) { 
    char messaggio[256]; // Usa un array locale
    
    sprintf(messaggio, "Richiesta di partecipazione. Il giocatore %s ha chiesto di partecipare alla tua partita:%d\n", nomeGiocatore, idRichiesta);
    send(socketProprietario, messaggio, strlen(messaggio), 0);
}

char *deleteRifiutaRichiesta(char *attributi, coda_t *richieste) {
    int idRichiesta;
    richiesta_t richiesta;
    int foundIndex = -1;
    char *response = malloc(256);

    // Parsing con ',' come separatore e un terzo attributo
    if (sscanf(attributi, "%d", &idRichiesta) != 1) {
        sprintf(response, "Formato input non valido\n");
    }

    // Cerca la richiesta nella coda
    for (int i = 0; i < richieste->size; i++) {
        int index = (richieste->front + i) % MAX_QUEUE_SIZE; // Posizione effettiva
        if (richieste->queue[index].idRichiesta == idRichiesta) {
            foundIndex = index;
            richiesta = richieste->queue[index];
            break;
        }
    }

    if (foundIndex == -1) {
        sprintf(response, "Richiesta non trovata\n");
    }

    // Rimuovi la richiesta spostando gli elementi successivi
    for (int i = foundIndex; i != richieste->rear; i = (i + 1) % MAX_QUEUE_SIZE) {
        int nextIndex = (i + 1) % MAX_QUEUE_SIZE;
        richieste->queue[i] = richieste->queue[nextIndex];
    }

    // Aggiorna la posizione della coda
    richieste->rear = (richieste->rear - 1 + MAX_QUEUE_SIZE) % MAX_QUEUE_SIZE;
    richieste->size--;

    sprintf(response, "la richiesta %d è stata rifiutata:%d\n", idRichiesta, idRichiesta);

    send(richiesta.socketCreatore, response, strlen(response), 0);
    printf("Message sent: %s alla socket %d\n", response, richiesta.socketCreatore);

    // Chiusura dei socket
    //close(richiesta.socketGiocatore);
    return response;
}