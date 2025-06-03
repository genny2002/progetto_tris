#include "partita.h"

void inizializza_partite(partita_t *partite) {
    for (int i = 0; i < MAX_PARTITE; i++) {
        partite[i].id = i;
        strcpy(partite[i].stato, "nuova_creazione");
        strcpy(partite[i].nomeCreatore, "");
        strcpy(partite[i].nomeGiocatore, "");
        partite[i].socketCreatore = -1;
        partite[i].socketGiocatore = -1;
    }
}

char *partitaParser(char *buffer, partita_t *partite, int socketCreatore, int sockets[], int numero_sockets, coda_t *richieste) {
    char nomeFunzione[50]={0}; 
    char attributi[150]={0};
    char *response;

    if (sscanf(buffer, "%*[^:]:%49[^:]:%99[^\n]", nomeFunzione, attributi) < 1) {
        return "Formato input non valido\n";
    }

    if(strcmp(nomeFunzione, "getPartiteInAttesa") == 0) response=getPartiteInAttesa(partite, socketCreatore);
    else if(strcmp(nomeFunzione, "putCreaPartita") == 0 ) response=putCreaPartita(partite, attributi, socketCreatore, sockets, numero_sockets);
    else if (strcmp(nomeFunzione, "putMove") == 0) response=putMove(partite, attributi, sockets, numero_sockets);
    else if (strcmp(nomeFunzione, "putRematch")==0) response = putRematch(attributi, partite, richieste);
    else return "Comando non riconosciuto\n\0";

    return response;
}

char *getPartiteInAttesa(partita_t *partite, int socketCreatore) {
    char *partiteInAttesa = malloc(1024);

    if (partiteInAttesa == NULL) {
        return "Errore di allocazione memoria\n";
    }

    partiteInAttesa[0] = '\0';

    for (int i = 0; i < MAX_PARTITE; i++) {
        if(strcmp(partite[i].stato, "in_attesa") == 0) {
            char buffer[100];

            sprintf(buffer, "id:%d,nomeCreatore:%s/", i, partite[i].nomeCreatore);
            strcat(partiteInAttesa, buffer);
        }
    }

    strcat(partiteInAttesa, "\n");
    send(socketCreatore, partiteInAttesa, strlen(partiteInAttesa), 0);
    printf("Message sent: %s alla socket %d\n", partiteInAttesa, socketCreatore);

    return partiteInAttesa;
}

char *putCreaPartita(partita_t *partite, char *nomeGiocatore, int socketCreatore, int sockets[], int numero_sockets) {
    for (int i = 0; i < MAX_PARTITE; i++) {
        if(strcmp(partite[i].stato, "nuova_creazione") == 0 || strcmp(partite[i].stato, "terminata") == 0) {
            strcpy(partite[i].nomeCreatore, nomeGiocatore);
            strcpy(partite[i].stato, "in_attesa");
            partite[i].socketCreatore = socketCreatore;
            partite[i].rematchCreatore = 0;
            partite[i].rematchGiocatore = 0;

            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    partite[i].campo[j][k] = ' ';
                }
            }

            char *response = malloc(1024);
            
            sprintf(response, "partita creata: %d\n", partite[i].id);
            send(socketCreatore, response, strlen(response), 0);
            printf("Message sent: %s alla socket %d\n", response, socketCreatore);

            sprintf(response, "%s ha creato una nuova partita, inviagli una richietsa per giocare\n", nomeGiocatore);
            send_in_broadcast(sockets, numero_sockets, response, socketCreatore, -1);

            return "Partita creata con successo\n";
        }
    }

    return "Nessuna partita disponibile\n";
}

char *putMove(partita_t *partite, char *attributi, int sockets[], int numero_sockets) {
    printf("sono in putMove\n");
    int idPartita;
    int row, col;
    char simbolo;
    char *response = malloc(1024);
    char *msg = malloc(1024);

    if (sscanf(attributi, "%d,%d,%c,%d", &row, &col, &simbolo, &idPartita) != 4) {
        return "Formato input non valido\n";
    }

    if (idPartita < 0 || idPartita >= MAX_PARTITE || strcmp(partite[idPartita].stato, "in_corso") != 0) {
        return "Partita non valida o non in corso\n";
    }

    if (row < 0 || row >= 3 || col < 0 || col >= 3 || partite[idPartita].campo[row][col] != ' ') {
        return "Mossa non valida\n";
    }

    partite[idPartita].campo[row][col] = simbolo;

    if(simbolo == partite[idPartita].simboloCreatore){
        printf("il creatore ha fatto una mossa\n");
        sprintf(response, "Mossa eseguita:%d:%d\n", row, col);
        send(partite[idPartita].socketGiocatore, response, strlen(response), 0);
        printf("Message sent: %s alla socket %d\n", response, partite[idPartita].socketGiocatore);
    }else{
        printf("il giocatore ha fatto una mossa\n");
        sprintf(response, "Mossa eseguita:%d:%d\n", row, col);
        send(partite[idPartita].socketCreatore, response, strlen(response), 0);
        printf("Message sent: %s alla socket %d\n", response, partite[idPartita].socketGiocatore);
    }

    if (controllaVittoria(partite[idPartita].campo, simbolo)) {
        strcpy(partite[idPartita].stato, "terminata");

        if(simbolo == partite[idPartita].simboloCreatore){
            response="Partita terminata: Hai vinto!\n";
            send(partite[idPartita].socketCreatore, response, strlen(response), 0);
            response="Partita terminata: Hai perso!\n";
            send(partite[idPartita].socketGiocatore, response, strlen(response), 0);
            sprintf(msg, "%s ha vinto contro %s\n", partite[idPartita].nomeCreatore, partite[idPartita].nomeGiocatore);
            send_in_broadcast(sockets, numero_sockets, msg, partite[idPartita].socketCreatore, partite[idPartita].socketGiocatore);
        }else{
            response="Partita terminata: Hai vinto!\n";
            send(partite[idPartita].socketGiocatore, response, strlen(response), 0);
            response="Partita terminata: Hai perso!\n";
            send(partite[idPartita].socketCreatore, response, strlen(response), 0);
            sprintf(msg, "%s ha vinto contro %s\n", partite[idPartita].nomeGiocatore, partite[idPartita].nomeCreatore);
            send_in_broadcast(sockets, numero_sockets, msg, partite[idPartita].socketCreatore, partite[idPartita].socketGiocatore);
        }
    }else if (controllaPareggio(partite[idPartita].campo)) {
        strcpy(partite[idPartita].stato, "terminata");
        response="Partita terminata: Pareggio!\n";
        send(partite[idPartita].socketCreatore, response, strlen(response), 0);
        send(partite[idPartita].socketGiocatore, response, strlen(response), 0);
        sprintf(msg, "Partita terminata: Pareggio tra %s e %s\n", partite[idPartita].nomeCreatore, partite[idPartita].nomeGiocatore);
        send_in_broadcast(sockets, numero_sockets, msg, partite[idPartita].socketCreatore, partite[idPartita].socketGiocatore);
    }

    return "Mossa eseguita con successo\n";
}

bool controllaVittoria(char campo[3][3], char simbolo) {
    for (int i = 0; i < 3; i++) {
        if ((campo[i][0] == simbolo && campo[i][1] == simbolo && campo[i][2] == simbolo) ||
            (campo[0][i] == simbolo && campo[1][i] == simbolo && campo[2][i] == simbolo)) {
            return true;
        }
    }

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
                return false;
            }
        }
    }
    return true;
}

void send_in_broadcast(int sockets[], int numero_sockets, char *message, int socket1, int socket2) {
    printf("sono in send_in_broadcast\n");

    char buffer[1024];
    snprintf(buffer, sizeof(buffer), "Broadcast:%s\n", message);

    for (int i = 0; i < numero_sockets; i++) {
        if(sockets[i] != socket1 && sockets[i] != socket2) {
            send(sockets[i], buffer, strlen(buffer), 0);
        }
        
    }

    printf("Broadcast message sent in broadcast: %s\n", buffer);
}

char *putRematch(char *attributi, partita_t *partite, coda_t *richieste) {
    int idPartita;
    int valoreRematch;
    char simbolo;
    char *response = malloc(1024);

    if (sscanf(attributi, "%d,%d,%c", &idPartita, &valoreRematch, &simbolo) != 3) {
        return "Formato input non valido\n";
    }

    if (simbolo == partite[idPartita].simboloCreatore) {
        partite[idPartita].rematchCreatore = valoreRematch;
    } else if (simbolo == partite[idPartita].simboloGiocatore) {
        partite[idPartita].rematchGiocatore = valoreRematch;
    }

    if(partite[idPartita].rematchCreatore==-1){
        sprintf(response, "rematch rifiutato\n");
        send(partite[idPartita].socketGiocatore, response, strlen(response), 0);
        printf("Message sent: %s alla socket %d\n", response, partite[idPartita].socketGiocatore);
        partite[idPartita].socketCreatore = -1;
        partite[idPartita].socketGiocatore = -1;
        //eliminaRichiestaByPartitaId(idPartita, richieste);
    }else if(partite[idPartita].rematchGiocatore==-1){
        sprintf(response, "rematch rifiutato\n");
        send(partite[idPartita].socketCreatore, response, strlen(response), 0);
        printf("Message sent: %s alla socket %d\n", response, partite[idPartita].socketCreatore);
        partite[idPartita].socketCreatore = -1;
        partite[idPartita].socketGiocatore = -1;
        //eliminaRichiestaByPartitaId(idPartita, richieste);
    }else if(partite[idPartita].rematchCreatore==1 && partite[idPartita].rematchGiocatore==1){
        srand(time(NULL));
        strcpy(partite[idPartita].stato, "in_corso");

        for (int j = 0; j < 3; j++) {
            for (int k = 0; k < 3; k++) {
                partite[idPartita].campo[j][k] = ' ';
            }
        }
        
        int randomValue = rand() % 2;

        if(randomValue == 0){
            partite[idPartita].simboloGiocatore = 'X';
            partite[idPartita].simboloCreatore = 'O';
        }else{
            partite[idPartita].simboloGiocatore = 'O';
            partite[idPartita].simboloCreatore = 'X';
        }

        sprintf(response, "rematch accettato:%c\n", partite[idPartita].simboloCreatore);
        send(partite[idPartita].socketCreatore, response, strlen(response), 0);
        printf("Message sent: %s alla socket %d\n", response, partite[idPartita].socketCreatore);

        sprintf(response, "rematch accettato:%c\n", partite[idPartita].simboloGiocatore);
        send(partite[idPartita].socketGiocatore, response, strlen(response), 0);
        printf("Message sent: %s alla socket %d\n", response, partite[idPartita].socketGiocatore);
    }else if(partite[idPartita].rematchCreatore==1){
        sprintf(response, "Richiesta Rematch: Il giocatore %s vuole giocare di nuovo\n", partite[idPartita].nomeCreatore);
        send(partite[idPartita].socketGiocatore, response, strlen(response), 0);
        printf("Message sent: %s alla socket %d\n", response, partite[idPartita].socketGiocatore);
    }else if(partite[idPartita].rematchGiocatore==1){
        sprintf(response, "Richiesta Rematch: Il giocatore %s vuole giocare di nuovo\n", partite[idPartita].nomeGiocatore);
        send(partite[idPartita].socketCreatore, response, strlen(response), 0);
        printf("Message sent: %s alla socket %d\n", response, partite[idPartita].socketCreatore);
    }  

    return response;
}

/*void eliminaRichiestaByPartitaId(int idPartita, coda_t* coda) {
    if (isCodaVuota(coda)) {
        printf("La coda è vuota. Nessuna richiesta da eliminare.\n");
        return;
    }

    coda_t tempQueue;
    inizializzaCoda(&tempQueue);

    int found = 0;

    // Esamina ogni elemento nella coda originale
    for (int i = 0; i < coda->size; i++) {
        int currentIndex = (coda->front + i) % MAX_QUEUE_SIZE;

        if (coda->queue[currentIndex].idPartita == idPartita) {
            found = 1;
        } else {
            tempQueue.queue[(tempQueue.rear + 1) % MAX_QUEUE_SIZE] = coda->queue[currentIndex];
            tempQueue.rear = (tempQueue.rear + 1) % MAX_QUEUE_SIZE;
            tempQueue.size++;
        }
    }

    if (found) {
        printf("Richiesta con idPartita %d eliminata.\n", idPartita);
    } else {
        printf("Richiesta con idPartita %d non trovata.\n", idPartita);
    }

    *coda = tempQueue;
}*/

void freePartite(partita_t *partite, int socket) {
    for (int i = 0; i < MAX_PARTITE; i++) {
        if (partite[i].socketCreatore == socket || partite[i].socketGiocatore == socket) {
            char *response = malloc(1024);
            
            response = "il tuo avversario ha abbandonato la partita\n";

            if (partite[i].socketCreatore == socket) {
                send(partite[i].socketGiocatore, response, strlen(response), 0);
            } else {
                send(partite[i].socketCreatore, response, strlen(response), 0);
            }

            strcpy(partite[i].stato, "nuova_creazione");
            strcpy(partite[i].nomeCreatore, "");
            strcpy(partite[i].nomeGiocatore, "");
            partite[i].socketCreatore = -1;
            partite[i].socketGiocatore = -1;
        }
    }
}