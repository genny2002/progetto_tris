#ifndef PARTITA_H
#define PARTITA_H

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <unistd.h>
#include <stdbool.h>
#include <time.h>

#include "config.h"
#include "coda.h"

typedef struct {
    int id;
    char stato[20]; 
    // nuova_creazione: la partita è stata creata ma non inizializzata, 
    //in_attesa: la partita è stata iniazilizzata e il creatore sta aspettando un giocatore, 
    //in_corso: i giocatori stanno giocando
    //terminata: la partita è terminata e può essere rinizializzata
    char nomeCreatore[50];
    char nomeGiocatore[50];
    int socketCreatore;
    int socketGiocatore;
    char simboloCreatore;
    char simboloGiocatore;
    char campo[3][3];
    int rematchCreatore;
    int rematchGiocatore;
} partita_t;

char *partitaParser(char *buffer, partita_t *partite, int socketCreatore, int sockets[], int numero_sockets, coda_t *richieste);
char *getPartiteInAttesa(partita_t *partite, int socketCreatore);
void inizializza_partite(partita_t *partite);
char *putCreaPartita(partita_t *partite, char *nomeGiocatore, int socketCreatore, int sockets[], int numero_sockets);
char *putMove(partita_t *partite, char *attributi, int sockets[], int numero_sockets);
bool controllaVittoria(char campo[3][3], char simbolo);
bool controllaPareggio(char campo[3][3]);
void send_in_broadcast(int sockets[], int numero_sockets, char *message, int socket1, int socket2);
char *putRematch(char *attributi, partita_t *partite, coda_t *richieste);
void eliminaRichiestaByPartitaId(int idPartita, coda_t* richieste);
void freePartite(partita_t *partite, int socket);

#endif 