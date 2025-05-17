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
    else if (strcmp(nomeFunzione, "putMove") == 0) response=putMove(partite, attributi);
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

    strcat(partiteInAttesa, "\n"); // Aggiungi un terminatore di riga alla fine

    // Invio del messaggio al client
    send(socketCreatore, partiteInAttesa, strlen(partiteInAttesa), 0);
    printf("Message sent: %s alla socket %d\n", partiteInAttesa, socketCreatore);

    // Chiusura dei socket
    
    //close(socketCreatore);
    return partiteInAttesa;
}

char *putCreaPartita(partita_t *partite, char *nomeGiocatore, int socketCreatore) {
    for (int i = 0; i < MAX_PARTITE; i++) {
        if(strcmp(partite[i].stato, "nuova_creazione") == 0 || strcmp(partite[i].stato, "terminata") == 0) {
            strcpy(partite[i].nomeCreatore, nomeGiocatore);
            strcpy(partite[i].stato, "in_attesa");
            partite[i].socketCreatore = socketCreatore;

            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    partite[i].campo[j][k] = ' '; // Inizializza il campo di gioco
                }
            }

            char *response = malloc(1024); // Allocazione dinamica
            
            sprintf(response, "%d\n", partite[i].id);
            send(socketCreatore, response, strlen(response), 0);
            printf("Message sent: %s alla socket %d\n", response, socketCreatore);

            return "Partita creata con successo\n";
        }
    }

    return "Nessuna partita disponibile\n";
}

char *putMove(partita_t *partite, char *attributi) {
    int idPartita;
    int row, col;
    char simbolo;
    char *response = malloc(256);

    // Parsing degli attributi
    if (sscanf(attributi, "%d,%d,%c,%d", &row, &col, &simbolo, &idPartita) != 4) {
        return "Formato input non valido\n";
    }

    // Verifica se la partita esiste
    if (idPartita < 0 || idPartita >= MAX_PARTITE || strcmp(partite[idPartita].stato, "in_gioco") != 0) {
        return "Partita non valida o non in corso\n";
    }

    // Verifica se la mossa è valida
    if (row < 0 || row >= 3 || col < 0 || col >= 3 || partite[idPartita].campo[row][col] != ' ') {
        return "Mossa non valida\n";
    }

    // Esegui la mossa
    partite[idPartita].campo[row][col] = simbolo;

    if(simbolo == partite[idPartita].simboloCreatore){
        sprintf(response, "Mossa eseguita:%d:%d\n", row, col);
        send(partite[idPartita].socketGiocatore, response, strlen(response), 0);
    }else{
        sprintf(response, "Mossa eseguita:%d:%d\n", row, col);
        send(partite[idPartita].socketCreatore, response, strlen(response), 0);
    }

    if (controllaVittoria(partite[idPartita].campo, simbolo)) {
        strcpy(partite[idPartita].stato, "terminata");

        if(simbolo == partite[idPartita].simboloCreatore){
            response="Partita terminata: Hai vinto!\n";
            send(partite[idPartita].socketCreatore, response, strlen(response), 0);
            response="Partita terminata: Hai perso!\n";
            send(partite[idPartita].socketGiocatore, response, strlen(response), 0);
        }else{
            response="Partita terminata: Hai vinto!\n";
            send(partite[idPartita].socketGiocatore, response, strlen(response), 0);
            response="Partita terminata: Hai perso!\n";
            send(partite[idPartita].socketCreatore, response, strlen(response), 0);
        }
    }else if (controllaPareggio(partite[idPartita].campo)) {
        strcpy(partite[idPartita].stato, "terminata");
        response="Partita terminata: Pareggio!\n";
        send(partite[idPartita].socketCreatore, response, strlen(response), 0);
        send(partite[idPartita].socketGiocatore, response, strlen(response), 0);
    }

    return "Mossa eseguita con successo\n";
}

bool controllaVittoria(char campo[3][3], char simbolo) {
    // Controlla righe e colonne
    for (int i = 0; i < 3; i++) {
        if ((campo[i][0] == simbolo && campo[i][1] == simbolo && campo[i][2] == simbolo) ||
            (campo[0][i] == simbolo && campo[1][i] == simbolo && campo[2][i] == simbolo)) {
            return true;
        }
    }

    // Controlla diagonali
    if ((campo[0][0] == simbolo && campo[1][1] == simbolo && campo[2][2] == simbolo) ||
        (campo[0][2] == simbolo && campo[1][1] == simbolo && campo[2][0] == simbolo)) {
        return true;
    }

    return false;
}

bool controllaPareggio(char campo[3][3]) {
    for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 3; j++) {
            if (campo[i][j] == ' ') {
                return false; // C'è ancora una mossa disponibile
            }
        }
    }
    return true; // Nessuna mossa disponibile, quindi è un pareggio
}