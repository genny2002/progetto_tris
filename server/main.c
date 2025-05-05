#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <pthread.h>

#include "partita.h"

void* process(void * ptr);

int main() {
    printf("Server started...\n");
    int sockfd, new_socket; //'sockfd' è la socket del server in cui riceve richieste di connessione, 'new_socket' è la socket del client con la quale il server comunica con il client
    int opt = 1;
    //char buffer[1024] = {0};
    //const char *msg = "id:1,nomeCreatore:Giocatore1/id:2,nomeCreatore:Giocatore2/id:3,nomeCreatore:Giocatore3";
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
    servaddr.sin_port = htons(5050);

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

        // Creo un thread per gestire la connessione
        if (pthread_create(&thread_client, NULL, process, (void *)client_socket_ptr) < 0) perror("Could not create thread"), exit(EXIT_FAILURE);
    }

    return 0;
}
    
void* process(void * ptr){
    const char *msg = "";
    char buffer[1024] = {0};
    int socket = *((int *) ptr);
    free(ptr); // Liberazione della memoria allocata per il descrittore del socket

    /*char request[MAX_REQUEST_SIZE];
    char response[MAX_RESPONSE_SIZE];

    memset(request, 0, MAX_REQUEST_SIZE);
    memset(response, 0, MAX_RESPONSE_SIZE);*/


    // Lettura dei dati dal client
    int n = read(socket, buffer, 1024);
    if (n < 0) {
        perror("read");
        close(socket);
        //close(sockfd);
        exit(EXIT_FAILURE);
    }

    printf("Received: \"%s\"\n", buffer);

    if(strstr(buffer, "Partita")!=NULL){
        msg=partitaParser(buffer);
        printf("msg: %s\n", msg);
    }

    // Invio del messaggio al client
    send(socket, msg, strlen(msg), 0);
    printf("Message sent.\n");

    // Chiusura dei socket
    close(socket);
    //close(sockfd);
}