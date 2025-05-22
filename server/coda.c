#include "coda.h"

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