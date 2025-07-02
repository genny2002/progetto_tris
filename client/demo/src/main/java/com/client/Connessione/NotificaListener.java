package com.client.Connessione;

import java.net.Socket;
import java.util.ArrayList;

import com.client.MainController;
import com.client.Model.Partita;
import com.client.Model.Richiesta;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class NotificaListener implements Runnable {
    private Socket socket;
    private MainController mainController;
    private Text statoRichiesta;
    public HBox riga;

    public NotificaListener(Socket socket, MainController mainController) {
        this.socket = socket;
        this.mainController = mainController;
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
                if(message.startsWith("logout:")){
                    String nomeGiocatore = message.substring("logout:".length());
                    mainController.updatePartiteInAttesa(nomeGiocatore);
                    mainController.updateRichieste(nomeGiocatore);
                }else if(message.startsWith("partiteInAttesa:")){
                    String partiteString = message.substring("partiteInAttesa:".length());
                    if (partiteString == null || partiteString.isEmpty()) {
                        System.out.println("Nessuna partita in attesa.");
                        mainController.partite = new ArrayList<>();
                    }else{
                        mainController.partite = Partita.convertToObjects(partiteString);
                        Platform.runLater(() -> mainController.showPartiteInAttesa(mainController.partite));
                    }
                }
                else if(message.contains("abbandonato")){
                    Platform.runLater(() -> {
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Partita terminata");
                        alert.setHeaderText("Partita terminata");
                        alert.setContentText("Il tuo avversario ha abbandonato la partita.");
                        alert.setOnCloseRequest(event -> {
                            if (mainController != null) {
                                mainController.setHomePageControllerVisible();
                                mainController.setRematch();
                            }
                        });
                        alert.showAndWait();
                    });
                }
                else if(message.startsWith("Richiesta Rematch")){
                    final String finalMessage = message;
                    
                    Platform.runLater(() -> {
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Broadcast");
                        alert.setHeaderText("Messaggio di sistema");
                        alert.setContentText(finalMessage);
                        alert.showAndWait();
                    });
                }
                else if(message.startsWith("rematch accettato")){
                    String[] parts = message.split(":");
                    String simboloGiocatoreAttuale = parts[1].trim();
                    if(mainController != null){
                        mainController.setRematch();

                        if(simboloGiocatoreAttuale.equals("X")){
                            mainController.simboloAvversario="O";
                            mainController.simboloGiocatore = "X";
                        }else{
                            mainController.simboloAvversario="X";
                            mainController.simboloGiocatore = "O";
                        }

                        Platform.runLater(() -> {
                            try {
                                mainController.setRichiestaAccettata(statoRichiesta, simboloGiocatoreAttuale);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });   
                    }
                }
                else if(message.startsWith("rematch rifiutato")){
                    final String finalMessage = message;
                    
                    Platform.runLater(() -> {
                        if (mainController != null) {
                            mainController.closePartitaTerminataAlert();
                        }

                        Alert alert2 = new Alert(AlertType.INFORMATION);
                        alert2.setTitle("Broadcast");
                        alert2.setHeaderText("Messaggio di sistema");
                        alert2.setContentText(finalMessage);
                        alert2.showAndWait();

                        if(mainController != null){
                            mainController.setHomePageControllerVisible();
                            mainController.setRematch();
                        }
                    });
                }
                else if(message.startsWith("partita creata")){
                    String[] parts = message.split(":");
                    String idPartita = parts[1].trim();

                    if(mainController != null){
                        mainController.idNuovaPartita = idPartita;
                    }
                }
                else if(message.startsWith("Broadcast")){
                    final String finalMessage = message;
                    
                    Platform.runLater(() -> {
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Broadcast");
                        alert.setHeaderText("Messaggio di sistema");
                        alert.setContentText(finalMessage.substring("Broadcast:".length()).trim());
                        alert.showAndWait();
                    });

                    if(message.contains("ha creato una nuova partita") || message.contains("hanno iniziato la partita")){
                        mainController.initPartiteInAttesa();
                    }
                }
                else if(message.startsWith("Partita terminata")){
                    mainController.setPartitaTerminata(message);
                }
                else if(message.startsWith("Mossa eseguita")){
                    int row = (message.charAt(15)) - '0';
                    int column = message.charAt(17) - '0';

                    System.out.println("Mossa eseguita: " + row + ", " + column);

                    mainController.setMossa(row, column);
                }
                else if (message.startsWith("Richiesta di partecipazione")) {
                    int separatorIndex = message.lastIndexOf(":");
                    
                    if (separatorIndex > 0) {
                        String testo = message.substring(0, separatorIndex);
                        String id = message.substring(separatorIndex + 1);

                        mainController.addNuovaRichiesta(new Richiesta(id, testo, "in attesa"));
                    }
                }else{                    
                    if(message.contains("rifiutata")){
                        if(mainController!=null){
                            mainController.deleteNotifica();
                        }

                        if(mainController != null && statoRichiesta != null){
                            mainController.setRichiestaRifiutata(statoRichiesta);
                        }
                    }else if(message.contains("accettata")){
                        String[] parts = message.split(":");
                        String idRichiesta = parts[1].trim();
                        String nomeAvversario = parts[2].trim();
                        String simboloGiocatoreAttuale= parts[3].trim();
                        String idPartita = parts[4].trim();

                        if(mainController != null){
                            mainController.nomeAvversario = nomeAvversario;
                            mainController.idPartita = idPartita;
                            if(simboloGiocatoreAttuale.equals("X")){
                                mainController.simboloAvversario="O";
                                mainController.simboloGiocatore = "X";
                            }else{
                                mainController.simboloAvversario="X";
                                mainController.simboloGiocatore = "O";
                            }

                            Platform.runLater(() -> {
                                try {
                                    mainController.setRichiestaAccettata(statoRichiesta, simboloGiocatoreAttuale);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });   
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Errore nel listener delle notifiche: " + e.getMessage());
        }
    }
}