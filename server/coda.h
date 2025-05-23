#ifndef CODA_H
#define CODA_H

#include <stdio.h>
#include <stdbool.h>

#include "config.h"

typedef struct {
    int idRichiesta;
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

#endif 