import { Socket } from 'net';

const HOST = '127.0.0.1';
const PORT = 8080;
const message = 'Hello from client';

// Creazione del client
const client = new Socket();

// Connessione al server
client.connect(PORT, HOST, () => {
    console.log(`Connected to server at ${HOST}:${PORT}`);
    
    // Invio del messaggio al server
    client.write(message);
    console.log(`Message sent to server: ${message}`);
});

// Gestione della risposta dal server
client.on('data', (data) => {
    console.log(`Message received from server: ${data.toString()}`);
    
    // Chiudi la connessione dopo aver ricevuto la risposta
    client.destroy();
});

// Gestione degli errori
client.on('error', (err) => {
    console.error(`Error: ${err.message}`);
});

// Gestione della chiusura della connessione
client.on('close', () => {
    console.log('Connection closed');
});