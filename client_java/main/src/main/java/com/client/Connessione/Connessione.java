package com.client.Connessione;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import com.client.Model.Partita;

public class Connessione {
    private static final int MAX_RESPONSE_SIZE = 1024;

    public static Socket createSocket() {
        Socket socket = null;
        String serverAddress = "127.0.0.1";
        int port = 5050;

        try {
            // Crea il socket e connettiti al server
            socket = new Socket(serverAddress, port);
            System.out.println("Connessione al server riuscita: " + serverAddress + ":" + port);
        } catch (UnknownHostException e) {
            System.err.println("Host sconosciuto: " + serverAddress);

            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Errore di connessione al server: " + serverAddress + ":" + port);

            e.printStackTrace();
            System.exit(1);
        }

        return socket;
    }

    public static String getClientSocket(String request) {
        Socket clientSocket = Connessione.createSocket();
        Connessione.sendRequest(clientSocket, request);
        String response = Connessione.readResponse(clientSocket);

        try {
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Errore durante la chiusura del socket");
            e.printStackTrace();
        }
        return response;
    }

    public static void sendRequest(Socket socket, String request) {
        try {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(request.getBytes());
            outputStream.flush(); // Assicurati che i dati vengano inviati immediatamente
        } catch (IOException e) {
            System.err.println("Errore durante l'invio della richiesta");

            e.printStackTrace();
            System.exit(1);
        }
    }

    public static String readResponse(Socket socket) {
        StringBuilder response = new StringBuilder();
        byte[] buffer = new byte[MAX_RESPONSE_SIZE];

        try {
            InputStream inputStream = socket.getInputStream();
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                response.append(new String(buffer, 0, bytesRead));
                // Termina il loop se il messaggio è completo (opzionale, dipende dal protocollo)
                if (bytesRead < MAX_RESPONSE_SIZE) break;
            }
        } catch (IOException e) {
            System.err.println("Errore durante la lettura della risposta");

            e.printStackTrace();
            System.exit(1);
        }

        return response.toString();
    }   
}
