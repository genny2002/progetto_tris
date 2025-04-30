import { Socket } from 'net';

// Creazione del client
const client = new Socket();

export class Connessione {
    static createSocket() {
        const HOST = '127.0.0.1';
        const PORT = 8080;

        // Creazione del client
        const client = new Socket();

        // Connessione al server
        client.connect(PORT, HOST, () => {
            console.log(`Connected to server at ${HOST}:${PORT}`);
        });

        return client;
    }

    static sendMessage(client, message) {
        // Invio del messaggio al server
        client.write(message);
        console.log(`Message sent to server: ${message}`);
    }   
}