package com.client.Connessione;

import java.net.Socket;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class NotificaListener implements Runnable {
    private Socket socket;

    public NotificaListener(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            System.out.println("In attesa di notifiche dal server... sulla socket: " + socket.getPort());
            InputStream inputStream = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String message;

            // Leggi notifiche dal server in un loop
            while ((message = reader.readLine()) != null) {
                System.out.println("Notifica ricevuta: " + message);

                // Qui puoi gestire la notifica ricevuta
            }
        } catch (IOException e) {
            System.err.println("Errore nel listener delle notifiche: " + e.getMessage());
        }
    }
}