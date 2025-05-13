package com.client.Connessione;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import com.client.Model.Partita;

public class Connessione {
    private static final int MAX_RESPONSE_SIZE = 1024;
    public Socket clientSocket;

    public Connessione() {
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

        this.clientSocket=socket;
    }

    public String getClientSocket(String request) {
        this.sendRequest(this.clientSocket, request);

        String response = this.readResponse(clientSocket);

        return response;
    }

    public void sendRequest(Socket socket, String request) {
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

    /*public  String readResponse(Socket socket) {
        System.out.println("Ciao sono in readResponse");
        StringBuilder response = new StringBuilder();
        byte[] buffer = new byte[MAX_RESPONSE_SIZE];

        try {
            InputStream inputStream = socket.getInputStream();
            int bytesRead;
            System.out.println("Ho letto lo stream della socket");
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
    }*/

    public String readResponse(Socket socket) {
        StringBuilder response = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line = reader.readLine(); // Reads until '\n'

            if (line != null) {
                response.append(line);
            }
        } catch (IOException e) {
            System.err.println("Errore durante la lettura della risposta");
            e.printStackTrace();
            System.exit(1);
        }
        return response.toString();
    }

    public void closeSocket() {
        try {
            this.clientSocket.close();
        } catch (IOException e) {
            System.err.println("Errore durante la chiusura del socket");
            e.printStackTrace();
        }
    }
}
