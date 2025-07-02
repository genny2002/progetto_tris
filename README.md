# Progetto tris
## Descrizione generale
Questo progetto implementa un'applicazione client-server containerizzata utilizzando Docker. Il server è scritto in C e il client è un'applicazione JavaFX.

## Struttura del Progetto
```
.
├── docker-compose.yml
├── Dockerfile.server
├── Dockerfile.client
├── server/
│   ├── main.c
│   ├── config.h
│   ├── partita.c
│   ├── partita.h
│   ├── richiesta.c
│   ├── richiesta.h
│   ├── coda.c
│   └── coda.h
└── client/demo
    ├── src/main
    │   ├── java/com/client
    │   │   ├── Connessione
    │   │   │   ├── Connessione.java
    │   │   │   └── NotificaListener.java
    │   │   ├── Model
    │   │   │   ├── Partita.java
    │   │   │   └── Richiesta.java
    │   │   ├── App.java
    │   │   ├── LoginController.java
    │   │   └── MainController.java
    │   └── resources/com/client
    │       ├── login.fxml
    │       └── main.fxml
    ├──  target/demo-1.0-SNAPSHOT-jar-with-dependencies.jar
    └── pom.xml
```

## Prerequisiti
- Docker
- Docker Compose
- Sistema operativo Linux con supporto X11 (per il client grafico)
- File con il codice sorgente

## Esecuzione del progetto
1) Estrarre i file scaricati
2) Aprire il terminale
3) Aprire la directory con i file usando il comando cd
4) Eseguire il seguente comando:
   ```
   docker build --no-cache -f Dockerfile.server -t progetto_tris-server .
   ```
5) Eseguire il seguente comando:
   ```
   docker build --no-cache -f Dockerfile.client -t progetto_tris-client .
   ```
6) Eseguire il seguente comando:
   ```
   xhost +local:docker
   ```
7) Avviare il server e un client con il seguente comando:
   ```
   docker-compose up --build
   ```
8) Avviare un nuovo client con il seguente comando in un nuovo terminale:
   ```
   docker run -it --rm \
    -e DISPLAY=$DISPLAY \
    -v /tmp/.X11-unix:/tmp/.X11-unix:rw \
    --device /dev/dri \
    --network host \
    progetto_tris-client
   ```

## Autori 
- Fabrizio Quaranta (N86004300)
- Gennaro Nappp (N86004294)
