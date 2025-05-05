#include "partita.h"

char *partitaParser(char *buffer) {
    char nomeFunzione[50]={0}; 
    char attributi[100]={0};
    
    
    char attr1[50], attr2[50];

    // Dividere la stringa in parti (ignora l'entità)
    if (sscanf(buffer, "%*[^:]:%49[^:]:%99[^\n]", nomeFunzione, attributi) < 1) {
        return "Formato input non valido\n";
    }

    if(strcmp(nomeFunzione, "getPartiteInAttesa") == 0) return getPartiteInAttesa();
    else return "Comando non riconosciuto\n\0";
}

char *getPartiteInAttesa() {
    // Simulazione di una risposta
    return "id:1,nomeCreatore:Giocatore1/id:2,nomeCreatore:Giocatore2/id:3,nomeCreatore:Giocatore3";
}

