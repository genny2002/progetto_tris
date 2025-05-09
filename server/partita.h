#ifndef PARTITA_H
#define PARTITA_H

#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#include "config.h"

typedef struct {
    int id;
    char stato[20]; 
    // nuova_creazione: la partita è stata creata ma non inizializzata, 
    //in_attesa: la partita è stata iniazilizzata e il creatore sta aspettando un giocatore, 
    //in_gioco: i giocatori stanno giocando
    //terminata: la partita è terminata e può essere rinizializzata
    char nomeCreatore[50];
    char nomeGiocatore[50];
    int socketCreatore;
    int socketGiocatore;
} partita_t;

char *partitaParser(char *buffer, partita_t *partite, int socketCreatore);
char *getPartiteInAttesa(partita_t *partite);
void inizializza_partite(partita_t *partite);
char *putCreaPartita(partita_t *partite, char *nomeGiocatore, int socketCreatore);

#endif 