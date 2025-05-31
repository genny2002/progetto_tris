#include "richiesta.h"

char *richiestaParser(char *buffer, partita_t *partite, int socketGiocatore, coda_t *richieste) {
    char nomeFunzione[50]={0}; 
    char attributi[150]={0};
    char *response;
    
    if (sscanf(buffer, "%*[^:]:%49[^:]:%99[^\n]", nomeFunzione, attributi) < 1) {
        return "Formato input non valido\n";
    }

    if(strcmp(nomeFunzione, "putSendRequest") == 0) response=putSendRequest(partite, attributi, socketGiocatore, richieste);
    else if (strcmp(nomeFunzione, "deleteRifiutaRichiesta") == 0) response = deleteRifiutaRichiesta(attributi, richieste);
    else if (strcmp(nomeFunzione, "putAccettaRichiesta")==0) response = putAccettaRichiesta(attributi, richieste, partite);
    else return "Comando non riconosciuto\n";

    return response;
}

char *putSendRequest(partita_t *partite, char *attributi, int socketGiocatore, coda_t *richieste) {
    int idPartita;
    char nomeGiocatore[50];
    char nomeCreatore[50];

    if (sscanf(attributi, "%d,%49[^,],%49s", &idPartita, nomeGiocatore, nomeCreatore) != 3) {
        return "Formato input non valido\n";
    }
    
    if (idPartita < 0 || idPartita >= MAX_PARTITE) {
        return "ID partita non valido\n";
    }

    if (strcmp(partite[idPartita].stato, "in_attesa") != 0) {
        return "Partita non disponibile per richieste\n";
    }
    
    richiesta_t nuovaRichiesta;

    nuovaRichiesta.idPartita = idPartita;
    strcpy(nuovaRichiesta.nomeCreatore, nomeCreatore);
    strcpy(nuovaRichiesta.nomeGiocatore, nomeGiocatore);
    nuovaRichiesta.socketCreatore = partite[idPartita].socketCreatore;
    nuovaRichiesta.socketGiocatore = socketGiocatore;
    nuovaRichiesta.idRichiesta = nuovaRichiesta.socketCreatore*10+nuovaRichiesta.socketGiocatore;
    enqueue(richieste, nuovaRichiesta);
    notificaProprietario(nuovaRichiesta.socketCreatore, nomeGiocatore, nuovaRichiesta.idRichiesta);

    char *response = malloc(256);

    sprintf(response, "Richiesta inviata");
    send(socketGiocatore, response, strlen(response), 0);

    return response;
}

void notificaProprietario(int socketProprietario, char* nomeGiocatore, int idRichiesta) { 
    char messaggio[256];
    
    sprintf(messaggio, "Richiesta di partecipazione. Il giocatore %s ha chiesto di partecipare alla tua partita:%d\n", nomeGiocatore, idRichiesta);
    send(socketProprietario, messaggio, strlen(messaggio), 0);
}

char *deleteRifiutaRichiesta(char *attributi, coda_t *richieste) {
    int idRichiesta;
    richiesta_t richiesta;
    int foundIndex = -1;
    char *response = malloc(256);

    if (sscanf(attributi, "%d", &idRichiesta) != 1) {
        sprintf(response, "Formato input non valido\n");
    }

    // Cerca la richiesta nella coda
    for (int i = 0; i < richieste->size; i++) {
        int index = (richieste->front + i) % MAX_QUEUE_SIZE;
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

    send(richiesta.socketGiocatore, response, strlen(response), 0);
    printf("Message sent: %s alla socket %d\n", response, richiesta.socketGiocatore);

    return response;
}

char *putAccettaRichiesta(char *attributi, coda_t *richieste, partita_t *partite) {
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
        int index = (richieste->front + i) % MAX_QUEUE_SIZE;
        if (richieste->queue[index].idRichiesta == idRichiesta) {
            foundIndex = index;
            richiesta = richieste->queue[index];
            break;
        }
    }

    if (foundIndex == -1) {
        sprintf(response, "Richiesta non trovata\n");
    }else{
        srand(time(NULL));

        int idPartita = richiesta.idPartita;

        strcpy(partite[idPartita].stato, "in_corso");
        partite[idPartita].socketGiocatore = richiesta.socketGiocatore;
        strcpy(partite[idPartita].nomeGiocatore, richiesta.nomeGiocatore);

        int randomValue = rand() % 2;

        if(randomValue == 0){
            partite[idPartita].simboloGiocatore = 'X';
            partite[idPartita].simboloCreatore = 'O';
        }else{
            partite[idPartita].simboloGiocatore = 'O';
            partite[idPartita].simboloCreatore = 'X';
        }

    }

    deleteEliminaRichiesteByPartitaId(richieste, richiesta.idPartita);

    printf("simboloCreatore: %c\n", partite[richiesta.idPartita].simboloCreatore); 
    printf("simboloGiocatore: %c\n", partite[richiesta.idPartita].simboloGiocatore); 
    
    sprintf(response, "la richiesta %d è stata accettata:%d:%s:%c:%d\n", idRichiesta, idRichiesta, richiesta.nomeGiocatore, partite[richiesta.idPartita].simboloCreatore, richiesta.idPartita);
    send(richiesta.socketCreatore, response, strlen(response), 0);
    printf("Message sent: %s alla socket %d\n", response, richiesta.socketCreatore);
    
    sprintf(response, "la richiesta %d è stata accettata:%d:%s:%c:%d\n", idRichiesta, idRichiesta, richiesta.nomeCreatore, partite[richiesta.idPartita].simboloGiocatore, richiesta.idPartita);
    send(richiesta.socketGiocatore, response, strlen(response), 0);
    printf("Message sent: %s alla socket %d\n", response, richiesta.socketGiocatore);

    return response;
}

void deleteEliminaRichiesteByPartitaId(coda_t *richieste, int idPartita) {
    int newSize = 0;
    int front = richieste->front;
    richiesta_t nuovaCoda[MAX_QUEUE_SIZE];

    for (int i = 0; i < richieste->size; i++) {
        int idx = (front + i) % MAX_QUEUE_SIZE;
        if (richieste->queue[idx].idPartita != idPartita) {
            nuovaCoda[newSize++] = richieste->queue[idx];
        }
    }

    for (int i = 0; i < newSize; i++) {
        richieste->queue[i] = nuovaCoda[i];
    }

    richieste->front = 0;
    richieste->rear = newSize > 0 ? newSize - 1 : -1;
    richieste->size = newSize;
}

void freeRichieste(coda_t *richieste, int socket) {
    int newSize = 0;
    int front = richieste->front;
    richiesta_t nuovaCoda[MAX_QUEUE_SIZE];

    for (int i = 0; i < richieste->size; i++) {
        int idx = (front + i) % MAX_QUEUE_SIZE;
        // Mantieni solo le richieste che NON hanno il socket come creatore o giocatore
        if (richieste->queue[idx].socketCreatore != socket && richieste->queue[idx].socketGiocatore != socket) {
            nuovaCoda[newSize++] = richieste->queue[idx];
        }
    }

    for (int i = 0; i < newSize; i++) {
        richieste->queue[i] = nuovaCoda[i];
    }

    richieste->front = 0;
    richieste->rear = newSize > 0 ? newSize - 1 : -1;
    richieste->size = newSize;
}