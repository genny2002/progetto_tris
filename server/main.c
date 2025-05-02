#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>

int main() {
    printf("Server started...\n");
    int sockfd, new_socket;
    int opt = 1;
    char buffer[1024] = {0};
    const char *hello = "Hello from server";
    struct sockaddr_in servaddr, cliaddr;

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

    // Accettazione di una connessione
    if ((new_socket = accept(sockfd, (struct sockaddr*)&cliaddr, (socklen_t*)&addrlen)) < 0) {
        perror("accept");
        exit(EXIT_FAILURE);
    }

    // Lettura dei dati dal client
    int n = read(new_socket, buffer, 1024);
    if (n < 0) {
        perror("read");
        close(new_socket);
        close(sockfd);
        exit(EXIT_FAILURE);
    }

    printf("Received: \"%s\"\n", buffer);

    // Invio del messaggio al client
    send(new_socket, hello, strlen(hello), 0);
    printf("Hello message sent.\n");

    // Chiusura dei socket
    close(new_socket);
    close(sockfd);

    return 0;
}