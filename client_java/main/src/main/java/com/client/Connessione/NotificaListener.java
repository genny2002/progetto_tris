package com.client.Connessione;

import java.net.Socket;

import com.client.MatchController;
import com.client.Model.Richiesta;

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
            InputStream inputStream = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String message;

            // Leggi notifiche dal server in un loop
            while ((message = reader.readLine()) != null) {
                int separatorIndex = message.indexOf(":");
                String testo = message.substring(0, separatorIndex); // Parte prima di ':'
                String id = message.substring(separatorIndex + 1);

                MatchController.addNuovaRichiesta(new Richiesta(id, testo, "in attesa"));
            }
        } catch (IOException e) {
            System.err.println("Errore nel listener delle notifiche: " + e.getMessage());
        }
    }
}