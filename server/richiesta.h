#ifndef RICHIESTA_H
#define RICHIESTA_H

#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <sys/socket.h>

#include "config.h"
#include "partita.h"

typedef struct {
    int idPartita;
    char nomeCreatore[50];
    char nomeGiocatore[50];
    int socketCreatore;
    int socketGiocatore;
} richiesta_t;

typedef struct {
    richiesta_t queue[MAX_QUEUE_SIZE];
    int front;
    int rear;
    int size;
} coda_t;

void inizializzaCoda(coda_t *coda);
bool isCodaPiena(coda_t *coda);
bool isCodaVuota(coda_t *coda);
bool enqueue(coda_t *coda, richiesta_t richiesta);
bool dequeue(coda_t *coda, richiesta_t *richiesta);
char *richiestaParser(char *buffer, partita_t *partite, int socketGiocatore, coda_t *richieste);
char *putSendRequest(partita_t *partite, char *attributi, int socketGiocatore, coda_t *richieste);
void notificaProprietario(int socketProprietario, char* nomeGiocatore);

#endif 