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

    public void setStatoRichiesta(Text statoRichiesta) {
        this.statoRichiesta = statoRichiesta;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String message;

            while (!Thread.currentThread().isInterrupted() && (message = reader.readLine()) != null) {
                if(message.startsWith("partita creata")){
                    String[] parts = message.split(":");
                    String idPartita = parts[1].trim();
                    System.out.println("ID ricevuto: " + idPartita);

                    if(homePageController != null){
                        homePageController.idNuovaPartita = idPartita;
                        //homePageController.setPartitaCreata(idPartita, nomeProprietario, simboloCreatore, simboloGiocatore);
                    }
                }
                else
                
                if(message.startsWith("Broadcast")){
                    System.out.println(message);
                }
                else if(message.startsWith("Partita terminata")){
                    matchController.setPartitaTerminata(message);
                }
                else if(message.startsWith("Mossa eseguita")){
                    int row = (message.charAt(15)) - '0';
                    int column = message.charAt(17) - '0';

                    matchController.setMossa(row, column);
                }
                else if (message.startsWith("Richiesta di partecipazione")) {
                    int separatorIndex = message.lastIndexOf(":");
                    if (separatorIndex > 0) {
                        String testo = message.substring(0, separatorIndex);
                        String id = message.substring(separatorIndex + 1);

                        matchController.addNuovaRichiesta(new Richiesta(id, testo, "in attesa"));
                    }
                }else{
                    System.out.println(message);
                    
                    if(message.contains("rifiutata")){
                        if(matchController!=null){
                            matchController.deleteNotifica();
                        }
                    
                        if(homePageController != null){
                            homePageController.setRichiestaRifiutata(statoRichiesta);
                        }
                    }else if(message.contains("accettata")){
                        String[] parts = message.split(":");
                        String idRichiesta = parts[1].trim();
                        String nomeProprietario = parts[2].trim();
                        String nomeGiocatore = parts[3].trim();
                        String simboloCreatore= parts[4].trim();
                        String simboloGiocatore= parts[5].trim();
                        String idPartita = parts[6].trim();

                        if(homePageController != null){
                            homePageController.nomeProprietario = nomeProprietario;
                            homePageController.setRichiestaAccettata(statoRichiesta, simboloGiocatore, idPartita);
                        }

                        if(matchController != null){
                            matchController.setNomeAvversario(nomeGiocatore);
                            matchController.setAvvisiLabel();
                            matchController.setSimboloFromNotificaListener(simboloCreatore);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Errore nel listener delle notifiche: " + e.getMessage());
        }
    }
}