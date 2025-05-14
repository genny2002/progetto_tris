package com.client.Connessione;

import java.net.Socket;

import com.client.HomePageController;
import com.client.MatchController;
import com.client.Model.Richiesta;

import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class NotificaListener implements Runnable {
    private Socket socket;
    private MatchController matchController;
    private HomePageController homePageController;
    private Text statoRichiesta;
    public HBox riga;

    public NotificaListener(Socket socket, MatchController matchController) {
        this.socket = socket;
        this.matchController = matchController;
        this.riga = riga;
    }

    public NotificaListener(Socket socket, HomePageController homePageController, Text statoRichiesta)
    {
        this.socket = socket;
        this.homePageController = homePageController;
        this.statoRichiesta = statoRichiesta;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String message;

            while ((message = reader.readLine()) != null) {
                // Solo i messaggi di notifica di partecipazione
                if (message.startsWith("Richiesta di partecipazione")) {
                    int separatorIndex = message.lastIndexOf(":");
                    if (separatorIndex > 0) {
                        String testo = message.substring(0, separatorIndex);
                        String id = message.substring(separatorIndex + 1);

                        matchController.addNuovaRichiesta(new Richiesta(id, testo, "in attesa"));
                    }
                }else{
                    System.out.println(message);
                    if(matchController!=null){
                        matchController.deleteNotifica();
                    }
                    
                    if(message.contains("rifiutata") && homePageController != null){
                        homePageController.setRichiestaRifiutata(statoRichiesta);
                    }
                }
                // Altri messaggi vengono ignorati da questo thread
            }
        } catch (IOException e) {
            System.err.println("Errore nel listener delle notifiche: " + e.getMessage());
        }
    }
}