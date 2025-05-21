#ifndef PARTITA_H
#define PARTITA_H

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <unistd.h>
#include <stdbool.h>

#include "config.h"

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
    char campo[3][3]; // Campo di gioco 3x3
} partita_t;

char *partitaParser(char *buffer, partita_t *partite, int socketCreatore, int sockets[], int numero_sockets);
char *getPartiteInAttesa(partita_t *partite, int socketCreatore);
void inizializza_partite(partita_t *partite);
char *putCreaPartita(partita_t *partite, char *nomeGiocatore, int socketCreatore, int sockets[], int numero_sockets);
char *putMove(partita_t *partite, char *attributi, int sockets[], int numero_sockets);
bool controllaVittoria(char campo[3][3], char simbolo);
bool controllaPareggio(char campo[3][3]);
void send_in_broadcast(int sockets[], int numero_sockets, char *message);

#endif 