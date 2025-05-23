#ifndef RICHIESTA_H
#define RICHIESTA_H

#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <sys/socket.h>
#include <unistd.h>
#include <stdlib.h>
#include <time.h>

#include "config.h"
#include "partita.h"
#include "coda.h"

char *richiestaParser(char *buffer, partita_t *partite, int socketGiocatore, coda_t *richieste);
char *putSendRequest(partita_t *partite, char *attributi, int socketGiocatore, coda_t *richieste);
void notificaProprietario(int socketProprietario, char* nomeGiocatore, int idRichiesta);
char *deleteRifiutaRichiesta(char *attributi, coda_t *richieste);
char *putAccettaRichiesta(char *attributi, coda_t *richieste, partita_t *partite);
void deleteEliminaRichiesteByPartitaId(coda_t *richieste, int idPartita);

#endif 