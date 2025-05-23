#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <pthread.h>

#include "config.h"
#include "partita.h"
#include "richiesta.h"
#include "coda.h"

partita_t partite[MAX_PARTITE];  // IL SERVER SUPPORTA AL MASSIMO 10 PARTITE
coda_t richieste;
int sockets[MAX_SOCKETS]; // Array per memorizzare i socket dei client
int numero_sockets = 0; // Contatore per il numero di socket aperti

void* process(void * ptr);

int main() {
    printf("Server started...\n");
    inizializza_partite(partite);
    inizializzaCoda(&richieste);

    int sockfd, new_socket; //'sockfd' è la socket del server in cui riceve richieste di connessione, 'new_socket' è la socket del client con la quale il server comunica con il client
    int opt = 1;
    struct sockaddr_in servaddr, cliaddr;
    pthread_t thread_client;

    // Creazione del socket
    if ((sockfd = socket(AF_INET, SOCK_STREAM, 0)) < 0) {
        perror("socket creation failed");
        exit(EXIT_FAILURE);
    }

    // Configurazione delle opzioni del socket
    if (setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR, &opt, sizeof(opt))) {
        perror("setsockopt");
        exit(EXIT_FAILURE);
    }

    // Inizializzazione delle strutture sockaddr_in
    memset(&servaddr, 0, sizeof(servaddr));
    memset(&cliaddr, 0, sizeof(cliaddr));
    servaddr.sin_family = AF_INET;
    servaddr.sin_addr.s_addr = INADDR_ANY;
    servaddr.sin_port = htons(PORT);

    // Binding del socket
    if (bind(sockfd, (const struct sockaddr *)&servaddr, sizeof(servaddr)) < 0) {
        perror("bind failed");
        exit(EXIT_FAILURE);
    }

    // Ascolto delle connessioni
    if (listen(sockfd, 3) < 0) {
        perror("listen");
        exit(EXIT_FAILURE);
    }

    int addrlen = sizeof(cliaddr);

    while(1){   
        // Accettazione di una connessione
        if ((new_socket = accept(sockfd, (struct sockaddr*)&cliaddr, (socklen_t*)&addrlen)) < 0) {
            perror("accept");
            exit(EXIT_FAILURE);
        }

        // Allocazione dinamica per passare il descrittore del socket al thread
        int *client_socket_ptr = malloc(sizeof(int));
        *client_socket_ptr = new_socket;

        sockets[numero_sockets] = new_socket;
        numero_sockets++;

        // Creo un thread per gestire la connessione
        if (pthread_create(&thread_client, NULL, process, (void *)client_socket_ptr) < 0) perror("Could not create thread"), exit(EXIT_FAILURE);
    }

    return 0;
}
    
void* process(void * ptr){
    char buffer[1024] = {0};
    int socket = *((int *) ptr);
    free(ptr);

    while (1) {
        memset(buffer, 0, sizeof(buffer));
        int n = read(socket, buffer, 1024);
        if (n <= 0) break; // Client ha chiuso la connessione

        printf("Received: \"%s\"\n", buffer);

        const char *msg = "";
        
        if(strstr(buffer, "Partita")!=NULL){
            msg=partitaParser(buffer, partite, socket, sockets, numero_sockets, &richieste);
        }else if(strstr(buffer, "Richiesta")!=NULL){
            msg=richiestaParser(buffer, partite, socket, &richieste);
        }else{
            break;
        }
    }
    close(socket);
    
    return NULL;
}