package com.client.Connessione;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Connessione {
    private static final int MAX_RESPONSE_SIZE = 1024; 
    public Socket clientSocket;

    public Connessione() {
        Socket socket = null;
        String serverAddress = System.getenv("SERVER_ADDRESS") != null ? 
                               System.getenv("SERVER_ADDRESS") : "server";
        int port = 5050;

        try {
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
            outputStream.flush();
        } catch (IOException e) {
            System.err.println("Errore durante l'invio della richiesta");

            e.printStackTrace();
            System.exit(1);
        }
    }

    public String readResponse(Socket socket) {
        StringBuilder response = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line = reader.readLine();

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
