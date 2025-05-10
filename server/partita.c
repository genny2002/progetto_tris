#include "partita.h"

void inizializza_partite(partita_t *partite) {
    for (int i = 0; i < MAX_PARTITE; i++) {
        partite[i].id = i;
        strcpy(partite[i].stato, "nuova_creazione");
        strcpy(partite[i].nomeCreatore, "");
        strcpy(partite[i].nomeGiocatore, "");
        partite[i].socketCreatore = -1; // Inizializza il socket a -1 per indicare che non è connesso
        partite[i].socketGiocatore = -1; // Inizializza il socket a -1 per indicare che non è connesso
    }
}

char *partitaParser(char *buffer, partita_t *partite, int socketCreatore) {
    char nomeFunzione[50]={0}; 
    char attributi[150]={0};
    char *response;
    
    //char attr1[50], attr2[50];

    // Dividere la stringa in parti (ignora l'entità)
    if (sscanf(buffer, "%*[^:]:%49[^:]:%99[^\n]", nomeFunzione, attributi) < 1) {
        return "Formato input non valido\n";
    }

    if(strcmp(nomeFunzione, "getPartiteInAttesa") == 0) response=getPartiteInAttesa(partite, socketCreatore);
    else if(strcmp(nomeFunzione, "putCreaPartita") == 0 ) response=putCreaPartita(partite, attributi, socketCreatore);
    else return "Comando non riconosciuto\n\0";

    return response;
}

char *getPartiteInAttesa(partita_t *partite, int socketCreatore) {
    char *partiteInAttesa = malloc(1024); // Allocazione dinamica

    if (partiteInAttesa == NULL) {
        return "Errore di allocazione memoria\n";
    }

    partiteInAttesa[0] = '\0'; // Inizializza la stringa vuota

    for (int i = 0; i < MAX_PARTITE; i++) {
        if(strcmp(partite[i].stato, "in_attesa") == 0) {
            char buffer[100]; // Buffer temporaneo per concatenare i dati

            sprintf(buffer, "id:%d,nomeCreatore:%s/", i, partite[i].nomeCreatore);
            strcat(partiteInAttesa, buffer);
        }
    }

    // Invio del messaggio al client
    send(socketCreatore, partiteInAttesa, strlen(partiteInAttesa), 0);
    printf("Message sent: %s alla socket %d\n", partiteInAttesa, socketCreatore);

    // Chiusura dei socket
    close(socketCreatore);
    return partiteInAttesa;
}

char *putCreaPartita(partita_t *partite, char *nomeGiocatore, int socketCreatore) {
    for (int i = 0; i < MAX_PARTITE; i++) {
        if(strcmp(partite[i].stato, "nuova_creazione") == 0 || strcmp(partite[i].stato, "terminata") == 0) {
            strcpy(partite[i].nomeCreatore, nomeGiocatore);
            strcpy(partite[i].stato, "in_attesa");
            partite[i].socketCreatore = socketCreatore;

            char *response = malloc(1024); // Allocazione dinamica
            strcpy(response, "Partita creata con successo\n");
            send(socketCreatore, response, strlen(response), 0);
            printf("Message sent: %s alla socket %d\n", response, socketCreatore);

            return "Partita creata con successo\n";
        }
    }

    return "Nessuna partita disponibile\n";
}